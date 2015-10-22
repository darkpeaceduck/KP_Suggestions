package ru.kpsug.server;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class RequestDispatcher {
    private String parseWordsAndRecall(ArrayList<String> words, RequestHandler caller){
         if(words.size() >= 2){
             String id = words.get(0);
             int depth = Integer.parseInt(words.get(1));
             return caller.processDb(id, depth);
         }
         return null;
    }
    public void dispatch(String s, RequestHandler caller){
        StringTokenizer tokenizer = new StringTokenizer(s);
        ArrayList<String> words = new ArrayList<>();
        while(true){
            try{
                words.add(tokenizer.nextToken("="));
            }catch(NoSuchElementException excp){
                break;
            }
        }
        parseWordsAndRecall(words, caller);
    }
}
