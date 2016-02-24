package ru.kpsug.kp;



import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.TreeMap;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

  

public class PageLoader {
    public static class PageLoaderException extends Exception{
        private static final long serialVersionUID = -2155291161920319692L;
    };
    
    
    
    private static int timeout = 30000;
    private static String user_agent = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:41.0) Gecko/20100101 Firefox/41.0";
    
    private static Document getWrapper(Connection conn) throws PageLoaderException, HttpStatusException{
        try{
            return conn.get();
        }catch(HttpStatusException e){
            throw e;
        }catch(Exception excp){
            excp.printStackTrace();
            throw new PageLoaderException();
        }
    }
    
    private static Connection doi(Connection conn){
        return conn.referrer(KpPath.getPrefix());
    }
    
    public static Document loadFilm(String id) throws Exception{
        return getWrapper(doi(Jsoup.connect(KpPath.makeFilmLink(id)).timeout(timeout).userAgent(user_agent)));
    }
    public static Document loadFilmWithCookies(String id, TreeMap<String, String> cookies) throws PageLoaderException, HttpStatusException{
        Connection conn = Jsoup.connect(KpPath.makeFilmLink(id)).timeout(timeout).userAgent(user_agent);
        conn = conn.cookies(cookies);   
        return getWrapper(conn);
    }
    
    public static Document loadFilmSuggestions(String id) throws PageLoaderException,HttpStatusException{
        return  getWrapper(doi(Jsoup.connect(KpPath.makeFilmLikeLink(id)).timeout(timeout).userAgent(user_agent)));
    }
    
    public static Document loadFilmSuggestionsWithCookies(String id, TreeMap<String, String> cookies) throws PageLoaderException, HttpStatusException{
        Connection conn = Jsoup.connect(KpPath.makeFilmLikeLink(id)).timeout(timeout).userAgent(user_agent);
        conn = conn.cookies(cookies);
        return getWrapper(conn);
    }
    
    public static Document loadUrl(String url) throws HttpStatusException, PageLoaderException{
        return getWrapper(Jsoup.connect(url).timeout(timeout).userAgent(user_agent));
    }
    
    
    private static String convertToUtf(String token) throws PageLoaderException{
        String result = null;
        try {
            result = URLEncoder.encode(token, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new PageLoaderException();
        }
        return result;
    }
    
    public static Document loadMainSearch(String token) throws PageLoaderException, HttpStatusException{
        return getWrapper(Jsoup.connect(KpPath.makeMainSearchLink(convertToUtf(token))).timeout(timeout).userAgent(user_agent));
    }
    
    public static Document loadPrefixSearch(String token) throws PageLoaderException, HttpStatusException{
        Connection conn =  Jsoup.connect(KpPath.getPrefixSearchLink()).timeout(timeout).userAgent(user_agent);
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("q", convertToUtf(token));
        map.put("query_id", "0.7698689627452319");
        map.put("type", "jsonp");
        map.put("topsuggest", "true");
        conn.data(map);
        conn.referrer(KpPath.getPrefix());
        return getWrapper(conn);
    }
};
