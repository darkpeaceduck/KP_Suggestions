package ru.kpsug.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import ru.kpsug.db.DBOperator;
import ru.kpsug.db.Film;
import ru.kpsug.kp.KpParser;
import ru.kpsug.kp.PageLoader;
import ru.kpsug.utils.ConfigParser;
import ru.kpsug.utils.JSONParceble;

public class Suggestions {
    public static class SuggestionsResult implements JSONParceble {
        private Map<Integer, List<String>> levelsEdges =new TreeMap<Integer, List<String>>();
        private TreeMap<String, Film> films = new TreeMap<String, Film>();
        
        public Map<Integer, List<String>> getLevelsEdges() {
            return levelsEdges;
        }

        public void setLevelsEdges(Map<Integer, List<String>> levelsEdges) {
            this.levelsEdges = levelsEdges;
        }
        
        
        public void setLevelsEdgesFromListFilm(Map<Integer, List<Film>> levelsEdges) {
            for(Entry<Integer, List<Film>> entry : levelsEdges.entrySet()){
                List<String> list = new ArrayList<String>();
                Collections.sort(entry.getValue(), Film.getFilmRatingComparator());
                int num = 0;
                for(Film film : entry.getValue()){
                    list.add(film.getId());
                    num++;
                    if(num > MAX_FILMS_ON_LEVEL){
                        break;
                    }
                }
                this.levelsEdges.put(entry.getKey(), list);
             }
         }

        @Override
        public String toString() {
            return toJSONString();
        }


        @Override
        public String toJSONString() {
            return toJSONObject().toJSONString();
        }

        private void parseLevelsEdges(Object object){
            Map<String, List<String>> result = ((Map<String, List<String>>) object);
            Map<Integer, List<String>> outp = new TreeMap<Integer, List<String>>();
            for(Entry<String, List<String>> item : result.entrySet()){
                outp.put(Integer.parseInt(item.getKey()), item.getValue());
            }
            setLevelsEdges(outp);
        }

        private void parseFilms(Object object) {
            TreeMap<String, Object> map = (TreeMap<String, Object>) object;
            TreeMap<String, Film> new_films = new TreeMap<>();
            for (Entry<String, Object> entry : map.entrySet()) {
                Film film = new Film();
                film.refreshStateFromObject(entry.getValue());
                new_films.put(entry.getKey(), film);
            }
            setFilms(new_films);
        }

        @Override
        public JSONObject toJSONObject() {
            JSONObject object = new JSONObject();
            object.put("films", films);
            object.put("levelsEdges", levelsEdges);
            return object;
        }

        @Override
        public boolean refreshStateFromJSONString(String s) {
            try {
                return refreshStateFromObject(ConfigParser.getJSONParser()
                        .parse(s, ConfigParser.getContainerFactory()));
            } catch (ParseException e) {
                return false;
            }
        }

        @Override
        public boolean refreshStateFromObject(Object object) {
            TreeMap<String, Object> map;
            try {
                map = (TreeMap<String, Object>) object;
            } catch (ClassCastException excp) {
                return false;
            }
            for (Entry<String, Object> entry : map.entrySet()) {
                if (!(entry.getKey() instanceof String)) {
                    return false;
                }
                switch (entry.getKey()) {
                case "films":
                    parseFilms(entry.getValue());
                    break;
                case "levelsEdges":
                    parseLevelsEdges(entry.getValue());
                    break;
                }
            }
            return true;
        }

        public TreeMap<String, Film> getFilms() {
            return films;
        }

        public void setFilms(TreeMap<String, Film> films) {
            this.films = films;
        }

    }
    
    

    private static final int DEPTH_LIMIT = 10;
    private static final int MAX_FILMS_ON_LEVEL = 15;
    private static final int MAX_RUNTIME_FILMS_ON_LEVEL = 100;
    
    private static Set<Film> getSetWithComp(){
        return new TreeSet<Film>(Film.getFilmRatingComparator());
    }
    
    public static SuggestionsResult method1(String id, int depth, DBOperator db){
        Film film = db.selectFilm(id);
        SuggestionsResult result = null;
        depth = Math.min(depth, DEPTH_LIMIT);
        if (film != null && depth <= DEPTH_LIMIT) {
            result = new SuggestionsResult();
            TreeMap<String, Integer> is_level = new TreeMap<String, Integer>();
            Map<Integer, List<Film>> re_is_level= new TreeMap<Integer, List<Film>>();
            TreeMap<String, Film> films = new TreeMap<String, Film>();
            is_level.put(film.getId(), 0);
            
            re_is_level.put(0, new ArrayList<Film>());
            re_is_level.get(0).add(film);
            
            ArrayList<Film> q = new ArrayList<Film>();
            q.add(film);
            films.put(film.getId(), film);
            
            int head_position = 0;
            while (q.size() > head_position) {
                Film current = q.get(head_position++);
                Integer current_depth = is_level.get(current.getId());
                if (current_depth < depth && !(re_is_level.containsKey(current_depth + 1) && re_is_level.get(current_depth + 1).size() > MAX_RUNTIME_FILMS_ON_LEVEL)) {
                    for (String link : current.getSuggestion_links()) {
                        if (!is_level.containsKey(link)) {
                            Film new_film = db.selectFilm(link);
                            if(new_film == null){
                                continue;
                            }
                            is_level.put(new_film.getId(), current_depth + 1);
                            
                            if(!re_is_level.containsKey(current_depth + 1)){
                                re_is_level.put(current_depth + 1, new ArrayList<Film>());
                            }
                            re_is_level.get(current_depth + 1).add(new_film);
                            
                            q.add(new_film);
                            films.put(link, new_film);
                        }
                    }
                }
            }
            result.setFilms(films);
            result.setLevelsEdgesFromListFilm(re_is_level);
        }
        return result;
    }
    
    
    public static SuggestionsResult getFilmsAround(String id, int depth,
            DBOperator db) {
        return method1(id, depth, db);
    }
}
