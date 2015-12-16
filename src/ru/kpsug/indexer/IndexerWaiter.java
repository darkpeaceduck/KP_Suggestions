package ru.kpsug.indexer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import ru.kpsug.db.Film;
import ru.kpsug.kp.KpParser;
import ru.kpsug.kp.PageLoader;
import ru.kpsug.kp.KpParser.FuckupException;
import ru.kpsug.kp.PageLoader.PageLoaderException;

public class IndexerWaiter implements Runnable {
    private final Condition waiterFukup;
    private final Condition waiterWhileFuckup;
    private final ReentrantLock lock = new ReentrantLock();
    private volatile boolean isFuckup = false;
    private volatile boolean over = false;
    private static final String defaultId = "373314";
    private static final int sleepingTime = 30000;

    public IndexerWaiter() {
        this.waiterFukup = lock.newCondition();
        this.waiterWhileFuckup = lock.newCondition();
    }

    public Condition getWaiterFukup() {
        return waiterFukup;
    }

    public Condition getWaiterWhileFuckup() {
        return waiterWhileFuckup;
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
            System.out.println("fuckup detected");
            isFuckup = true;
            waiterFukup.signal();
        } finally {
            lock.unlock();
        }
    }

    public void off() {
        lock.lock();
        try {
            over = true;
            waiterFukup.signal();
        } finally {

            lock.unlock();
        }
    }

    public void tryingLoop() {
        lock.lock();
        try {
            while (!over) {
                try {
                    System.out.println("WAITER TEST CONNECTION => ");
                    Film film = KpParser.parseFilm(PageLoader.loadFilm(String
                            .valueOf(defaultId)), PageLoader
                            .loadFilmSuggestions(String.valueOf(defaultId)));
                } catch (FuckupException e1) {
                    System.out.println("NO, FUCKUP");
                    try {
                        Thread.sleep(sleepingTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                } catch (PageLoaderException e1) {
                    System.out.println("NO, CANT CONNECT");
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
            System.out.println("YES");
            isFuckup = false;
            waiterWhileFuckup.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        lock.lock();
        try {
            while (!over) {
                try {
                    while (!isFuckup && !over) {
                        System.out.println("WAITER GOING TO SLEEP");
                        waiterFukup.await();
                    }
                    System.out.println("WAITER WAKED UP");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (over) {
                    break;
                }
                tryingLoop();
            }
        } finally {
            lock.unlock();
        }
        System.out.println("WAITER OVER");
    }
    
    public void waitForTryer(String id){
        lock.lock();
        try{
            try {
                while(isFuckup()){
                    System.out.println(id + " GOIND TO SLEEP");
                    getWaiterWhileFuckup().await();
                    System.out.println(id + " WAKED UP");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }finally{
            lock.unlock();
        }
    }
}
