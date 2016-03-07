package ru.kpsug.indexer;

import java.util.concurrent.Future;

import ru.kpsug.db.Film;

public interface IndexerInserter {
	public static class IndexerInserterGetFailedException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = -8182410565495953176L;

	}

	void insertFilm(String id, IndexerLogger logger);

	Film getFilm(String id) throws IndexerInserterGetFailedException;

	Future<?> submitTask(Runnable task);
}
