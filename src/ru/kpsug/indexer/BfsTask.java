package ru.kpsug.indexer;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import ru.kpsug.db.DBOperator;
import ru.kpsug.db.Film;
import ru.kpsug.kp.KpParser;
import ru.kpsug.kp.KpPath;
import ru.kpsug.kp.PageLoader;
import ru.kpsug.kp.KpParser.FuckupException;
import ru.kpsug.kp.PageLoader.PageLoaderException;

public class BfsTask implements Runnable {
    private void writeLog(Integer page, boolean success){
        System.out.append("INDEXER  " + String.valueOf(pid)
                + " WITH PAGE = " + String.valueOf(page) + " OVER WITH " 
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
    
    private int pid;
    private DBOperator db;
    private String start;
    private int depth;
    private IndexerWaiter waiter;
    String rootUrl;
    
    public BfsTask(int id, DBOperator db,
            int page, IndexerWaiter waiter, String rootUrl, int depth) {
        super();
        this.db = db;
        this.pid = id;
        this.waiter = waiter;
        this.rootUrl = rootUrl;
        this.start = String.valueOf(page);
        this.depth = depth;
    }
    
    @Override
    public void run() {
        System.out.println("INDEXER " + String.valueOf(pid) + " STARTED");
        Map<String, Integer> used = new HashMap<>();
        Deque<String> queue= new LinkedList<>();
        used.put(start, 0);
        queue.add(start);
        insert(start);
        while(!queue.isEmpty()){
            String id = queue.getFirst();
            queue.pop();
            Integer h = used.get(id);
            if(h < depth){
                Film film = db.selectFilm(id);
                if(film == null){
                    continue;
                }
                for(String go : film.getSuggestion_links()){
                    if(!used.containsKey(go)){
                        used.put(go, h+1);
                        queue.push(go);
                        insert(go);
                    }
                }
            }
        }
        System.out.println("INDEXER " + String.valueOf(pid) + " FINISHED");
    }
}
