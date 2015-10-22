package tryurl;

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
}
