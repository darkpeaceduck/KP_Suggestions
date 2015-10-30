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
    }
    
    public static SearchResult mainSearch(String token){
        SearchResult res = null;
        try {
            res = new SearchResult(KpParser.parseMainSearch(PageLoader.loadMainSearch(token)));
        } catch (IOException e) {
        }
        return res;
    }
    
    public static SearchResult parsePrefixSearch(String token){
        
        return null;
    }
}
