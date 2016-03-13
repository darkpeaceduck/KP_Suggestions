package ru.kpsug.indexer;

import java.io.PrintStream;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import ru.kpsug.db.Film;
import ru.kpsug.indexer.IndexerInserter.IndexerInserterGetFailedException;
import ru.kpsug.indexer.SingleThreadSegmentTask.SingleThreadSegmentTaskConstrArgs;

public class MultiThreadBfsTask implements Runnable {
	private int pid;
	private int depth;
	private PrintStream log;
	private int page_start;
	private int page_end;
	private IndexerInserter inserter;
	
	public static class MultiThreadBfsTaskConstrArgs{
		public int pid;
		public int depth;
		public PrintStream log;
		public int page_start;
		public int page_end;
		public IndexerInserter inserter;
		public MultiThreadBfsTaskConstrArgs(int pid, int depth, PrintStream log, int page_start, int page_end,
				IndexerInserter inserter) {
			super();
			this.pid = pid;
			this.depth = depth;
			this.log = log;
			this.page_start = page_start;
			this.page_end = page_end;
			this.inserter = inserter;
		}
	}

	public MultiThreadBfsTask(MultiThreadBfsTaskConstrArgs args) {
		super();
		this.pid = args.pid;
		this.depth = args.depth;
		this.log = args.log;
		this.page_start = args.page_start;
		this.page_end = args.page_end;
		this.inserter = args.inserter;
	}

	private void runBfs() {
		Map<Integer, Integer> used = new HashMap<>();
		Deque<Integer> queue = new LinkedList<>();
		Deque<Future<?>> queueFut = new LinkedList<>();
		for (int i = page_start; i <= page_end; i++) {
			queue.push(i);
			queueFut.push(inserter.submitTask(new SingleThreadSegmentTask(new SingleThreadSegmentTaskConstrArgs(0, i, i, log, inserter))));
			used.put(i, 0);
		}
		while (!queue.isEmpty()) {
			Integer id = queue.getFirst();
			queue.pop();
			Future<?> fut = queueFut.getFirst();
			queueFut.pop();
			Integer h = used.get(id);
			try {
				fut.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				continue;
			}
			if (h < depth) {
				Film film;
				try {
					film = inserter.getFilm(String.valueOf(id));
				} catch (IndexerInserterGetFailedException e) {
					continue;
				}

				for (String go : film.getSuggestion_links()) {
					Integer goInt = Integer.parseInt(go);
					if (!used.containsKey(goInt)) {
						used.put(goInt, h + 1);
						queue.push(goInt);
						queueFut.push(inserter.submitTask(new SingleThreadSegmentTask(new SingleThreadSegmentTaskConstrArgs(0, goInt, goInt, log, inserter))));
					}
				}
			}
		}
	}

	@Override
	public void run() {
		log.println("MULTIBFS INDEXER " + String.valueOf(pid) + " STARTED");
		runBfs();
		log.println("MULTIBFS INDEXER " + String.valueOf(pid) + " FINISHED");
	}
}
