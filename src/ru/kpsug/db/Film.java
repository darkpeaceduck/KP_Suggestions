package ru.kpsug.db;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.junit.runners.ParentRunner;

import ru.kpsug.server.Suggestions;
import ru.kpsug.utils.ConfigParser;
import ru.kpsug.utils.JSONParceble;

public class Film implements Comparable<Film>, JSONParceble {
    private ArrayList<String> suggestion_links = new ArrayList<>();
    private String id = null;
    private String name = null;
    private String annotation = null;
    private String rating = null;
    private TreeMap<String, ArrayList<String>> purposes = new TreeMap<>();
    private ArrayList<String> actors = new ArrayList<String>();

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public ArrayList<String> getSuggestion_links() {
        return suggestion_links;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public TreeMap<String, ArrayList<String>> getPurposes() {
        return purposes;
    }

    public void setPurposes(TreeMap<String, ArrayList<String>> purposes) {
        this.purposes = purposes;
    }

    public ArrayList<String> getActors() {
        return actors;
    }

    public void setActors(ArrayList<String> actors) {
        this.actors = actors;
    }

    public void setSuggestion_links(ArrayList<String> suggestion_links) {
        this.suggestion_links = suggestion_links;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void addPurpose(String key, String value) {
        if (!purposes.containsKey(key)) {
            purposes.put(key, new ArrayList<String>());
        }
        purposes.get(key).add(value);
    }

    public void addActor(String name) {
        actors.add(name);
    }
    
    @Override
    public JSONObject toJSONObject(){
        JSONObject object = new JSONObject();
        object.put("id", id);
        object.put("name", name);
        object.put("annotation", annotation);
        object.put("rating", rating);
        object.put("actors", actors);
        object.put("purposes", purposes);
        object.put("suggestions", suggestion_links);
        return object;
    }

    @Override
    public String toString() {
        return toJSONString();
    }

    @Override
    public String toJSONString() {
        return toJSONObject().toJSONString();
    }

    @Override
    public boolean equals(Object obj) {
        Film with = (Film) obj;
        return toString().equals(with.toString());
    }

    public void addSuggestionLink(String link) {
        suggestion_links.add(link);
    }

    @Override
    public int compareTo(Film with) {
        return id.compareTo(with.getId());
    }

    @Override
    public boolean refreshStateFromJSONString(String s) {
        Map<String, Object> map;
        try {
            map = (Map<String, Object>) ConfigParser.getJSONParser().parse(s, ConfigParser.getContainerFactory());
        } catch (ParseException e) {
            return false;
        }
        for(Entry<String, Object> entry : map.entrySet()){
            switch (entry.getKey()) {
            case "id":
                setId((String) entry.getValue());
                break;
            case "name":
                setName((String) entry.getValue());
                break;
            case "annotation":
                setAnnotation((String) entry.getValue());
                break;
            case "rating":
                setRating((String) entry.getValue());
                break;
            case "actors":
                setActors((ArrayList<String>) entry.getValue());
                break;
            case "suggestions":
                setSuggestion_links((ArrayList<String>) entry.getValue());
                break;
            case "purposes":
                setPurposes((TreeMap<String, ArrayList<String>>) entry.getValue());
                break;
            }
        }
        return true;
    }

}
