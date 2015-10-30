package ru.kpsug.kp;

public class KpPath {
    public static String getPrefix(){
        return "http://www.kinopoisk.ru/";
    }
    
    public static String getFilmPrefix(){
        return getPrefix() + "film/"; 
    }
    
    public static String getLikeSuffix(){
        return "like/";
    }
    
    public static String makeFilmLink(String id){
        return getFilmPrefix() + id;
    }
    
    public static String makeFilmLikeLink(String id){
        return getFilmPrefix() + id + "/" + getLikeSuffix();
    }
    
    public static String makeMainSearchLink(String token){
        return getPrefix() + "s/type/film/list/1/find/" + token;
    }
    
    public static String getPrefixSearchLink(){
        return getPrefix() + "handler_search.php";
    }
}
