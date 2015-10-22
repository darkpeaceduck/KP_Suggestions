package kp.indexer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.jsoup.nodes.Document;

public class Indexer implements Runnable {
    private int page_start;
    private int page_stop;
    private int id;
    DBOperator db;
    private OutputStreamWriter log;

    public Indexer(OutputStreamWriter log, int id, DBOperator db, int page_start, int page_stop) {
        super();
        this.db = db;
        this.log = log;
        this.id = id;
        this.page_start = page_start;
        this.page_stop = page_stop;
    }

    @Override
    public void run() {
        System.out.println("STARTING INDEXER " + String.valueOf(id));
        for (int current_page = page_start; current_page <= page_stop; current_page++) {
            boolean success = true;
            try {
                Film film = KpParser.parseFilm(
                        PageLoader.loadFilm(String.valueOf(current_page)),
                        PageLoader.loadFilmSuggestions(String.valueOf(current_page)));
                success = db.InsertFilm(film);
            } catch (Exception e) {
                success = false;
            }

            synchronized (log) {
                try {
                    log.append("PROGRESS OF INDEXER  " + String.valueOf(id) + " "
                            + String.valueOf(current_page) + "/"
                            + String.valueOf(page_stop) + " LAST WAS " + (success ? "SUCCESS\n" : "FAILED\n"));
                    log.flush();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
