package tryurl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Film {
    private ArrayList<String> suggestion_links = new ArrayList<>();
    private String id = null;
    String name = null;
    String annotation = null;
    String rating = null;
    private TreeMap<String, ArrayList<String> > purposes = new TreeMap<>();
    private ArrayList<String> actors =new ArrayList<String>();
    
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
    
    public void addPurpose(String key, String value){
        if(!purposes.containsKey(key)){
            purposes.put(key, new ArrayList<String>());
        }
        purposes.get(key).add(value);
    }
    
    public void addActor(String name){
        actors.add(name);
    }
    
    
    public void print(OutputStream stream) throws IOException{
        OutputStreamWriter writer = new OutputStreamWriter(stream);
        writer.append("id = " + id + "\n");
        writer.append("name = " + name + "\n");
        writer.append("annotation = " + annotation + "\n");
        writer.append("rating = " + rating + "\n");
        writer.append("actors = ");
        for(String name : actors){
            writer.append(name + ", ");
        }
        writer.append("\n");
        writer.append("purposes = ");
        for(Entry<String, ArrayList<String>> purp : purposes.entrySet()){
            writer.append("( " + purp.getKey() + " :");
            for(String value : purp.getValue()){
                writer.append(value + ", ");
            }
            writer.append(" ), ");
        }
        writer.append("\n");
        writer.append("suggestions = ");
        for(String sug : suggestion_links){
            writer.append(sug + " ,");
        }
        writer.append("\n");
        writer.flush();
    }
    
    public void addSuggestionLink(String link){
        suggestion_links.add(link);
    }
}
