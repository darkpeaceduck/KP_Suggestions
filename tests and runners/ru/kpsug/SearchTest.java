package ru.kpsug;

import java.io.IOException;
import java.util.TreeMap;

import org.json.simple.parser.ParseException;
import org.jsoup.nodes.Document;

import ru.kpsug.db.DBOperator;
import ru.kpsug.db.Film;
import ru.kpsug.kp.KpParser;
import ru.kpsug.kp.PageLoader;
import ru.kpsug.kp.Search;
import ru.kpsug.kp.PageLoader.PageLoaderException;
import ru.kpsug.utils.ConfigParser;

public class SearchTest {
    public static void main(String[] args) throws Exception {
        System.setProperty("socksProxyHost", "127.0.0.1"); // set proxy server
        System.setProperty("socksProxyPort", "12345");
        Film film = KpParser.parseFilm(PageLoader.loadFilm("30012"), PageLoader.loadFilmSuggestions("30012"));
        System.out.println(film);
    }

}
