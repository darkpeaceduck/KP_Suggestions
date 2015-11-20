package ru.kpsug;

import java.io.IOException;
import java.util.TreeMap;

import org.json.simple.parser.ParseException;
import org.jsoup.nodes.Document;

import ru.kpsug.kp.KpParser;
import ru.kpsug.kp.PageLoader;
import ru.kpsug.kp.Search;
import ru.kpsug.utils.ConfigParser;

public class SearchTest {
    public static void main(String[] args) {
        try {
            System.out.println(KpParser.parseMainSearch(PageLoader.loadMainSearch("ты")));
//                Document doc = PageLoader.loadPrefixSearch("������ ����");
//                KpParser.parseMainSearch(PageLoader.loadMainSearch("������ ����"));
//            KpParser.parse
//                System.out.println(doc.body().html());
//                String s = doc.body().html();
//                s = s.substring(1, s.length() - 1);
//                Object object= ConfigParser.getJSONParser().parse(s, ConfigParser.getContainerFactory());
//                TreeMap<String, Object> map = (TreeMap<String, Object>) object;
//                Object value = map.get("0");
//                map = (TreeMap<String, Object>) value;
//                System.out.println(value);
            } catch (IOException e) {
                System.out.println("NOPE!");
            }
//         System.out.println(Search.mainSearch(token));
    }

}
