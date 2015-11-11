package ru.kpsug.app.film;

import ru.kpsug.db.Film;

public class FilmStringPretty {
    public static String prefixPrint(Film film){
        String result = "";
        result += film.getName();
        result += "(";
        boolean have = false;
        if(film.getPurposes().containsKey("���")){
            have = true;
            result += film.getPurposes().get("���").get(0);
        }
        if(film.getPurposes().containsKey("������")){
            if(have){
                result += ", ";
            }
            result += film.getPurposes().get("������").get(0);
        }
        
        if(film.getPurposes().containsKey("��������")){
            if(have){
                result += ", ";
            }
            result += film.getPurposes().get("��������").get(0);
        }
        
        if(film.getRating() != null){
            if(have){
                result += ", ";
            }
            result += film.getRating();
        }
        result += ")";
        return result;
    }
}   
