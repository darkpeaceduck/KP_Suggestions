package ru.kpsug.kp;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ru.kpsug.db.Film;

public class KpParser {
    private static Document removeSpecialChars(Document doc) {
        return Jsoup.parse(doc.html().replaceAll("&.*?;", " ").replaceAll("'", "\""));
    }

    private static void parseSuggestions(Film film, Document doc) {
        Elements films = doc.getElementsByAttributeValueStarting("id", "tr_");
        for (Element new_sug : films) {
            film.addSuggestionLink(new_sug.attr("id").substring(3));
        }
    }

    private static void parseInfoBlock(Film film, Document doc) {
        Element info_block = doc.getElementsByClass("info").first().children()
                .first();
        for (Element child : info_block.children()) {
            String purp_name = child.getElementsByClass("type").first().html();
            if (purp_name.equals("рейтинг MPAA")) {
                continue;
            }
            for (Element td : child.children()) {
                if (td.className() != "type") {
                    Elements as = td.getElementsByTag("a");
                    if (as != null) {
                        for (Element a : as) {
                            if (a.html().equals("...")) {
                                break;
                            }
                            film.addPurpose(purp_name, a.html());
                        }
                    } else {
                        film.addPurpose(purp_name, td.html());
                    }
                }
            }
        }
    }

    private static void parseActors(Film film, Document doc) {
        Element actors_block = doc.getElementById("actorList").getElementsByTag("ul").first();
        if(actors_block == null){
            return;
        }
        Elements actors_list = actors_block.getElementsByAttributeValue("itemprop", "actors");
        if(actors_list == null){
            return;
        }
        for (Element actor : actors_list) {
            String actor_str = actor.getElementsByTag("a").html();
            if (actor_str.equals("...")) {
                break;
            }
            film.addActor(actor_str);
        }
    }

    private static void parseAnnotation(Film film, Document doc) {
        Element description = doc.getElementsByAttributeValue("itemprop",
                "description").first();
        if (description == null) {
            film.setAnnotation("");
        } else {
            String result = description.html();
            result = result.replaceAll("<br>", "");
            result = result.replaceAll("\n", "");
            film.setAnnotation(result);
        }
    }

    private static void parseName(Film film, Document doc) {
        Element elem = doc.getElementsByTag("title").first();
        if (elem != null) {
            film.setName(elem.html());
        } else {
            film.setName("");
        }
    }

    private static void parseId(Film film, Document doc) {
        StringTokenizer tokenizer = new StringTokenizer(doc
                .getElementsByAttributeValue("rel", "canonical").attr("href"));
        String result = "";
        while (true) {
            try {
                result = tokenizer.nextToken("/");
            } catch (NoSuchElementException exsp) {
                break;
            }
        }
        film.setId(result);
    }

    private static void parseRating(Film film, Document doc) {
        Element rating = doc.getElementsByClass("rating_ball").first();
        if (rating == null) {
            film.setRating("0");
        } else {
            film.setRating(rating.html());
        }
    }

    public static Film parseFilm(Document doc, Document doc_suggestions) {
        Film film = new Film();
        doc = removeSpecialChars(doc);
        doc_suggestions = removeSpecialChars(doc_suggestions);
        parseId(film, doc);
        parseName(film, doc);
        parseInfoBlock(film, doc);
        parseActors(film, doc);
        parseAnnotation(film, doc);
        parseRating(film, doc);
        parseSuggestions(film, doc_suggestions);
        return film;
    }
    
    public static String parseFilmIdFromMainSearchLink(String link){
         Matcher mt = Pattern.compile("film/([0-9]*)").matcher(link);
         if(mt.find()){
             return mt.group(1);
         }
         return null;
    }
    
    public static ArrayList<String> parseMainSearch(Document doc){
        doc = removeSpecialChars(doc);
        ArrayList<String> result = new ArrayList<String>();
        Element films = doc.select(".search_results.search_results_last").first();
        if(films != null){
            for(Element entry : films.children()){
                if(entry.hasClass("element")){
                    Element name = entry.getElementsByClass("info").first().getElementsByClass("name").first();
                    String film_link = name.getElementsByTag("a").first().attr("href");
                    result.add(parseFilmIdFromMainSearchLink(film_link));
                }
            }   
        }
        return result;
    }

}
