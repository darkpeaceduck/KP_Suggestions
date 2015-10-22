package ru.kpsug.server;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class RequestFormatter {
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
            result = new Request(Integer.parseInt(words.get(0)), Integer.parseInt(words.get(1)), words.get(2));
        }
        return result;
    }
    
    public static String makeResponse(Request request, Suggestions suggestions){
        String result = "";
        if(request.getType() == 0){
            result += suggestions.getDepthSet().toString();
        } else if(request.getType() == 1){
            result += suggestions.getDepthMap().toString();
        } else {
            result += suggestions.getGraph().toString();
        }
        return result;
    }
    
    public static String makeError(){
        return "";
    }
}
