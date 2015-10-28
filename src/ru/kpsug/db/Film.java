package ru.kpsug.db;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.junit.runners.ParentRunner;

import ru.kpsug.server.Suggestions;

public class Film implements Comparable<Film> {
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
    public String toString() {
        String spec1 = "$$";
        String spec2 = "::";

        String result = "(";
        result += ("id" + spec1 + id + spec2);
        result += ("name" + spec1 + name + spec2);
        result += ("annotation" + spec1 + annotation + spec2);
        result += ("rating" + spec1 + rating + spec2);
        result += ("actors" + spec1 + actors + spec2);
        result += ("purposes" + spec1 + purposes + spec2);
        result += ("suggestions" + spec1 + suggestion_links);
        result += ")";
        return result;
    }

    private static ArrayList<String> parseArrayList(String s) {
        ArrayList<String> result = new ArrayList<>();
        for (String nex : (s.substring(1, s.length() - 1)).split(", ")) {
            result.add(nex);
        }
        return result;
    }

    private static TreeMap<String, ArrayList<String>> parsePurposes(String s) {
        TreeMap<String, ArrayList<String>> result = new TreeMap<String, ArrayList<String>>();
        Matcher matcher = Pattern.compile("(^| )([^,]*)=(\\[.*?\\])").matcher(
                s.substring(1, s.length() - 1));
        while (matcher.find()) {
            result.put(matcher.group(2), parseArrayList(matcher.group(3)));
        }
        return result;
    }

    public static Film parse(String s) {
        String spec1 = "::";
        String spec2 = "\\$\\$";

        Film film = new Film();
        StringTokenizer tokenizer = new StringTokenizer(s.substring(1,
                s.length() - 1));
        String token;
        while (true) {
            try {
                token = tokenizer.nextToken(spec1);
            } catch (NoSuchElementException excp) {
                break;
            }
            String[] key_value = token.split(spec2);
            switch (key_value[0]) {
            case "id":
                film.setId(key_value[1]);
                break;
            case "name":
                film.setName(key_value[1]);
                break;
            case "annotation":
                film.setAnnotation(key_value[1]);
                break;
            case "rating":
                film.setRating(key_value[1]);
                break;
            case "actors":
                film.setActors(parseArrayList(key_value[1]));
                break;
            case "suggestions":
                film.setSuggestion_links(parseArrayList(key_value[1]));
                break;
            case "purposes":
                film.setPurposes(parsePurposes(key_value[1]));
                break;
            }
        }
        return film;
    }

    public void print(OutputStream stream) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(stream);
        writer.append(toString());
        writer.flush();
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
}
