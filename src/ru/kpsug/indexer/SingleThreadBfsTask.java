package ru.kpsug.indexer;

import java.io.PrintStream;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import ru.kpsug.db.Film;
import ru.kpsug.indexer.IndexerInserter.IndexerInserterGetFailedException;

public class SingleThreadBfsTask implements Runnable, IndexerLogger {
	private int pid;
	private IndexerInserter inserter;
	private String start;
	private int depth;
	private PrintStream log;

	public SingleThreadBfsTask(int id, IndexerInserter inserter, int page, int depth, PrintStream log) {
		super();
		this.inserter = inserter;
		this.pid = id;
		this.start = String.valueOf(page);
		this.depth = depth;
		this.log = log;
	}

	@Override
	public void writeLog(Integer page, boolean success) {
		log.append("INDEXER  " + String.valueOf(pid) + " WITH PAGE = " + String.valueOf(page) + " OVER WITH "
				+ (success ? "SUCCESS\n" : "FAILED\n"));
		log.flush();
	}

	@Override
	public void run() {
		log.println("INDEXER " + String.valueOf(pid) + " STARTED");
		Map<String, Integer> used = new HashMap<>();
		Deque<String> queue = new LinkedList<>();

		used.put(start, 0);
		queue.add(start);

		inserter.insertFilm(start, this);
		while (!queue.isEmpty()) {
			String id = queue.getFirst();
			queue.pop();
			Integer h = used.get(id);
			if (h < depth) {
				Film film;
				try {
					film = inserter.getFilm(id);
				} catch (IndexerInserterGetFailedException e) {
					continue;
				}
				for (String go : film.getSuggestion_links()) {
					if (!used.containsKey(go)) {
						used.put(go, h + 1);
						queue.push(go);
						inserter.insertFilm(go, this);
					}
				}
			}
		}
		log.println("INDEXER " + String.valueOf(pid) + " FINISHED");
	}

	@Override
	public int getId() {
		return pid;
	}
}
