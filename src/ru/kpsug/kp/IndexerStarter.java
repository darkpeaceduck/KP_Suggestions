package ru.kpsug.kp;

import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.ArrayList;

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
        OutputStreamWriter writer = new OutputStreamWriter(System.out);
        DBOperator db ;
        try{
           db = new DBOperator(null);
           db.connect();
           db.BuildDatabase();
        } catch(Exception excp){
            System.out.println("couldnt init db");
            return;
        }
        System.out.println("success init db");
        threads = Math.min(threads, page_end - page_start + 1);
        int task_num = (page_end - page_start + 1) / threads;
        int task_over =  (page_end - page_start + 1) % threads;
        ArrayList<Thread> thread_pool = new ArrayList<Thread>();
        for(int i = 0 ; i < threads; i++){
            int tasks = task_num;
            if(task_over > 0){
                task_over--;
                tasks++;
            }
            Thread new_thread = new Thread(new Indexer(writer, i, db, page_start, page_start + tasks -1));
            new_thread.start();
            thread_pool.add(new_thread);
            page_start += tasks;
        }
        for(Thread thread :thread_pool){
            try {
                thread.join();
            } catch (InterruptedException e) {
                
            }
        }
        try {
            db.closeAll();
        } catch (SQLException e) {
            System.out.println("couldnt close db");
        }
        System.out.println("OK");
    }
}
