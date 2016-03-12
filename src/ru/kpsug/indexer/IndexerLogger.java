package ru.kpsug.indexer;

public interface IndexerLogger {
	void writeLog(Integer page, boolean success);

	int getId();
}
