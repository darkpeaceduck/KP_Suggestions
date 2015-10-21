package tryurl;



import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import java.sql.*;
public class TestLoad {

    @Test
    public void test() throws IOException {
        if(false){
            String id = "397236";
            Film film = KpParser.parseFilm(PageLoader.loadFilm(id), PageLoader.loadFilmSuggestions(id) );
            Suggestions sugg = new Suggestions();
            sugg.buildGraph(film, 2);
            ArrayList<ArrayList<Suggestions.Node>> result = sugg.getDepthSet();
            int count = 0;
            for(ArrayList<Suggestions.Node> ar : result){
                for(Suggestions.Node node: ar){
                    System.out.println("FILM WITH " + String.valueOf(count) );
                    node.getContent().print(System.out);
                    System.out.println("END---");
                }
                count++;
            }
            try {
                film.print(System.out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Test
    public void testDB() throws ClassNotFoundException, SQLException, IOException{
        DBConnector db_con =  new DBConnector(null);
        db_con.connect();
        db_con.BuildDatabase();
        String id = "397236";
        Film film = KpParser.parseFilm(PageLoader.loadFilm(id), PageLoader.loadFilmSuggestions(id) );
        db_con.InsertFilm(film);
        assertTrue(db_con.deleteFilmFromId(film.getId()));
        db_con.closeAll();
    }
    
    @Test
    public void testDbConf(){
        try {
            DBConnector db_con = new DBConnector("file.conf");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
}
