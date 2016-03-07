package ru.kpsug.server;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import ru.kpsug.server.Suggestions.SuggestionsResult;

public class MyProtoFormatter {
	private static List<String> parseToLexems(String s) {
		List<String> words = new ArrayList<>();
		StringTokenizer tokenizer = new StringTokenizer(s);
		while (true) {
			try {
				words.add(tokenizer.nextToken("="));
			} catch (NoSuchElementException excp) {
				break;
			}
		}
		return words;
	}

	public static MyProtoRequest parse(String s) {
		MyProtoRequest result = null;
		List<String> words = parseToLexems(s);
		if (words.size() >= 3) {
			int type, id;
			try {
				type = Integer.parseInt(words.get(0));
				id = Integer.parseInt(words.get(1));
				int temp = Integer.parseInt(words.get(2));
			} catch (NumberFormatException excp) {
				return null;
			}
			result = new MyProtoRequest(type, id, words.get(2));
		}
		return result;
	}

	public static String makeResponse(MyProtoRequest request, SuggestionsResult suggestions_result) {
		if (suggestions_result == null) {
			return makeError();
		}
		String result = "";
		if (request.getType() == 0) {
			result += suggestions_result.toString();
		} else {

		}
		return result;
	}

	public static String makeError() {
		return "&&&ERROR&&&";
	}
}
