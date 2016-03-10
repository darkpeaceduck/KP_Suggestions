package ru.kpsug.app.etc;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import ru.kpsug.db.Film;

public class FilmStringPretty {
	public static String prefixPrint(Film film) {
		List<String> values = new LinkedList<>();
		if (film.getPurposes().containsKey("год")) {
			values.add(film.getPurposes().get("год").get(0));
		}
		if (film.getPurposes().containsKey("страна")) {
			values.add(film.getPurposes().get("страна").get(0));
		}

		if (film.getPurposes().containsKey("режиссер")) {
			values.add(film.getPurposes().get("режиссер").get(0));
		}

		if (film.getRating() != null) {
			values.add(film.getRating());
		}

		return film.getName() + "(" + StringUtils.join(values, ",") + ")";
	}

	public static String purposesPrint(Film film) {
		Map<String, List<String>> purposes = film.getPurposes();
		StringBuilder stringBuilder = new StringBuilder();
		for (Map.Entry<String, List<String>> entry : purposes.entrySet()) {
			stringBuilder.append(entry.getKey().toUpperCase());
			stringBuilder.append(" : ");
			stringBuilder.append(StringUtils.join(entry.getValue(), ", "));
			stringBuilder.append("\n\n");
		}
		return stringBuilder.toString();
	}

	public static String actorsPrint(Film film) {
		return StringUtils.join(film.getActors(), "\n");
	}
}
