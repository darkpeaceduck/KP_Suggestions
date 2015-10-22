package tryurl;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class PageLoader {
    private static int timeout = 30000;
    private static String user_agent = "Mozilla";
    public static Document loadFilm(String id) throws IOException{
        return Jsoup.connect(KpPath.makeFilmLink(id)).timeout(timeout).userAgent(user_agent).get();
    }
    public static Document loadFilmSuggestions(String id) throws IOException{
        return Jsoup.connect(KpPath.makeFilmLikeLink(id)).timeout(timeout).userAgent(user_agent).get();
    }
}
