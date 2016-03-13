package ru.kpsug.indexer;

import java.io.PrintStream;

public class SingleThreadSegmentTask implements Runnable, IndexerLogger {
	private int pid;
	private int page_start;
	private int page_stop;
	private int args;
	private PrintStream log;
	private IndexerInserter inserter;

	public static class SingleThreadSegmentTaskConstrArgs {
		public int pid;
		public int page_start;
		public int page_stop;
		public PrintStream log;
		public IndexerInserter inserter;
	}

	public SingleThreadSegmentTask(SingleThreadSegmentTaskConstrArgs args) {
		super();
		this.pid = args.pid;
		this.page_start = args.page_start;
		this.page_stop = args.page_stop;
		this.log = args.log;
		this.inserter = args.inserter;
	}

	@Override
	public void writeLog(Integer current_page, boolean success) {
		log.append("PROGRESS OF INDEXER  " + String.valueOf(pid) + " " + String.valueOf(current_page) + "/"
				+ String.valueOf(page_stop) + " LAST WAS " + (success ? "SUCCESS\n" : "FAILED\n"));
		log.flush();
	}

	@Override
	public void run() {
		log.println("STARTING INDEXER " + String.valueOf(pid));
		for (int current_page = page_start; current_page <= page_stop; current_page++) {
			inserter.insertFilm(String.valueOf(current_page), this);
		}
		log.println("OVER INDEXER " + String.valueOf(pid));
	}

	@Override
	public int getId() {
		return pid;
	}
}
