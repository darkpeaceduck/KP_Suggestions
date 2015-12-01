package ru.kpsug.indexer;

import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ru.kpsug.db.DBOperator;

public class IndexerStarter {
    public static void main(String[] args) {
        if(args.length < 4){
            System.out.println("wrong number of arguments");
            return;
        }
        int page_start = Integer.parseInt(args[1]);
        int page_end = Integer.parseInt(args[2]);
        int threads = Integer.parseInt(args[3]);
        IndexerWaiter waiter = new IndexerWaiter();
        DBOperator db ;
        try{
           db = new DBOperator(null);
           db.connect();
        } catch(Exception excp){
            System.out.println("couldnt init db");
            return;
        }
        System.out.println("success init db");
        threads = Math.min(threads, page_end - page_start + 1);
        int task_num = (page_end - page_start + 1) / threads;
        int task_over =  (page_end - page_start + 1) % threads;
        
        ExecutorService exec = Executors.newFixedThreadPool(threads);
        new Thread(waiter).start();
        for(int i = 0 ; i < threads; i++){
            int tasks = task_num;
            if(task_over > 0){
                task_over--;
                tasks++;
            }
            exec.execute(new Indexer(i, db, page_start, page_start + tasks -1, waiter));
            page_start += tasks;
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
