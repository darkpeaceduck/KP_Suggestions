package ru.kpsug.server;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class RequestDispatcher {
    private static String parseWordsAndRecall(ArrayList<String> words,
            RequestHandler caller) {
        if (words.size() >= 2) {
            String id = words.get(0);
            int depth = Integer.parseInt(words.get(1));
            return caller.processDb(id, depth);
        }
        return null;
    }

    private static ArrayList<String> parseToLexems(String s) {
        StringTokenizer tokenizer = new StringTokenizer(s);
        ArrayList<String> words = new ArrayList<>();
        while (true) {
            try {
                words.add(tokenizer.nextToken("="));
            } catch (NoSuchElementException excp) {
                break;
            }
        }
        return words;
    }

    public static String dispatch(String s, RequestHandler caller) {

        return parseWordsAndRecall(parseToLexems(s), caller);
    }
}
