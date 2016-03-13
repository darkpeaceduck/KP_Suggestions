package ru.kpsug.kp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ru.kpsug.db.Film;
import ru.kpsug.utils.ParseUtils;

public class KpParser {
	private static Document removeSpecialChars(Document doc) {
		return Jsoup.parse(doc.html().replaceAll("&.*?;", " ").replaceAll("'", "\""));
	}

	private static void parseSuggestions(Film film, Document doc) {
		Elements films = doc.getElementsByAttributeValueStarting("id", "tr_");
		if (!films.isEmpty()) {
			for (Element new_sug : films) {
				film.addSuggestionLink(new_sug.attr("id").substring(3));
			}
		}
	}

	public static class FuckupException extends RuntimeException {

		/**
		 * 
		 */
		public FuckupException(String doc) {
			super(doc);
		}

		private static final long serialVersionUID = -8661040999826285826L;

	}

	private static void indetificateFuckup(Document doc) throws FuckupException {
		String rawHtml = doc.html();
		if (rawHtml.contains(
				"Если вы видите эту страницу, значит с вашего IP-адреса поступило необычно много запросов.")) {
			throw new FuckupException(rawHtml);
		}
	}

	private static void parsePurposes(Film film, Document doc) {
		Elements info = doc.getElementsByClass("info");
		if (!info.isEmpty() && !info.first().children().isEmpty()) {
			Element info_block = info.first().children().first();
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
	}

	private static void parseActors(Film film, Document doc) {
		Element actors_block = doc.getElementById("actorList").getElementsByTag("ul").first();
		if (actors_block == null) {
			return;
		}
		Elements actors_list = actors_block.getElementsByAttributeValue("itemprop", "actors");
		if (actors_list == null) {
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
		Element description = doc.getElementsByAttributeValue("itemprop", "description").first();
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
		StringTokenizer tokenizer = new StringTokenizer(
				doc.getElementsByAttributeValue("rel", "canonical").attr("href"));
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

	public static Film parseFilm(Document doc, Document doc_suggestions) throws FuckupException {
		Film film = new Film();
		doc = removeSpecialChars(doc);
		indetificateFuckup(doc);
		doc_suggestions = removeSpecialChars(doc_suggestions);
		parseId(film, doc);
		parseName(film, doc);
		parsePurposes(film, doc);
		parseActors(film, doc);
		parseAnnotation(film, doc);
		parseRating(film, doc);
		parseSuggestions(film, doc_suggestions);
		return film;
	}

	public static String parseFilmIdFromMainSearchLink(String link) {
		Matcher mt = Pattern.compile("film/([0-9]*)").matcher(link);
		if (mt.find()) {
			return mt.group(1);
		}
		return null;
	}

	public static List<Film> parseMainSearch(Document doc) {
		doc = removeSpecialChars(doc);
		List<Film> result = new ArrayList<Film>();
		Elements elem = doc.getElementsByClass("search_results");
		// in older version here was doc.select
		Element films = null;
		for (Element item : elem) {
			if (item.classNames().contains("search_results_last")) {
				films = item;
			}
		}
		//
		if (films != null) {
			for (Element entry : films.children()) {
				if (entry.hasClass("element")) {
					Film film = new Film();
					Element info = entry.getElementsByClass("info").first();
					Element name = info.getElementsByClass("name").first();
					Elements gray = info.getElementsByClass("gray");
					Element year = name.getElementsByTag("span").first();
					film.setId(parseFilmIdFromMainSearchLink(name.getElementsByTag("a").first().attr("href")));
					film.setName(name.getElementsByTag("a").first().html());
					if (year != null) {
						film.addPurpose("год", name.getElementsByTag("span").first().html());
					}
					String rating = entry.getElementsByClass("right").first().child(0).attr("title").split(" ")[0];
					if (rating.length() > 0) {
						film.setRating(rating);
					}
					Element director_inner = info.getElementsByClass("director").first();
					if (director_inner != null) {
						film.addPurpose("режиссер", director_inner.child(0).html());
					}
					film.addPurpose("страна", gray.get(1).html().split("(,)|(\\.\\.\\.)")[0]);
					Elements actorsElements = gray.get(2).getElementsByClass("lined");
					for (Element actor : actorsElements) {
						String actor_name = actor.html();
						if (!actor_name.equals("...")) {
							film.addActor(actor_name);
						}
					}
					result.add(film);
				}
			}
		}
		return result;
	}

	public static List<Film> parsePrefixSearch(Document doc) {
		doc = removeSpecialChars(doc);
		Map<String, Object> map;
		try {
			String s = doc.body().html();
			s = s.substring(1, s.length() - 1);
			map = (Map<String, Object>) ParseUtils.getJSONParser().parse(s, ParseUtils.getContainerFactory());
		} catch (ParseException e) {
			return null;
		}
		List<Film> result = new ArrayList<Film>();
		for (Entry<String, Object> entry : map.entrySet()) {
			if (entry.getKey().equals("query_id")) {
				continue;
			}
			Map<String, String> inner_map = (Map<String, String>) entry.getValue();
			if (inner_map.get("link").matches("(.*)(film)(.*)")) {
				Film new_film = new Film();
				for (Entry<String, String> inner_entry : inner_map.entrySet()) {
					switch (inner_entry.getKey()) {
					case "id":
						new_film.setId(inner_entry.getValue());
						break;
					case "rus":
						new_film.setName(inner_entry.getValue());
						break;
					case "ur_rating":
						new_film.setRating(String.valueOf(inner_entry.getValue()));
						break;
					case "year":
						new_film.addPurpose("год", inner_entry.getValue());
						break;
					}
				}
				result.add(new_film);
			}
		}
		return result;
	}

}
