package ru.kpsug.kp;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream.GetField;
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
    
    
    public static Document loadMainSearch(String token) throws IOException{
        Document doc = Jsoup.connect(KpPath.makeMainSearchLink(token)).timeout(timeout).userAgent(user_agent).get();
        return doc;
    }
    
    public static Document loadPrefixSearch(String token) throws IOException{
        Connection conn =  Jsoup.connect(KpPath.getPrefixSearchLink()).timeout(timeout).userAgent(user_agent);
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("q", token);
        map.put("query_id", "0.7698689627452319");
        map.put("type", "jsonp");
        map.put("topsuggest", "true");
        conn.data(map);
        conn.referrer(KpPath.getPrefix());
        Document doc = conn.get();
//        System.out.println(doc.html());
        return doc;
    }
}
