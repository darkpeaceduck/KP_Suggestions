package ru.kpsug.kp;

import java.io.IOException;
import java.util.ArrayList;

import ru.kpsug.db.Film;

public class Search {
    public static class SearchResult{
        private ArrayList<Film> films = new ArrayList<Film>();
        public SearchResult() {
        }
        
        public SearchResult(ArrayList<Film> films){
            this.films = films;
        }
        
        
        public void addfilm(Film film){
            films.add(film);
        }
        
        @Override
        public String toString() {
            return films.toString();
        }

        public ArrayList<Film> getFilms() {
            return films;
        }

        public void setFilms(ArrayList<Film> films) {
            this.films = films;
        }
        
        public int getNumber(){
            return films.size();
        }
        
        public SearchResult cut(int length){
            SearchResult new_ret = new SearchResult((ArrayList<Film>) films.subList(0, length));
            return new_ret;
        }
        
    }
    
    public static class SearchException extends Exception{
        private static final long serialVersionUID = 8385837171526199624L;
    }
    
    public static SearchResult mainSearch(String token) throws SearchException{
        SearchResult res = null;
        try {
            res = new SearchResult(KpParser.parseMainSearch(PageLoader.loadMainSearch(token)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new SearchException();
        }
        return res;
    }
    
    public static SearchResult prefixSearch(String token) throws SearchException{
        SearchResult ret;
        try {
            ret = new SearchResult(KpParser.parsePrefixSearch(PageLoader.loadPrefixSearch(token)));
        }  catch (Exception e) {
            e.printStackTrace();
            throw new SearchException();
        }
        return ret;
    }
}
