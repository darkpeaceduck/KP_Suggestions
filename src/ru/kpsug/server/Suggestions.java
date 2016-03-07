package ru.kpsug.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import ru.kpsug.db.DBOperator;
import ru.kpsug.db.Film;
import ru.kpsug.utils.JSONParceble;
import ru.kpsug.utils.MyParseUtils;

public class Suggestions {
	public static class SuggestionsResult implements JSONParceble {
		private static final int MAX_LIMIT = 100;

		private Map<Integer, List<String>> levelsEdges = new TreeMap<Integer, List<String>>();
		private Map<String, Film> films = new TreeMap<String, Film>();

		public Map<Integer, List<String>> getLevelsEdges() {
			return levelsEdges;
		}

		public void setLevelsEdges(Map<Integer, List<String>> levelsEdges) {
			this.levelsEdges = levelsEdges;
		}

		public List<Film> getFilmsSortedByRating(int position, int limit) {
			List<Film> pagedFilms = new ArrayList<>();
			if (levelsEdges.get(position) != null)
				for (String id : levelsEdges.get(position)) {
					if (limit == 0) {
						break;
					}
					limit--;
					pagedFilms.add(films.get(id));
				}
			return pagedFilms;
		}

		public List<Film> getFilmSortedByYearMore(int position, int limit) {
			List<Film> films = getFilmsSortedByRating(position, MAX_LIMIT);
			Collections.sort(films, Film.getFilmYearComparator());
			return films.subList(0, Math.min(films.size(), limit));
		}

		public List<Film> getFilmSortedByYearLess(int position, int limit) {
			List<Film> films = getFilmSortedByYearMore(position, MAX_LIMIT);
			Collections.reverse(films);
			return films.subList(0, Math.min(films.size(), limit));
		}

		public void setLevelsEdgesFromListFilm(Map<Integer, List<Film>> levelsEdges) {
			for (Entry<Integer, List<Film>> entry : levelsEdges.entrySet()) {
				List<String> list = new ArrayList<String>();
				Collections.sort(entry.getValue(), Film.getFilmRatingComparator());
				int num = 0;
				for (Film film : entry.getValue()) {
					list.add(film.getId());
					num++;
					if (num > MAX_FILMS_ON_LEVEL) {
						break;
					}
				}
				this.levelsEdges.put(entry.getKey(), list);
			}
		}

		@Override
		public String toString() {
			return toJSONString();
		}

		@Override
		public String toJSONString() {
			return toJSONObject().toJSONString();
		}

		private void parseLevelsEdges(Object object) {
			Map<String, List<String>> result = ((Map<String, List<String>>) object);
			Map<Integer, List<String>> outp = new TreeMap<Integer, List<String>>();
			for (Entry<String, List<String>> item : result.entrySet()) {
				outp.put(Integer.parseInt(item.getKey()), item.getValue());
			}
			setLevelsEdges(outp);
		}

		private void parseFilms(Object object) {
			Map<String, Object> map = (Map<String, Object>) object;
			Map<String, Film> new_films = new TreeMap<>();
			for (Entry<String, Object> entry : map.entrySet()) {
				Film film = new Film();
				film.refreshStateFromObject(entry.getValue());
				new_films.put(entry.getKey(), film);
			}
			setFilms(new_films);
		}

		@Override
		public JSONObject toJSONObject() {
			JSONObject object = new JSONObject();
			object.put("films", films);
			object.put("levelsEdges", levelsEdges);
			return object;
		}

		@Override
		public boolean refreshStateFromJSONString(String s) {
			try {
				return refreshStateFromObject(
						MyParseUtils.getJSONParser().parse(s, MyParseUtils.getContainerFactory()));
			} catch (ParseException e) {
				return false;
			}
		}

		@Override
		public boolean refreshStateFromObject(Object object) {
			Map<String, Object> map;
			try {
				map = (Map<String, Object>) object;
			} catch (ClassCastException excp) {
				return false;
			}
			for (Entry<String, Object> entry : map.entrySet()) {
				if (!(entry.getKey() instanceof String)) {
					return false;
				}
				switch (entry.getKey()) {
				case "films":
					parseFilms(entry.getValue());
					break;
				case "levelsEdges":
					parseLevelsEdges(entry.getValue());
					break;
				}
			}
			return true;
		}

		public Map<String, Film> getFilms() {
			return films;
		}

		public void setFilms(Map<String, Film> films) {
			this.films = films;
		}

	}

	private static final int DEPTH_LIMIT = 10;
	private static final int MAX_FILMS_ON_LEVEL = 50;
	private static final int MAX_RUNTIME_FILMS_ON_LEVEL = 100;

	private static Set<Film> getSetWithComp() {
		return new TreeSet<Film>(Film.getFilmRatingComparator());
	}

	public static SuggestionsResult mainSuggestionsMethod(String id, int depth, DBOperator db) {
		Film film = null;
		try {
			film = db.selectFilm(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		SuggestionsResult result = null;
		depth = Math.min(depth, DEPTH_LIMIT);
		if (film != null && depth <= DEPTH_LIMIT) {
			result = new SuggestionsResult();
			Map<String, Integer> is_level = new TreeMap<String, Integer>();
			Map<Integer, List<Film>> re_is_level = new TreeMap<Integer, List<Film>>();
			Map<String, Film> films = new TreeMap<String, Film>();
			is_level.put(film.getId(), 0);

			re_is_level.put(0, new ArrayList<Film>());
			re_is_level.get(0).add(film);

			List<Film> q = new ArrayList<Film>();
			q.add(film);
			films.put(film.getId(), film);

			int head_position = 0;
			while (q.size() > head_position) {
				Film current = q.get(head_position++);
				Integer current_depth = is_level.get(current.getId());
				if (current_depth < depth && !(re_is_level.containsKey(current_depth + 1)
						&& re_is_level.get(current_depth + 1).size() > MAX_RUNTIME_FILMS_ON_LEVEL)) {
					for (String link : current.getSuggestion_links()) {
						if (!is_level.containsKey(link)) {
							Film new_film;
							try {
								new_film = db.selectFilm(link);
							} catch (Exception e) {
								e.printStackTrace();
								continue;
							}
							is_level.put(new_film.getId(), current_depth + 1);

							if (!re_is_level.containsKey(current_depth + 1)) {
								re_is_level.put(current_depth + 1, new ArrayList<Film>());
							}
							re_is_level.get(current_depth + 1).add(new_film);

							q.add(new_film);
							films.put(link, new_film);
						}
					}
				}
			}
			result.setFilms(films);
			result.setLevelsEdgesFromListFilm(re_is_level);
		}
		return result;
	}

	public static SuggestionsResult getFilmsAround(String id, int depth, DBOperator db) {
		return mainSuggestionsMethod(id, depth, db);
	}
}
