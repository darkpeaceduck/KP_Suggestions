package ru.kpsug;

import java.io.IOException;

import org.jsoup.nodes.Document;

import ru.kpsug.kp.PageLoader;
import ru.kpsug.kp.Search;

public class SearchTest {
    public static void main(String[] args) {
        try {
            Document doc = PageLoader.loadPrefixSearch("зелёная миля");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // System.out.println(Search.mainSearch(token));
    }

}
