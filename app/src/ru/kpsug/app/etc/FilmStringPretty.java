package ru.kpsug.app.etc;

import java.util.LinkedList;
import java.util.List;

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
}
