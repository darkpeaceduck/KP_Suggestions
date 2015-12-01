package ru.kpsug;

import java.io.IOException;
import java.util.TreeMap;

import org.json.simple.parser.ParseException;
import org.jsoup.nodes.Document;

import ru.kpsug.db.DBOperator;
import ru.kpsug.db.Film;
import ru.kpsug.kp.KpParser;
import ru.kpsug.kp.PageLoader;
import ru.kpsug.kp.Search;
import ru.kpsug.kp.PageLoader.PageLoaderException;
import ru.kpsug.utils.ConfigParser;

public class SearchTest {
    public static void main(String[] args) throws Exception {
        DBOperator db_con =  new DBOperator(null);
        try {
            db_con.connect();
            Film film = db_con.selectFilm("12000");
            System.out.println(film);
        }catch(Exception e){
            
        }
    }

}
