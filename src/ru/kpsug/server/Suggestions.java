package ru.kpsug.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.kpsug.db.DBOperator;
import ru.kpsug.db.Film;
import ru.kpsug.kp.KpParser;
import ru.kpsug.kp.PageLoader;

public class Suggestions {
    public static class Edge{
        private String id_from;
        private String id_to;
        public Edge(String id_from, String id_to) {
            this.id_from = id_from;
            this.id_to = id_to;
        }
        
        public String getId_from() {
            return id_from;
        }

        public void setId_from(String id_from) {
            this.id_from = id_from;
        }

        public String getId_to() {
            return id_to;
        }

        public void setId_to(String id_to) {
            this.id_to = id_to;
        }

        @Override
        public String toString() {
            return "(" + id_from + " <-> " + id_to + ")"; 
        }
    }
    
    public static class Node {
        private Film content;
        private ArrayList<Node> children = new ArrayList<Node>();

        public Film getContent() {
            return content;
        }

        public void setContent(Film content) {
            this.content = content;
        }

        public ArrayList<Node> getChildren() {
            return children;
        }

        public void setChildren(ArrayList<Node> children) {
            this.children = children;
        }

        public Node(Film from) {
            content = from;
        }

        private void print(StringBuilder result, TreeSet<String> used) {
            used.add(content.getId());
            for (Node child : children) {
                if (!used.contains(child.content.getId())) {
                    result.append("(" + content.getId() + ","
                            + child.getContent().getId() + ")");
                    child.print(result, used);
                }
            }
        }
        
        public static ArrayList<Edge> parse(String s){
            Matcher m = Pattern.compile("\\((.*?),(.*?)\\)").matcher(s);   
            ArrayList<Edge> ret = new ArrayList<Suggestions.Edge>();
            while(m.find()){
                ret.add(new Edge(m.group(1), m.group(2)));
            }
            return ret;
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            TreeSet<String> used = new TreeSet<>();
            print(result, used);
            return result.toString();
        }
        
    }

    private Node root = null;
    private ArrayList<ArrayList<Film>> depth_set = null;
    private boolean is_failed = false;
    private static final int DEPTH_LIMIT = 3;

    public boolean isFailed() {
        return is_failed;
    }

    public Suggestions(String id, int depth, DBOperator db) {
        Film film = db.selectFilm(id);
        if (film != null && depth <= DEPTH_LIMIT) {
            root = new Node(film);
            depth_set = new ArrayList<ArrayList<Film>>();

            TreeMap<String, Integer> is_level = new TreeMap<String, Integer>();
            is_level.put(root.getContent().getId(), 0);
            ArrayList<Node> q = new ArrayList<Node>();
            q.add(root);
            int head_position = 0;

            depth_set.add(new ArrayList<Film>());
            depth_set.get(0).add(film);
            while (q.size() > head_position) {
                Node current_node = q.get(head_position++);
                Integer current_depth = is_level.get(current_node.getContent()
                        .getId());
                if (current_depth < depth) {
                    for (String link : current_node.getContent()
                            .getSuggestion_links()) {
                        if (!is_level.containsKey(link)) {
                            Film new_film = db.selectFilm(link);
                            is_level.put(new_film.getId(), current_depth + 1);
                            Node new_node = new Node(new_film);
                            q.add(new_node);
                            current_node.getChildren().add(new_node);
                            new_node.getChildren().add(current_node);
                            while (depth_set.size() <= current_depth + 1) {
                                depth_set.add(new ArrayList<Film>());
                            }
                            depth_set.get(current_depth + 1).add(new_film);
                        }
                    }
                }
            }
        } else {
            is_failed = true;
        }
    }

    public Node getGraph() {
        return root;
    }

    public ArrayList<ArrayList<Film>> getDepthSet() {
        return depth_set;
    }

    public TreeMap<Film, Integer> getDepthMap() {
        TreeMap<Film, Integer> ret = new TreeMap<>();
        for (Integer depth = 0; depth < depth_set.size(); depth++) {
            for (Film film : depth_set.get(depth)) {
                ret.put(film, depth);
            }
        }
        return ret;
    }

    public String getStringDepthMap() {
        String spec1 = "##";
        String spec2 = "@@";

        TreeMap<Film, Integer> result = getDepthMap();
        StringBuilder ret = new StringBuilder("{");
        for (Entry<Film, Integer> entry : result.entrySet()) {
            ret.append(entry.getKey().toString());
            ret.append(spec1);
            ret.append(entry.getValue());
            ret.append(spec2);
        }
        ret.append("}");
        return ret.toString();
    }

    public String getStringDepthSet() {
        String spec1 = "##";
        String spec2 = "@@";

        ArrayList<ArrayList<Film>> result = getDepthSet();
        StringBuilder ret = new StringBuilder("{");
        for (ArrayList<Film> entry : result) {
            for(Film film : entry){
                ret.append(film);
                ret.append(spec2);
            }
            ret.append(spec1);
        }
        ret.append("}");
        return ret.toString();
    }

    public static TreeMap<Film, Integer> parseDepthMap(String s) {
        String spec1 = "@@";
        String spec2 = "##";

        TreeMap<Film, Integer> result = new TreeMap<Film, Integer>();
        String[] items = s.substring(1, s.length() - 1).split(spec1);
        for (String item : items) {
            String[] key_value = item.split(spec2);
            result.put(Film.parse(key_value[0]), Integer.parseInt(key_value[1]));
        }
        return result;
    }

    public static ArrayList<ArrayList<Film>> parseDepthSet(String s) {
        String spec1 = "##";
        String spec2 = "@@";
        ArrayList<ArrayList<Film>> result = new ArrayList<ArrayList<Film>>();
        for (String nex : (s.substring(1, s.length() - 1)).split(spec1)) {
            ArrayList<Film> inner = new ArrayList<Film>();
            for(String go : nex.split(spec2)){
                inner.add(Film.parse(go));
            }
            result.add(inner);
        }
        return result;
    }

}
