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
	
	private class BfsEntry{
		private Future<?> future;
		private int id;
		
		public BfsEntry(Future<?> future, int id) {
			super();
			this.future = future;
			this.id = id;
		}
		
		public Future<?> getFuture() {
			return future;
		}

		public int getId() {
			return id;
		}
	}

	private void runBfs() {
		Map<Integer, Integer> used = new HashMap<>();
		Deque<BfsEntry> queue = new LinkedList<>();
		for (int i = page_start; i <= page_end; i++) {
			SingleThreadSegmentTaskConstrArgs args = new SingleThreadSegmentTaskConstrArgs();
			args.pid = 0;
			args.page_start = i;
			args.page_stop = i;
			args.log = log;
			args.inserter = inserter;
			queue.push(new BfsEntry(inserter.submitTask(new SingleThreadSegmentTask(args)), 
					i));
			used.put(i, 0);
		}
		while (!queue.isEmpty()) {
			BfsEntry entry = queue.getFirst();
			queue.pop();
			Integer id = entry.getId();
			Future<?> fut = entry.getFuture();
			
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
						SingleThreadSegmentTaskConstrArgs args = new SingleThreadSegmentTaskConstrArgs();
						args.pid = 0;
						args.page_start = goInt;
						args.page_stop = goInt;
						args.log = log;
						args.inserter = inserter;
						queue.push(new BfsEntry(inserter.submitTask(new SingleThreadSegmentTask(args)),
								goInt));
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
