package ru.kpsug.db;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import ru.kpsug.utils.JSONParceble;
import ru.kpsug.utils.MyParseUtils;

public class Film implements Comparable<Film>, JSONParceble {
	private List<String> suggestion_links = new ArrayList<>();
	private String id = null;
	private String name = null;
	private String annotation = null;
	private String rating = null;
	private Map<String, List<String>> purposes = new TreeMap<>();
	private List<String> actors = new ArrayList<String>();

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public List<String> getSuggestion_links() {
		return suggestion_links;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public Map<String, List<String>> getPurposes() {
		return purposes;
	}

	public void setPurposes(Map<String, List<String>> purposes) {
		this.purposes = purposes;
	}

	public List<String> getActors() {
		return actors;
	}

	public void setActors(List<String> actors) {
		this.actors = actors;
	}

	public void setSuggestion_links(List<String> suggestion_links) {
		this.suggestion_links = suggestion_links;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void addPurpose(String key, String value) {
		if (!purposes.containsKey(key)) {
			purposes.put(key, new ArrayList<String>());
		}
		purposes.get(key).add(value);
	}

	public void addActor(String name) {
		actors.add(name);
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject object = new JSONObject();
		object.put("id", id);
		object.put("name", name);
		object.put("annotation", annotation);
		object.put("rating", rating);
		object.put("actors", actors);
		object.put("purposes", purposes);
		object.put("suggestions", suggestion_links);
		return object;
	}

	@Override
	public String toString() {
		return toJSONString();
	}

	@Override
	public String toJSONString() {
		return toJSONObject().toJSONString();
	}

	@Override
	public boolean equals(Object obj) {
		Film with = (Film) obj;
		return toString().equals(with.toString());
	}

	public void addSuggestionLink(String link) {
		suggestion_links.add(link);
	}

	@Override
	public int compareTo(Film with) {
		return id.compareTo(with.getId());
	}

	@Override
	public boolean refreshStateFromJSONString(String s) {
		try {
			return refreshStateFromObject(MyParseUtils.getJSONParser().parse(s, MyParseUtils.getContainerFactory()));
		} catch (ParseException e) {
			return false;
		}
	}

	@Override
	public boolean refreshStateFromObject(Object object) {
		Map<Object, Object> map = (Map<Object, Object>) object;
		for (Entry<Object, Object> entry : map.entrySet()) {
			switch ((String) entry.getKey()) {
			case "id":
				setId((String) entry.getValue());
				break;
			case "name":
				setName((String) entry.getValue());
				break;
			case "annotation":
				setAnnotation((String) entry.getValue());
				break;
			case "rating":
				setRating((String) entry.getValue());
				break;
			case "actors":
				setActors((List<String>) entry.getValue());
				break;
			case "suggestions":
				setSuggestion_links((List<String>) entry.getValue());
				break;
			case "purposes":
				setPurposes((Map<String, List<String>>) entry.getValue());
				break;
			}
		}
		return true;
	}

	public static Comparator<Film> getFilmRatingComparator() {
		return new Comparator<Film>() {

			@Override
			public int compare(Film lhs, Film rhs) {
				String r1 = lhs.getRating();
				String r2 = rhs.getRating();
				if (r1 == null) {
					return (r2 == null ? 0 : 1);
				}
				if (r2 == null) {
					return -1;
				}
				int value = Double.compare(Double.parseDouble(r2), Double.parseDouble(r1));
				if (value != 0) {
					return value;
				}
				return lhs.compareTo(rhs);
			}
		};
	}

	public static Comparator<Film> getFilmYearComparator() {
		return new Comparator<Film>() {

			@Override
			public int compare(Film lhs, Film rhs) {
				List<String> r1 = lhs.getPurposes().get("год");
				List<String> r2 = rhs.getPurposes().get("год");
				if (r1 == null) {
					return (r2 == null ? 0 : 1);
				}
				if (r2 == null) {
					return -1;
				}
				int value = Integer.compare(Integer.parseInt(r2.get(0)), Integer.parseInt(r1.get(0)));
				if (value != 0) {
					return value;
				}
				return lhs.compareTo(rhs);
			}
		};
	}

}
