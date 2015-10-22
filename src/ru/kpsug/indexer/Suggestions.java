package ru.kpsug.indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import ru.kpsug.db.Film;

public class Suggestions {
    public static class Node{
        private Film content;
        private ArrayList<Node> children = new  ArrayList<Node>();
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
        public Node(Film from){
            content = from;
        }
    }
    
    private Node root = null;
    private ArrayList<ArrayList<Node>> depth_set = null;
    
    public void buildGraph(Film film, int depth) throws IOException{
        root = new Node(film); 
        depth_set = new ArrayList<ArrayList<Node> >();
        
        TreeMap<String, Integer> is_level= new TreeMap<>();
        is_level.put(root.getContent().getId(), 0);
        ArrayList<Node> q = new ArrayList<Node>();
        q.add(root);
        int head_position = 0;
        
        depth_set.add(new ArrayList<Node>());
        depth_set.get(0).add(root);
        while(q.size() > head_position){
            Node current_node = q.get(head_position++);
            Integer current_depth = is_level.get(current_node.getContent().getId());
            if(current_depth < depth){
                for(String link : current_node.getContent().getSuggestion_links()){
                    if(!is_level.containsKey(link)){
                        Film new_film = KpParser.parseFilm(PageLoader.loadFilm(link), PageLoader.loadFilmSuggestions(link));
                        is_level.put(new_film.getId(), current_depth + 1);
                        Node new_node = new Node(new_film);
                        q.add(new_node);
                        current_node.getChildren().add(new_node);
                        new_node.getChildren().add(current_node);
                        while(depth_set.size() <= current_depth + 1){
                            depth_set.add(new ArrayList<Node>());
                        }
                        depth_set.get(current_depth + 1).add(new_node);
                    }
                }
            }
        }
    }

    public Node getGraph() {
        return root;
    }

    public ArrayList<ArrayList<Node>> getDepthSet() {
        return depth_set;
    }
    
    public TreeMap<Node, Integer> getDepthMap(){
        TreeMap<Node,Integer>  ret = new TreeMap<Suggestions.Node, Integer>();
        for(Integer depth = 0; depth < depth_set.size(); depth++){
            for(Node node : depth_set.get(depth)){
                ret.put(node, depth);
            }
        }
        return ret;
    }
}
