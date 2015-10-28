package ru.kpsug.kp;

import java.io.IOException;
import java.util.ArrayList;

public class Search {
    public static class SearchResult{
        private ArrayList<String> ids = new ArrayList<String>();
        public SearchResult() {
        }
        
        public SearchResult(ArrayList<String> ids){
            this.ids = ids;
        }
        
        public ArrayList<String> getIds() {
            return ids;
        }

        public void setIds(ArrayList<String> ids) {
            this.ids = ids;
        }
        
        public void addId(String id){
            ids.add(id);
        }
        
        @Override
        public String toString() {
            return ids.toString();
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
