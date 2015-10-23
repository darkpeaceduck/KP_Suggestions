package ru.kpsug;



import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import org.jsoup.nodes.Document;
import org.junit.Test;

import ru.kpsug.db.DBOperator;
import ru.kpsug.db.Film;
import ru.kpsug.indexer.KpParser;
import ru.kpsug.indexer.PageLoader;
import ru.kpsug.server.Suggestions;

import java.sql.*;
public class TestIndexer {

    @Test
    public void test() throws IOException {
//        if(false){
//            String id = "397236";
//            Film film = KpParser.parseFilm(PageLoader.loadFilm(id), PageLoader.loadFilmSuggestions(id) );
//            Suggestions sugg = new Suggestions();
//            sugg.buildGraph(film, 2);
//            ArrayList<ArrayList<Suggestions.Node>> result = sugg.getDepthSet();
//            int count = 0;
//            for(ArrayList<Suggestions.Node> ar : result){
//                for(Suggestions.Node node: ar){
//                    System.out.println("FILM WITH " + String.valueOf(count) );
//                    node.getContent().print(System.out);
//                    System.out.println("END---");
//                }
//                count++;
//            }
//            try {
//                film.print(System.out);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }
    
    @Test
    public void testDB() throws ClassNotFoundException, SQLException, IOException{
        DBOperator db_con =  new DBOperator(null);
        db_con.connect();
        assertTrue(db_con.BuildDatabase());
        String id = "373314";
        TreeMap<String, String> cookies = new TreeMap<String, String>();
        cookies.put("PHPSESSID", "1024f7c014ece92d83036ed35488c78d");
        cookies.put("_ym_visorc_22663942", "b");
        cookies.put("mobile", "no");
        cookies.put("noflash", "false");
        cookies.put("refresh_yandexuid", "251607371445295272");
        cookies.put("user_country", "ru");
        cookies.put("yandexuid", "251607371445295272");
        
        Document doc = PageLoader.loadFilm(id);
        System.out.println(doc);
        Film film = KpParser.parseFilm(doc, PageLoader.loadFilmSuggestions(id) );
        film.print(System.out);
        assertTrue(db_con.InsertFilm(film));
        for(String new_id : film.getSuggestion_links()){
            Film new_film = KpParser.parseFilm(PageLoader.loadFilm(new_id), PageLoader.loadFilmSuggestions(new_id));
            new_film.print(System.out);
            db_con.InsertFilm(new_film);
        }
//        assertEquals(db_con.selectFilm(id), film);
//        assertTrue(db_con.deleteFilmFromId(film.getId()));
        db_con.closeAll();
        
    }
    
    @Test
    public void testDbConf(){
//        try {
//            DBOperator db_con = new DBOperator("file.conf");
//        } catch (ClassNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        
    }
}
