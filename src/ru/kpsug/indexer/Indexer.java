package ru.kpsug.indexer;

import java.util.concurrent.locks.ReentrantLock;

import ru.kpsug.db.DBOperator;
import ru.kpsug.db.Film;
import ru.kpsug.kp.KpParser;
import ru.kpsug.kp.KpParser.FuckupException;
import ru.kpsug.kp.PageLoader;
import ru.kpsug.kp.PageLoader.PageLoaderException;

public class Indexer implements Runnable {
    private int page_start;
    private int page_stop;
    private int id;
    private DBOperator db;
    private IndexerWaiter waiter;
    
    private void waitForTryer(){
        ReentrantLock lock = waiter.getLock();
        lock.lock();
        try{
            try {
                while(waiter.isFuckup()){
                    System.out.println(id + " GOIND TO SLEEP");
                    waiter.getWaiterWhileFuckup().await();
                    System.out.println(id + " WAKED UP");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }finally{
            lock.unlock();
        }
    }
    
    
    public Indexer(int id, DBOperator db,
            int page_start, int page_stop, IndexerWaiter waiter) {
        super();
        this.db = db;
        this.id = id;
        this.page_start = page_start;
        this.page_stop = page_stop;
        this.waiter = waiter;
    }

    private void writeLog(Integer id,Integer current_page, boolean success){
            System.out.append("PROGRESS OF INDEXER  " + String.valueOf(id)
                    + " " + String.valueOf(current_page) + "/"
                    + String.valueOf(page_stop) + " LAST WAS "
                    + (success ? "SUCCESS\n" : "FAILED\n"));
            System.out.flush();
    }
    
    @Override
    public void run() {
        System.out.println("STARTING INDEXER " + String.valueOf(id));
        for (int current_page = page_start; current_page <= page_stop; current_page++) {
            waitForTryer();
            boolean success = true;
            Film film = db.selectFilm(String.valueOf(current_page));
            if(film != null){
                writeLog(id, current_page, success);
                continue;
            }
            try {
                film = KpParser.parseFilm(PageLoader.loadFilm(String
                        .valueOf(current_page)), PageLoader
                        .loadFilmSuggestions(String.valueOf(current_page)));
            } catch (FuckupException | PageLoaderException e1) {
                e1.printStackTrace();
                waiter.fuckupDetect();
                success = false;
            } catch (Exception e1) {
                e1.printStackTrace();
                success = false;
            }
            if (success) {
                success = db.InsertFilm(film);
            }

            writeLog(id, current_page, success);
        }
        System.out.println("OVER INDEXER " + String.valueOf(id));
    }
}
