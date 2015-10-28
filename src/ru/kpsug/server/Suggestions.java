package ru.kpsug.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import ru.kpsug.db.DBOperator;
import ru.kpsug.db.Film;
import ru.kpsug.kp.KpParser;
import ru.kpsug.kp.PageLoader;
import ru.kpsug.utils.ConfigParser;
import ru.kpsug.utils.JSONParceble;

public class Suggestions {
    public static class SuggestionsResult implements JSONParceble{
        private TreeMap<Film, ArrayList<Film>> edges = new TreeMap<Film, ArrayList<Film>>();
        private TreeMap<Film, Integer> levels = new TreeMap<Film, Integer>();
        
        public TreeMap<Film, ArrayList<Film>> getEdges() {
            return edges;
        }
        public void setEdges(TreeMap<Film, ArrayList<Film>> edges) {
            this.edges = edges;
        }
        public TreeMap<Film, Integer> getLevels() {
            return levels;
        }
        public void setLevels(TreeMap<Film, Integer> levels) {
            this.levels = levels;
        }
        
        public void addEdgeOr(Film a, Film b){
            if(!edges.containsKey(a)){
                edges.put(a, new ArrayList<Film>());
            }
            edges.get(a).add(b);
        }
        
        public void addEdge(Film a , Film b){
            addEdgeOr(a, b);
            addEdgeOr(b, a);
        }
       
        @Override
        public String toString() {
            return toJSONString();
        }
        @Override
        public String toJSONString() {
            return toJSONObject().toJSONString();
        }
        
        private ArrayList<Film> parseArrayListFilm(Object object){
            ArrayList<Object> arr = ( ArrayList<Object> ) object;
            ArrayList<Film> ret= new ArrayList<Film>();
            for(Object next : arr){
                Film new_film = new Film();
                new_film.refreshStateFromObject(next);
                ret.add(new_film);
            }
            return ret;
        }
        
        void parseEdges(Object object) {
            TreeMap<Object, Object> map = ( TreeMap<Object, Object> ) object;
            TreeMap<Film, ArrayList<Film>> new_edges = new TreeMap<Film, ArrayList<Film>>();
            for(Entry<Object, Object> entry : map.entrySet()){
//                film.refreshStateFromObject(entry.getKey());
                Film film = new Film();
                film.refreshStateFromJSONString((String)entry.getKey());
                ArrayList<Film> ret =  new ArrayList<Film>();
                new_edges.put(film, ret);
            }
            setEdges(new_edges);
        }
        
        void parseLevels(Object object){
            TreeMap<Object, Object> map = ( TreeMap<Object, Object> ) object;
            TreeMap<Film, Integer> new_levels = new TreeMap<Film, Integer>();
            for(Entry<Object, Object> entry : map.entrySet()){
                Film film = new Film();
                film.refreshStateFromJSONString((String)entry.getKey());
                new_levels.put(film, ((Long)entry.getValue()).intValue());
            }
            setLevels(new_levels);
        }
        
        @Override
        public JSONObject toJSONObject() {
            JSONObject object = new JSONObject();
            object.put("edges", edges);
            object.put("levels", levels);
            return object;
        }
        
        @Override
        public boolean refreshStateFromJSONString(String s) {
            try {
                return refreshStateFromObject(ConfigParser.getJSONParser().parse(s, ConfigParser.getContainerFactory()));
            } catch (ParseException e) {
                return false;
            }
        }
        @Override
        public boolean refreshStateFromObject(Object object) {
            TreeMap<String, Object> map;
            try{
                map = (TreeMap<String, Object>) object;
            }catch(ClassCastException excp){
                return false;
            }
            for(Entry<String, Object> entry : map.entrySet()){
                if(!(entry.getKey() instanceof String)){
                    return false;
                }
                switch (entry.getKey()) {
                case "edges":
                    parseEdges(entry.getValue());
                    break;
                case "levels":
                    parseLevels(entry.getValue());
                    break;
                }
            }
            return true;
        }
        
    }
    
    private static final int DEPTH_LIMIT = 3;

    public static SuggestionsResult getFilmsAround(String id, int depth, DBOperator db) {
        Film film = db.selectFilm(id);
        SuggestionsResult result = null;
        if (film != null && depth <= DEPTH_LIMIT) {
            result = new SuggestionsResult();
            TreeMap<Film, Integer> is_level = new TreeMap<Film, Integer>();
            
            is_level.put(film, 0);
            ArrayList<Film> q = new ArrayList<Film>();
            q.add(film);
            int head_position = 0;
            while (q.size() > head_position) {
                Film current = q.get(head_position++);
                Integer current_depth = is_level.get(current);
                if (current_depth < depth) {
                    for (String link : current.getSuggestion_links()) {
                        Film new_film = db.selectFilm(link);
                        if (!is_level.containsKey(new_film)) {
                            is_level.put(new_film, current_depth + 1);
                            q.add(new_film);
                            result.addEdge(film, new_film);
                        }
                    }
                }
            }
            result.setLevels(is_level);
        } 
        return result;
    }
}
