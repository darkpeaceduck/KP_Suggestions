package ru.kpsug.indexer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;

public class PageLoader {
    private static int timeout = 30000;
    private static String user_agent = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:41.0) Gecko/20100101 Firefox/41.0";
    public static Document loadFilm(String id) throws IOException{
        return Jsoup.connect(KpPath.makeFilmLink(id)).timeout(timeout).userAgent(user_agent).get();
    }
    public static Document loadFilmWithCookies(String id, TreeMap<String, String> cookies) throws IOException{
        Connection conn = Jsoup.connect(KpPath.makeFilmLink(id)).timeout(timeout).userAgent(user_agent);
        conn = conn.cookies(cookies);
        conn = conn.referrer("http://www.kinopoisk.ru/");
        return conn.get();
    }
    
    public static Document loadFilmSuggestions(String id) throws IOException{
        return  Jsoup.connect(KpPath.makeFilmLikeLink(id)).timeout(timeout).userAgent(user_agent).get();
    }
    
    public static Document loadFilmSuggestionsWithCookies(String id, TreeMap<String, String> cookies) throws IOException{
        Connection conn = Jsoup.connect(KpPath.makeFilmLikeLink(id)).timeout(timeout).userAgent(user_agent);
        conn = conn.cookies(cookies);
        return conn.get();
    }
}
