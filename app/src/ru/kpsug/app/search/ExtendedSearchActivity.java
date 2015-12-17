package ru.kpsug.app.search;

import java.util.ArrayList;
import java.util.List;


import ru.kpsug.app.R;
import ru.kpsug.app.film.FilmDetailsActivity;
import ru.kpsug.app.film.FilmStringPretty;
import ru.kpsug.db.Film;
import ru.kpsug.kp.Search;
import ru.kpsug.kp.Search.SearchException;
import ru.kpsug.kp.Search.SearchResult;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ExtendedSearchActivity extends AppCompatActivity {
    private LinearLayout lm;
    private List<Film> result = null;
    private AsyncTask<String, Object, ArrayList<Film>> searchLoadingTask = new AsyncTask<String, Object, ArrayList<Film>>() {
        @Override
        protected ArrayList<Film> doInBackground(String... params) {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            ArrayList<Film> films = new ArrayList<Film>();
            try {
                SearchResult result = Search.mainSearch(params[0]);
                films = result.getFilms();
            } catch (SearchException e) {
                e.printStackTrace();
            }
            return films;
        }

        protected void onPostExecute(ArrayList<Film> result) {
            ExtendedSearchActivity.this.result = result;
            applyResultChanges();
        };
    };

    private void applyResultChanges() {
        for (final Film item : result) {
            View v = LayoutInflater.from(ExtendedSearchActivity.this).inflate(
                    R.layout.list_item, null);
            TextView product = (TextView) v.findViewById(R.id.tadaText);
            product.setText(FilmStringPretty.prefixPrint(item));
            product.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ExtendedSearchActivity.this,
                            FilmDetailsActivity.class);
                    intent.putExtra("id", item.getId());
                    startActivity(intent);
                }
            });
            lm.addView(v);
        }
        ((ProgressBar) findViewById(R.id.progressBar1))
                .setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extended_search);
        lm = (LinearLayout) findViewById(R.id.o2);
        String searchWord = getIntent().getStringExtra("word");
        searchLoadingTask.execute(searchWord);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.extended_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
