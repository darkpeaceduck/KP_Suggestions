package ru.kpsug;

import ru.kpsug.db.Film;
import ru.kpsug.kp.KpParser;
import ru.kpsug.kp.PageLoader;

public class SearchTest {
	public static void main(String[] args) throws Exception {
		System.setProperty("socksProxyHost", "127.0.0.1"); // set proxy server
		System.setProperty("socksProxyPort", "12345");
		Film film = KpParser.parseFilm(PageLoader.loadFilm("30012"), PageLoader.loadFilmSuggestions("30012"));
		System.out.println(film);
	}

}
