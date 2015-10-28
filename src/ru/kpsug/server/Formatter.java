package ru.kpsug.server;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class Formatter {
    private static ArrayList<String> parseToLexems(String s){
        ArrayList<String> words = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(s);
        while (true) {
            try {
                words.add(tokenizer.nextToken("="));
            } catch (NoSuchElementException excp) {
                break;
            }
        }
        return words;    
    }
    
    public static Request parse(String s) {
        Request result = null;
        ArrayList<String> words = parseToLexems(s);
        if(words.size() >= 3){
            int type, id;
            try{
                type = Integer.parseInt(words.get(0));
                id = Integer.parseInt(words.get(1));
                int temp = Integer.parseInt(words.get(2));
            } catch(NumberFormatException excp){
                return null;
            }
            result = new Request(type, id, words.get(2));
        }
        return result;
    }
    
    public static String makeResponse(Request request, Suggestions suggestions){
        if(suggestions.isFailed()){
            return makeError();
        }
        String result = "";
        if(request.getType() == 0){
            result += suggestions.getStringDepthSet();
        } else if(request.getType() == 1){
            result += suggestions.getStringDepthMap();
        } else if(request.getType() == 2){
            result += suggestions.getGraph().toString();
        }  else {
            return makeError();
        }
        return result;
    }
    
    public static String makeError(){
        return "&&&ERROR&&&";
    }
}
