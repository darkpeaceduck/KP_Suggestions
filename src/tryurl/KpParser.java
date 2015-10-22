package tryurl;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class KpParser {
    private static Document removeSpecialChars(Document doc){
        return Jsoup.parse(doc.html().replaceAll("&.*?;", " "));
    }
    private static void parseSuggestions(Film film, Document doc){
         Elements films = doc.getElementsByAttributeValueStarting("id", "tr_");
         for(Element new_sug : films){
             film.addSuggestionLink(new_sug.attr("id").substring(3));
         }
    }
    private static void parseInfoBlock(Film film, Document doc) {
        Element info_block = doc.getElementsByClass("info").first().children().first();
        for (Element child : info_block.children()) {
            String purp_name = child.getElementsByClass("type").first().html();
            if(purp_name.equals("рейтинг MPAA")){
                continue;
            }
            for (Element td : child.children()) {
                if (td.className() != "type") {
                    Elements as = td.getElementsByTag("a");
                    if (as != null) {
                        for (Element a : as) {
                            if(a.html().equals("...")){
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
        Element actors_block = doc.getElementById("actorList");
        Elements actors_list = actors_block.getElementsByTag("ul").first()
                .getElementsByAttributeValue("itemprop", "actors");
        for (Element actor : actors_list) {
            String actor_str = actor.getElementsByTag("a").html();
            if(actor_str.equals("...")){
                break;
            }
            film.addActor(actor_str);
        }
    }
    
    private static void parseAnnotation(Film film, Document doc){
        Element description = doc.getElementsByAttributeValue("itemprop", "description").first();
        if(description == null){
            film.setAnnotation("");
        }else{
            String result = description.html();
            result = result.replaceAll("<br>", "");
            result = result.replaceAll("\n", "");
            film.setAnnotation(result);
        }
    }
    
    private static void parseName(Film film, Document doc){
        Element elem = doc.getElementsByTag("title").first();
        if(elem != null){
            film.setName(elem.html());
        } else {
            film.setName("");
        }
    }
    
    private static void parseId(Film film, Document doc){
        StringTokenizer tokenizer = new StringTokenizer(doc.getElementsByAttributeValue("rel", "canonical").attr("href"));
        String result = "";
        while(true){
            try{
                result = tokenizer.nextToken("/");
            }catch(NoSuchElementException exsp){
                break;
            }
        }
        film.setId(result);
    }
    
    private static void parseRating(Film film, Document doc){
        Element rating = doc.getElementsByClass("rating_ball").first();
        if(rating == null){
            film.setRating("0");
        } else  {
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

}
