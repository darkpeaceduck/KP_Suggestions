package ru.kpsug.indexer;

import java.io.PrintStream;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import ru.kpsug.db.Film;
import ru.kpsug.kp.KpParser;
import ru.kpsug.kp.KpParser.FuckupException;
import ru.kpsug.kp.PageLoader;
import ru.kpsug.kp.PageLoader.PageLoaderException;

public class IndexerWaiter implements Runnable {
	private final Condition indexerWaiterCondition;
	private final Condition whileFuckupThreadWaitingCondition;
	private final ReentrantLock lock = new ReentrantLock();
	private volatile boolean isFuckup = false;
	private volatile boolean off = false;
	private static final String defaultFilmId = "373314";
	private static final int fuckupExceptionSleepingTime = 30000;
	private PrintStream log;

	public IndexerWaiter(PrintStream log) {
		this.indexerWaiterCondition = lock.newCondition();
		this.whileFuckupThreadWaitingCondition = lock.newCondition();
		this.log = log;
	}

	public Condition getIndexerWaiterCondition() {
		return indexerWaiterCondition;
	}

	public Condition getWhileFuckupThreadWaitingCondition() {
		return whileFuckupThreadWaitingCondition;
	}

	public ReentrantLock getLock() {
		return lock;
	}

	public boolean isFuckup() {
		return isFuckup;
	}

	public void fuckupDetect() {
		lock.lock();
		try {
			log.println("fuckup detected");
			isFuckup = true;
			indexerWaiterCondition.signal();
		} finally {
			lock.unlock();
		}
	}

	public void off() {
		lock.lock();
		try {
			off = true;
			indexerWaiterCondition.signal();
		} finally {

			lock.unlock();
		}
	}

	public void fuckupCrashingLoop() {
		lock.lock();
		try {
			while (!off) {
				try {
					log.println("WAITER TEST CONNECTION => ");
					KpParser.parseFilm(PageLoader.loadFilm(String.valueOf(defaultFilmId)),
							PageLoader.loadFilmSuggestions(String.valueOf(defaultFilmId)));
				} catch (FuckupException e1) {
					log.println("NO, FUCKUP");
					try {
						Thread.sleep(fuckupExceptionSleepingTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				} catch (PageLoaderException e1) {
					log.println("NO, CANT CONNECT");
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				} catch (Exception excp) {
					break;
				}
				break;
			}
			log.println("YES");
			isFuckup = false;
			whileFuckupThreadWaitingCondition.signalAll();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void run() {
		lock.lock();
		try {
			while (!off) {
				try {
					while (!isFuckup && !off) {
						log.println("WAITER GOING TO SLEEP");
						indexerWaiterCondition.await();
					}
					log.println("WAITER WAKED UP");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (off) {
					break;
				}
				fuckupCrashingLoop();
			}
		} finally {
			lock.unlock();
		}
		log.println("WAITER OVER");
	}

	public void waitForTryer(String id) {
		lock.lock();
		try {
			try {
				while (isFuckup()) {
					log.println(id + " GOIND TO SLEEP");
					getWhileFuckupThreadWaitingCondition().await();
					log.println(id + " WAKED UP");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} finally {
			lock.unlock();
		}
	}
}
