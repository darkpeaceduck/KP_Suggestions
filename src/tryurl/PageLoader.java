package tryurl;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class PageLoader {
    private static int timeout = 30000;
    private static String user_agent = "Mozilla";
    private static String kp_prefix = "http://www.kinopoisk.ru/film/"; 
    public static Document loadFilm(String id) throws IOException{
        return Jsoup.connect(kp_prefix + String.valueOf(id)).timeout(timeout).userAgent(user_agent).get();
    }
    public static Document loadFilmSuggestions(String id) throws IOException{
        return Jsoup.connect(kp_prefix + String.valueOf(id) + "/like").timeout(timeout).userAgent(user_agent).get();
    }
}
