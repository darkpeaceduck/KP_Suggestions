package ru.kpsug.indexer;

import java.io.PrintStream;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import ru.kpsug.db.DBOperator;
import ru.kpsug.db.DBOperator.FilmNotFoundException;
import ru.kpsug.db.Film;
import ru.kpsug.indexer.SingleThreadBfsTask.SingleThreadBfsTaskConstrArgs;
import ru.kpsug.indexer.SingleThreadSegmentTask.SingleThreadSegmentTaskConstrArgs;
import ru.kpsug.kp.KpParser;
import ru.kpsug.kp.KpParser.FuckupException;
import ru.kpsug.kp.KpPath;
import ru.kpsug.kp.PageLoader;
import ru.kpsug.kp.PageLoader.PageLoaderException;

public class Indexer {
	private static int pageStart;
	private static int pageEnd;
	private static int depth;
	private static DBOperator db;
	private static IndexerWaiter waiter;
	private static ExecutorService taskExecutor;
	private static String prefix;
	private static int taskSegmentLength;

	private enum IndexerMode {
		SINGLE_THREAD_BFS_EVERY_TASK_MODE, SINGLE_THREAD_EVERY_TASK_SEGMENT_MODE, MULTI_THREAD_BFS
	}

	private static IndexerMode mode;

	private static IndexerInserter inserter = new IndexerInserter() {
		@Override
		public void insertFilm(String id, IndexerLogger logger) {
			Film film = null;
			final String rootUrl = KpPath.makeFilmPrefix(prefix);

			try {
				film = db.selectFilm(String.valueOf(id));
				logger.writeLog(Integer.valueOf(id), true);
				return;
			} catch (SQLException e2) {
				e2.printStackTrace();
			} catch (FilmNotFoundException e2) {
			}

			while (true) {
				waiter.waitForTryer(String.valueOf(logger.getId()));
				try {
					String kpFilmPageUrl = KpPath.makeFilmFromUrlPrefixLink(rootUrl, String.valueOf(id));
					String kpFilmLikePageUrl = KpPath.makeFilmLikeUrlPrefixLink(rootUrl, String.valueOf(id));

					film = KpParser.parseFilm(PageLoader.loadUrl(kpFilmPageUrl), PageLoader.loadUrl(kpFilmLikePageUrl));
					db.insertFilm(film);
					logger.writeLog(Integer.valueOf(id), true);
					break;
				} catch (FuckupException | PageLoaderException e1) {
					e1.printStackTrace();
					waiter.fuckupDetect();
					continue;
				} catch (SQLException e) {
					e.printStackTrace();
					logger.writeLog(Integer.valueOf(id), false);
					break;
				} catch (Exception e1) {
					e1.printStackTrace();
					logger.writeLog(Integer.valueOf(id), false);
					break;
				}
			}
		}

		@Override
		public synchronized Film getFilm(String id) throws IndexerInserterGetFailedException {
			Film film;
			try {
				film = db.selectFilm(String.valueOf(id));
			} catch (SQLException e2) {
				e2.printStackTrace();
				throw new IndexerInserterGetFailedException();
			} catch (FilmNotFoundException e2) {
				throw new IndexerInserterGetFailedException();
			}
			return film;
		}

		@Override
		public Future<?> submitTask(Runnable task) {
			return taskExecutor.submit(task);
		}
	};

	public static void singleThreadBfsEveryTaskProc(PrintStream log) {
		if (mode != IndexerMode.SINGLE_THREAD_BFS_EVERY_TASK_MODE) {
			return;
		}

		for (int i = pageStart; i <= pageEnd; i++) {
			Runnable task = new SingleThreadBfsTask(
					new SingleThreadBfsTaskConstrArgs(0, inserter, i, depth, log));
			taskExecutor.execute(task);
		}
	}

	public static void singleThreadEveryTaskSegmentProc(PrintStream log) {
		if (mode != IndexerMode.SINGLE_THREAD_EVERY_TASK_SEGMENT_MODE) {
			return;
		}

		for (int i = pageStart; i <= pageEnd; i += taskSegmentLength) {
			Runnable task = new SingleThreadSegmentTask(new SingleThreadSegmentTaskConstrArgs(i - pageStart, i, Math.min(pageEnd, i + taskSegmentLength), log,
					inserter));
			taskExecutor.execute(task);
		}
	}

	public static void multiThreadBfsProc(PrintStream log) {
		if (mode != IndexerMode.MULTI_THREAD_BFS) {
			return;
		}
		new MultiThreadBfsTask(new MultiThreadBfsTask.MultiThreadBfsTaskConstrArgs(
				0, depth, log, pageStart, pageEnd, inserter)).run();
	}

	private static void initDb() throws SQLException {
		db = new DBOperator(null);
		db.connect();
	}

	private static void init(String[] args) throws SQLException, InvalidParameterException {
		if (args.length < 5) {
			System.out.println("wrong number of arguments");
			throw new InvalidParameterException();
		}

		pageStart = Integer.parseInt(args[0]);
		pageEnd = Integer.parseInt(args[1]);
		int threads = Integer.parseInt(args[2]);
		switch (args[3]) {
		case "normal":
			mode = IndexerMode.SINGLE_THREAD_EVERY_TASK_SEGMENT_MODE;
			taskSegmentLength = Integer.parseInt(args[4]);
			break;
		case "bfs":
			mode = IndexerMode.SINGLE_THREAD_BFS_EVERY_TASK_MODE;
			depth = Integer.parseInt(args[4]);
			break;
		case "multibfs":
			mode = IndexerMode.MULTI_THREAD_BFS;
			depth = Integer.parseInt(args[4]);
			break;
		}

		prefix = KpPath.getPrefix();

		if (args.length >= 7) {
			System.setProperty("socksProxyHost", args[5]); // set proxy server
			System.setProperty("socksProxyPort", args[6]);
		}

		try {
			initDb();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("failed init db");
			throw e;
		}
		System.out.println("success init db");

		waiter = new IndexerWaiter(System.out);
		threads = Math.min(threads, pageEnd - pageStart + 1);
		taskExecutor = Executors.newFixedThreadPool(threads);
		new Thread(waiter).start();
	}

	private static void finit() throws SQLException {
		taskExecutor.shutdown();
		try {
			taskExecutor.awaitTermination(1000, TimeUnit.DAYS);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		waiter.off();

		try {
			db.closeAll();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("couldnt close db");
			throw e;
		}
	}

	public static void main(String[] args) {

		try {
			init(args);
		} catch (Exception e) {
			System.out.println("init failed");
			return;
		}

		singleThreadEveryTaskSegmentProc(System.out);
		singleThreadBfsEveryTaskProc(System.out);
		multiThreadBfsProc(System.out);

		try {
			finit();
		} catch (Exception e) {
			System.out.println("finit failed");
			return;
		}

		System.out.println("OK");
	}
}
