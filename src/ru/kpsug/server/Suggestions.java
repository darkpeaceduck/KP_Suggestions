package ru.kpsug.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        private TreeMap<String, ArrayList<String>> edges = new TreeMap<String, ArrayList<String>>();
        private TreeMap<String, Integer> levels;
        private Map<Integer, List<String>> levelsEdges;
        private TreeMap<String, Film> films;
        
        public Map<Integer, List<String>> getLevelsEdges() {
            return levelsEdges;
        }

        public TreeMap<String, ArrayList<String>> getEdges() {
            return edges;
        }

        public void setLevelsEdges(Map<Integer, List<String>> levelsEdges) {
            this.levelsEdges = levelsEdges;
        }

        public void setEdges(TreeMap<String, ArrayList<String>> edges) {
            this.edges = edges;
        }

        public void addEdgeOr(String a, String b) {
            if (!edges.containsKey(a)) {
                edges.put(a, new ArrayList<String>());
            }
            edges.get(a).add(b);
        }

        public void addEdge(String a, String b) {
            addEdgeOr(a, b);
            addEdgeOr(b, a);
        }

        @Override
        public String toString() {
            return toJSONString();
        }

        public TreeMap<String, Integer> getLevels() {
            return levels;
        }

        public void setLevels(TreeMap<String, Integer> levels) {
            this.levels = levels;
        }

        @Override
        public String toJSONString() {
            return toJSONObject().toJSONString();
        }

        private void parseEdges(Object object) {
            setEdges((TreeMap<String, ArrayList<String>>) object);
        }
        
        private void parseLevelsEdges(Object object){
            Map<String, List<String>> result = ((Map<String, List<String>>) object);
            Map<Integer, List<String>> outp = new TreeMap<Integer, List<String>>();
            for(Entry<String, List<String>> item : result.entrySet()){
                outp.put(Integer.parseInt(item.getKey()), item.getValue());
            }
            setLevelsEdges(outp);
        }

        private void parseLevels(Object object) {
            TreeMap<String, Long> map = (TreeMap<String, Long>) object;
            TreeMap<String, Integer> new_levels = new TreeMap<String, Integer>();
            for (Entry<String, Long> entry : map.entrySet()) {
                new_levels.put(entry.getKey(), entry.getValue().intValue());
            }
            setLevels(new_levels);
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
            object.put("edges", edges);
            object.put("levels", levels);
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
                case "edges":
                    parseEdges(entry.getValue());
                    break;
                case "levels":
                    parseLevels(entry.getValue());
                    break;
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

    private static final int DEPTH_LIMIT = 3;

    public static SuggestionsResult getFilmsAround(String id, int depth,
            DBOperator db) {
        Film film = db.selectFilm(id);
        SuggestionsResult result = null;
        if (film != null && depth <= DEPTH_LIMIT) {
            result = new SuggestionsResult();
            TreeMap<String, Integer> is_level = new TreeMap<String, Integer>();
            Map<Integer, List<String>> re_is_level= new TreeMap<Integer, List<String>>();
            TreeMap<String, Film> films = new TreeMap<String, Film>();
            
            is_level.put(film.getId(), 0);
            
            re_is_level.put(0, new ArrayList<String>());
            re_is_level.get(0).add(film.getId());
            
            ArrayList<Film> q = new ArrayList<Film>();
            q.add(film);
            films.put(film.getId(), film);
            
            int head_position = 0;
            while (q.size() > head_position) {
                Film current = q.get(head_position++);
                Integer current_depth = is_level.get(current.getId());
                if (current_depth < depth) {
                    for (String link : current.getSuggestion_links()) {
                        if (!is_level.containsKey(link)) {
                            Film new_film = db.selectFilm(link);
                            is_level.put(new_film.getId(), current_depth + 1);
                            if(!re_is_level.containsKey(current_depth + 1)){
                                re_is_level.put(current_depth + 1, new ArrayList<String>());
                            }
                            re_is_level.get(current_depth + 1).add(new_film.getId());
                            q.add(new_film);
                            result.addEdge(film.getId(), new_film.getId());
                            films.put(link, new_film);
                        }
                    }
                }
            }
            result.setLevels(is_level);
            result.setFilms(films);
            result.setLevelsEdges(re_is_level);
        }
        return result;
    }
}
