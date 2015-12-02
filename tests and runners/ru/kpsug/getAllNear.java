package ru.kpsug;

import java.sql.SQLException;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.jsoup.HttpStatusException;

import ru.kpsug.db.DBOperator;
import ru.kpsug.db.Film;
import ru.kpsug.kp.KpParser;
import ru.kpsug.kp.KpParser.FuckupException;
import ru.kpsug.kp.PageLoader;
import ru.kpsug.kp.PageLoader.PageLoaderException;

public class getAllNear {
    
    public static void insert(DBOperator db, String id) throws FuckupException, HttpStatusException, PageLoaderException, Exception{
        Film film = KpParser.parseFilm(PageLoader.loadFilm(id), PageLoader.loadFilmSuggestions(id));
        System.out.println(db.InsertWithUpdate(film));
    }   
    
    public static void bfs(String start, DBOperator db, Integer depth) throws FuckupException, HttpStatusException, PageLoaderException, Exception{
        Map<String, Integer> used = new HashMap<>();
        Deque<String> queue= new LinkedList<>();
        used.put(start, 0);
        queue.add(start);
        insert(db, start);
        while(!queue.isEmpty()){
            String id = queue.getFirst();
            queue.pop();
            Integer h = used.get(id);
            if(h < depth){
                Film film = db.selectFilm(id);
                if(film == null){
                System.out.println(id);
                continue;
                }
                for(String go : film.getSuggestion_links()){
                    System.out.println(go);
                    if(!used.containsKey(go)){
                        used.put(go, h+1);
                        queue.push(go);
                        insert(db, go);
                    }
                }
            }
        }
    }
    
    public static void dfs(String id, DBOperator db, Set<String> used){
        Film film = db.selectFilm(id);
        used.add(id);
        for(String go : film.getSuggestion_links()){
            if(!used.contains(go)){
                dfs(go, db, used);
            }
        }
    }
    
    public static void main(String[] args) {
        DBOperator db = new DBOperator(null);
        try {
            db.connect();
        } catch (SQLException e) {
            System.out.println("failed ");
            e.printStackTrace();
            return;
        }
        try {
            bfs("373314" , db, 5);
        } catch (Exception e) {
            System.out.println("failed dfdf");
            e.printStackTrace();
        }
        try {
            db.closeAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
