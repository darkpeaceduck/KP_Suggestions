package ru.kpsug.app.search;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;

import ru.kpsug.app.R;
import ru.kpsug.app.R.id;
import ru.kpsug.app.R.layout;
import ru.kpsug.app.R.menu;
import ru.kpsug.app.film.FilmDetailsActivity;
import ru.kpsug.app.film.FilmStringPretty;
import ru.kpsug.db.Film;
import ru.kpsug.kp.KpParser;
import ru.kpsug.kp.PageLoader;
import ru.kpsug.kp.PageLoader.PageLoaderException;
import ru.kpsug.kp.Search;
import ru.kpsug.kp.Search.SearchException;
import ru.kpsug.kp.Search.SearchResult;
import android.support.v7.app.ActionBarActivity;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ExtendedSearchActivity extends Activity {
    private LinearLayout lm;
    private List<Film> result = null;
    private AsyncTask<String, Object, ArrayList<Film>> searchLoadingTask = new AsyncTask<String, Object, ArrayList<Film>>(){
        @Override
        protected ArrayList<Film> doInBackground(String... params) {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            ArrayList<Film> films = new ArrayList<Film>();
            try {
                SearchResult result= Search.mainSearch(params[0]);
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
    
    private void applyResultChanges(){
        for (final Film item : result) {
            TextView product = new TextView(ExtendedSearchActivity.this);
            product.setText(FilmStringPretty.prefixPrint(item));
            product.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ExtendedSearchActivity.this, FilmDetailsActivity.class);  
                    intent.putExtra("id", item.getId());
                    startActivity(intent);
                }
            });
            lm.addView(product);
        }
        ((ProgressBar)findViewById(R.id.progressBar1)).setVisibility(View.GONE);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extended_search);
        lm = (LinearLayout) findViewById(R.id.o2);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        String searchWord = getIntent().getStringExtra("word");
        searchLoadingTask.execute(searchWord);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.extended_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
