package ru.kpsug.kp;

public class KpPath {
    public static String getPrefix(){
        return "http://www.kinopoisk.ru/";
    }
    
    public static String getFilmSuffix(){
        return "film/";
    }
    
    public static String makeFilmPrefix(String pref){
        return pref + getFilmSuffix();
    }
    
    public static String getFilmPrefix(){
        return makeFilmPrefix(getPrefix());
    }
    
    public static String getLikeSuffix(){
        return "like/";
    }
    
    public static String makeFilmLink(String id){
        return makeFilmFromUrlPrefixLink(getFilmPrefix(), id);
    }
    
    public static String makeFilmFromUrlPrefixLink(String prefix, String id){
        return prefix + id;
    }
    
    public static String makeFilmLikeUrlPrefixLink(String prefix, String id){
        return prefix + id + "/" + getLikeSuffix();
    }
    
    
    public static String makeFilmLikeLink(String id){
        return makeFilmLikeUrlPrefixLink(getFilmPrefix(), id);
    }
    
    public static String makeMainSearchLink(String token){
        return getPrefix() + "s/type/film/list/1/find/" + token;
    }
    
    public static String getPrefixSearchLink(){
        return getPrefix() + "handler_search.php";
    }
    
}
