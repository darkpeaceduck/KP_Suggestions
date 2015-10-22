package tryurl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;

public class PageLoader {
    private static int timeout = 30000;
    private static String user_agent = "Mozilla";
    public static Document loadFilm(String id) throws IOException{
        return Jsoup.connect(KpPath.makeFilmLink(id)).timeout(timeout).userAgent(user_agent).get();
    }
    public static Document loadFilmSuggestions(String id) throws IOException{
        return  Jsoup.connect(KpPath.makeFilmLikeLink(id)).timeout(timeout).userAgent(user_agent).get();
    }
}
