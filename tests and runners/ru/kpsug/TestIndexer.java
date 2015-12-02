package ru.kpsug;



import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONStreamAware;
import org.json.simple.JSONValue;
import org.jsoup.nodes.Document;
import org.junit.Test;

import ru.kpsug.db.DBOperator;
import ru.kpsug.db.Film;
import ru.kpsug.kp.KpParser;
import ru.kpsug.kp.PageLoader;
import ru.kpsug.kp.PageLoader.PageLoaderException;
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
    
    private void forFilm(DBOperator db, String id) throws PageLoaderException{
//        Film film = db.selectFilm(id);
//        System.out.println(film.getName());
//        for(String new_id : film.getSuggestion_links()){
//            Film new_film = KpParser.parseFilm(PageLoader.loadFilm(new_id), PageLoader.loadFilmSuggestions(new_id));
//            System.out.println(new_film.getName());
//            db.InsertFilm(new_film);
//        }
    }
    
    private void ftest(){
        DBOperator db_con =  new DBOperator(null);
        try {
            db_con.connect();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Film film = db_con.selectFilm("-1");
        assertTrue(film == null);
    }
    
    @Test
    public void testDB() throws ClassNotFoundException, SQLException, IOException{
//        DBOperator db_con =  new DBOperator(null);
//        db_con.connect();
//        assertTrue(db_con.BuildDatabase());
//        String id = "373314";
//        TreeMap<String, String> cookies = new TreeMap<String, String>();
//        cookies.put("PHPSESSID", "1024f7c014ece92d83036ed35488c78d");
//        cookies.put("_ym_visorc_22663942", "b");
//        cookies.put("mobile", "no");
//        cookies.put("noflash", "false");
//        cookies.put("refresh_yandexuid", "251607371445295272");
//        cookies.put("user_country", "ru");
//        cookies.put("yandexuid", "251607371445295272");
//        
//        Document doc = PageLoader.loadFilm(id);
////        System.out.println(doc);
//        Film film = KpParser.parseFilm(doc, PageLoader.loadFilmSuggestions(id) );
////        System.out.println(film);
//        System.out.print(film.getName());
//        db_con.InsertFilm(film);
//        forFilm(db_con, id);
//        for(String new_id : film.getSuggestion_links()){
//            forFilm(db_con, new_id);
//        }
////        Film new_film = new Film();
//        assertTrue(new_film.refreshStateFromJSONString(film.toJSONString()));
//        assertEquals(new_film, film);
//        System.out.println(new_film.getSuggestion_links().get(0));
//        assertTrue(db_con.InsertFilm(film));
//        assertEquals(db_con.selectFilm(id), film);
       
////        assertTrue(db_con.deleteFilmFromId(film.getId()));
//        db_con.closeAll();
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
    
    @Test
    public void test1(){
//        Film film = new Film();
//        film.setActors(new ArrayList<String>(Arrays.asList("aba", "caba")));
//        film.setSuggestion_links(new ArrayList<String>(Arrays.asList("123", "321")));
//        film.setId("12312312");
//        film.setName("vasya");
//        film.setRating("10.123");
//        film.setAnnotation("trololol l l ol ol o lo l o lo  l");
//        TreeMap<String, ArrayList<String>> new_list = new TreeMap<String, ArrayList<String>>();
//        new_list.put("key", new ArrayList<>(Arrays.asList("value")));
//        new_list.put("key2", new ArrayList<>(Arrays.asList("value")));
//        film.setPurposes(new_list);
//        String str_type=  film.toString();
//        System.out.println(str_type);
//        Film new_film = Film.parse(str_type);
//        System.out.println(new_film);
//        assertEquals(film, new_film);
//        TreeMap<String, String> good = new TreeMap<String, String>();
//        good.put("1", "a");
//        good.put("2", "b");
//        String s = good.toString();
//        String [] splitt = (s.substring(1, s.length() - 1)).split(", ");
//        for(String res :splitt){
//            System.out.println(res);
//        }
    }
    
    @Test
    public void test2(){
//        String s = "(373314,453284)(373314,415379)(373314,401779)(373314,22292)(373314,452599)(373314,39864)(373314,251733)";
//        Matcher m = Pattern.compile("\\((.*?),(.*?)\\)").matcher(s);   
//        while (m.find()) {
//            System.out.println("Found: " + m.group(2));
//        }
//        String [] key = s.split("(\\[.*?\\])(, ){0,1}");
//        String [] values =  s.split("(\\[.*?\\])");
//        for(String res : key){
//            System.out.println(res);
//        }
    }
}
