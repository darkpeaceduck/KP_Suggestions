package tryurl;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TreeMap;
public class HttpParser {
    
    static private class Pair{
        private String content;
        private Node child;
        public String getContent() {
            return content;
        }
        public void setContent(String content) {
            this.content = content;
        }
        public Node getChild() {
            return child;
        }
        public void setChild(Node child) {
            this.child = child;
        }
        private Pair(String content_, Node child_){
            content = content_;
            child = child_;
        }
    }
    
    private class Tokenizer{
        private String src;
        private int position;
        private Tokenizer(String s) {
            src = s;
            position = 0;
        }
        private String nextToken(String delim){
            if(position == src.length()){
                throw new NoSuchElementException();
            }
            
            StringBuilder builder = new StringBuilder();
            while(position != src.length() && !delim.contains("" + src.charAt(position))){
                builder.append(src.charAt(position));
                position++;
            }
            return builder.toString();
        }
        
        private String nextToken(){
            return nextToken(" ");
        }
        
        private void pushPrefix(String prefix){
            src = src.substring(0, position).concat(prefix.concat(src.substring(position)));
        }
    }
    
    private class Node{
        ArrayList<Pair> children;
        String tagName;
        boolean isClosed;
        TreeMap<String, String> attr;
       
        public boolean isClosed() {
            return isClosed;
        }

        public void setClosed(boolean isClosed) {
            this.isClosed = isClosed;
        }

        private void addChild(Pair child){
            children.add(child);
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        private  void addChildren(ArrayList<Pair> newChildren){
             children.addAll(newChildren);
        }
        
        private void addAttr(String key, String value){
            attr.put(key, value);
        }

        public Node(String TagName_){
            tagName = TagName_;
            children = new ArrayList<>();
            attr = new TreeMap<String, String>();
            isClosed = false;
        }

        public String getTagName() {
            return tagName;
        }

        public ArrayList<Pair> getChildren() {
            return children;
        }

        public TreeMap<String, String> getAttr() {
            return attr;
        }
        
        
       
        
        private void print() throws IOException{
            for(int i = 0; i < print_intent; i++){
                current_intent += " "; 
            }
            writer.write(current_intent + "<" + tagName);
            for(Entry<String, String> entry : attr.entrySet()){
               writer.write(" " + entry.getKey() + "=" + entry.getValue() + " ");
            }
            writer.write(">\n");
            for(Pair pchild: children){
                writer.write(current_intent + pchild.content + "\n");
                if(pchild.child != null)
                    pchild.child.print();
            }
            writer.write(current_intent + "</" + tagName + ">\n");
            current_intent = current_intent.substring(current_intent.length() - print_intent);
        }
    }
    
    Tokenizer tokenizer;
    Node root;
    private int print_intent = 4;
    private String current_intent = "";
    
    private void setPrintIntent(int value){
        print_intent = value;
    }
    
    private void parseHtmlBody(Node parent){
          while(true){
              String content = tokenizer.nextToken("<");
              Node new_child = parseTag();
              if(new_child.isClosed){
                  parent.addChild(new Pair(content, null));
                  break;
              }
              parent.addChild(new Pair(content, new_child));
              
          }
    }
    
    
    private Node parseTag(){
        Node ret  = new Node(tokenizer.nextToken("/>="));
        if(ret.getTagName().charAt(0) == '<'){
            ret.setTagName(ret.getTagName().substring(1));
        }
        if(ret.tagName.equals("")){
            ret.setTagName(tokenizer.nextToken(">="));
            ret.setClosed(true);
        }
        while(true){
            try{
                String atrrName = tokenizer.nextToken(" =<");
                if(atrrName.charAt(0) == '>'){
                    tokenizer.pushPrefix(atrrName.substring(1));
                    break;
                }
                String arrrValue = tokenizer.nextToken(" =");
                ret.addAttr(atrrName, arrrValue);
            }catch(NoSuchElementException excp){
                break;
            }
        }
        if(!ret.isClosed()){
            parseHtmlBody(ret);
        }
        return ret;
    }
    
    public void buildTree(String s){
         tokenizer = new Tokenizer(s);
         root = parseTag();
    }
    
    OutputStreamWriter writer;  
    
    public void printTree(OutputStream stream_input) throws IOException{   
        writer = new OutputStreamWriter(stream_input);
        root.print();
        writer.flush();
        writer.close();
        
    }
}
