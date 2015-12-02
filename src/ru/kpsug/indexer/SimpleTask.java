package ru.kpsug.indexer;


import ru.kpsug.db.DBOperator;
import ru.kpsug.db.Film;
import ru.kpsug.kp.KpParser;
import ru.kpsug.kp.KpParser.FuckupException;
import ru.kpsug.kp.KpPath;
import ru.kpsug.kp.PageLoader;
import ru.kpsug.kp.PageLoader.PageLoaderException;

public class SimpleTask implements Runnable {
    private int page_start;
    private int page_stop;
    private int pid;
    private DBOperator db;
    private IndexerWaiter waiter;
    String rootUrl;
    
    public SimpleTask(int id, DBOperator db,
            int page_start, int page_stop, IndexerWaiter waiter, String rootUrl) {
        super();
        this.db = db;
        this.pid = id;
        this.page_start = page_start;
        this.page_stop = page_stop;
        this.waiter = waiter;
        this.rootUrl = rootUrl;
    }

    private void writeLog(Integer current_page, boolean success){
            System.out.append("PROGRESS OF INDEXER  " + String.valueOf(pid)
                    + " " + String.valueOf(current_page) + "/"
                    + String.valueOf(page_stop) + " LAST WAS "
                    + (success ? "SUCCESS\n" : "FAILED\n"));
            System.out.flush();
    }
    
    public void insert(String id) {
        Film film = db.selectFilm(String.valueOf(id));
        if(film != null){
            writeLog(Integer.valueOf(id), true);
            return;
        }
        while(true){
            waiter.waitForTryer(String.valueOf(pid));
            boolean success = true;
            try {
                film = KpParser.parseFilm(PageLoader.loadUrl(KpPath.makeFilmFromUrlPrefixLink(rootUrl, String.valueOf(id))),
                        PageLoader.loadUrl(KpPath.makeFilmLikeUrlPrefixLink(rootUrl, String.valueOf(id))));
            } catch (FuckupException | PageLoaderException e1) {
                e1.printStackTrace();
                waiter.fuckupDetect();
                success = false;
            } catch (Exception e1) {
                e1.printStackTrace();
                success = false;
                break;
            }
            writeLog(Integer.valueOf(id), success);
            if (success) {
                success = db.InsertFilm(film);
                break;
            }
        }
    }   
    
    @Override
    public void run() {
        System.out.println("STARTING INDEXER " + String.valueOf(pid));
        for (int current_page = page_start; current_page <= page_stop; current_page++) {
            insert(String.valueOf(current_page));
        }
        System.out.println("OVER INDEXER " + String.valueOf(pid));
    }
}
