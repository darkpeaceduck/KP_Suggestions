package ru.kpsug.indexer;

import java.sql.SQLException;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import ru.kpsug.db.DBOperator;
import ru.kpsug.db.Film;
import ru.kpsug.kp.KpPath;

public class Indexer {
    private static int page_start;
    private static int page_end;
    private static int depth;
    private static DBOperator db;
    private static IndexerWaiter waiter;
    private static ExecutorService exec;
    private static String prefix;
    public static void bfs(){
        Map<Integer, Integer> used = new HashMap<>();
        Deque<Integer> queue= new LinkedList<>();
        Deque<Future<?>> queueFut= new LinkedList<>();
        for (int i = page_start; i <=page_end; i++) {
            queue.push(i);
            queueFut.push(exec.submit(new SimpleTask(0, db, i, i, waiter, KpPath.getFilmPrefix())));
            used.put(i, 0);
        }
        while(!queue.isEmpty()){
            Integer id = queue.getFirst();
            queue.pop();
            Future<?> fut = queueFut.getFirst();
            queueFut.pop();
            Integer h = used.get(id);
            try {
                fut.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                continue;
            }
            if(h < depth){
                Film film = db.selectFilm(String.valueOf(id));
                if(film == null){
                    continue;
                }
                for(String go : film.getSuggestion_links()){
                    Integer goInt = Integer.parseInt(go); 
                    if(!used.containsKey(goInt)){
                        used.put(goInt, h+1);
                        queue.push(goInt);
                        queueFut.push(exec.submit(new SimpleTask(0, db, goInt, goInt, waiter, KpPath.makeFilmPrefix(prefix))));
                    }
                }
            }
        }
    }
    
    public static void bfsAtOneTask(){
        for (int i = page_start; i <=page_end; i++) {
            Runnable task = new BfsTask(0, db, i, waiter, KpPath.makeFilmPrefix(prefix), depth);
            exec.execute(task);
        }
    }
    
    public static void normal(int num){
        for (int i = page_start; i <=page_end; i+= num) {
            Runnable task = new SimpleTask(i - page_start, db, i, Math.min(page_end, i + num), waiter, KpPath.makeFilmPrefix(prefix));
            exec.execute(task);
        }
    }
    
    public static void main(String[] args) {

        if (args.length < 5) {
            System.out.println("wrong number of arguments");
            return;
        }
        page_start = Integer.parseInt(args[0]);
        page_end = Integer.parseInt(args[1]);
        int threads = Integer.parseInt(args[2]);
        int param = Integer.parseInt(args[4]);
        prefix = KpPath.getPrefix();
        if(args.length >= 7){
            System.setProperty("socksProxyHost", args[5]); // set proxy server
            System.setProperty("socksProxyPort", args[6]); 
        }
        System.out.println(prefix);
        
        waiter = new IndexerWaiter();
        try {
            db = new DBOperator(null);
            db.connect();
        } catch (Exception excp) {
            System.out.println("couldnt init db");
            return;
        }
        System.out.println("success init db");
        System.out.println(args[3]);

        threads = Math.min(threads, page_end - page_start + 1);
        exec = Executors.newFixedThreadPool(threads);
        new Thread(waiter).start();

        switch(args[3]){
        case "normal":
            normal(param);
            break;
        case "bfs":
            bfsAtOneTask();
            break;
        case "multibfs":
            bfs();
            break;
        }
        
        exec.shutdown();
        try {
            exec.awaitTermination(1000, TimeUnit.DAYS);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        waiter.off();

        try {
            db.closeAll();
        } catch (SQLException e) {
            System.out.println("couldnt close db");
        }
        System.out.println("OK");
    }
}
