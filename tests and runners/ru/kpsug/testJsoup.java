package ru.kpsug;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ru.kpsug.kp.PageLoader;

public class testJsoup {
	public static void main(String[] args) {
		try {
			Document doc = PageLoader.loadMainSearch("зелен");
			Elements elem = doc.getElementsByClass("search_results");
			System.out.println(elem.isEmpty());
			for (Element item : elem) {
				if (item.classNames().contains("search_results_last")) {
					System.out.println("OK");
				}
			}
		} catch (Exception e) {
			System.out.println("fuck");
			e.printStackTrace();
		}
	}
}
