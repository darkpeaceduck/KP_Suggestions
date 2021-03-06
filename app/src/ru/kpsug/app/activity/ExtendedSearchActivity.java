package ru.kpsug.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import ru.kpsug.app.R;
import ru.kpsug.app.etc.FilmStringPretty;
import ru.kpsug.app.etc.IntentFactory;
import ru.kpsug.db.Film;
import ru.kpsug.kp.Search;
import ru.kpsug.kp.Search.SearchException;
import ru.kpsug.kp.Search.SearchResult;

public class ExtendedSearchActivity extends AppCompatActivity {
	private LinearLayout lm;
	private List<Film> result = null;

	private AsyncTask<String, Object, List<Film>> searchLoadingTask = new AsyncTask<String, Object, List<Film>>() {
		@Override
		protected List<Film> doInBackground(String... params) {
			List<Film> films = new ArrayList<>();
			try {
				SearchResult result = Search.mainSearch(params[0]);
				films = result.getFilms();
			} catch (SearchException e) {
				e.printStackTrace();
			}
			return films;
		}

		@Override
		protected void onPostExecute(List<Film> result) {
			ExtendedSearchActivity.this.result = result;
			applyResultChanges();
		};
	};

	private void applyResultChanges() {
		for (final Film item : result) {
			View v = LayoutInflater.from(ExtendedSearchActivity.this).inflate(R.layout.list_item, null);
			TextView product = (TextView) v.findViewById(R.id.ListItemText);
			product.setText(FilmStringPretty.prefixPrint(item));
			product.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(IntentFactory.createFilmDetailsActivity(ExtendedSearchActivity.this, item.getId()));
				}
			});
			lm.addView(v);
		}
		((ProgressBar) findViewById(R.id.progressBarExtendedSearch)).setVisibility(View.GONE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_extended_search);
		lm = (LinearLayout) findViewById(R.id.LinearLayoutExtendedSearch);
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
			startActivity(IntentFactory.createSearchActivity(this));
			return true;
		}
		if (id == R.id.action_history) {
			startActivity(IntentFactory.createHistoryActivity(this));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
