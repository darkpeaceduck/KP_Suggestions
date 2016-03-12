package ru.kpsug.app.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import ru.kpsug.app.R;
import ru.kpsug.app.etc.ExpandableTextView;
import ru.kpsug.app.etc.FilmStringPretty;
import ru.kpsug.app.etc.IntentFactory;
import ru.kpsug.app.service.DbConnectionService;
import ru.kpsug.app.service.DbConnectionService.DbConnectionServiceResponse;
import ru.kpsug.app.service.HistoryKeeperService;
import ru.kpsug.app.service.HistoryKeeperService.HistorySetNode;
import ru.kpsug.db.Film;
import ru.kpsug.kp.KpPath;

public class FilmDetailsActivity extends AppCompatActivity implements DbConnectionService.DbConnectionTaskCallback {
	private String id = null;
	private DbConnectionService.DbConnectionBinder connectionBinder = null;
	private HistoryKeeperService.HistoryKeeperBinder connectionBinderHistory = null;
	private TextView filmNameView = null;
	private TextView ratingView = null;
	private ExpandableTextView purposesView = null;
	private ExpandableTextView annotationView = null;
	private ExpandableTextView actorsView = null;
	private Film savedFilm = null;
	private Button buttonSearch;
	private Button buttonOpenInBrowser;
	private Button buttonRefresh;
	private ProgressBar progressBarLoading;

	@Override
	public void onDbConnectionTaskCallback(DbConnectionServiceResponse result) {
		if (result.isError()) {
			refreshDataOnServerError();
		} else if (result.getResult().getFilms().isEmpty()) {
			refreshDataOnFilmNotFoundError();
		} else {
			refreshFilmData(result.getResult().getFilms().entrySet().iterator().next().getValue());
		}
	}

	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			connectionBinder = (DbConnectionService.DbConnectionBinder) service;
			connectionBinder.getService().requestToDb(id, FilmDetailsActivity.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}
	};

	private ServiceConnection connHistory = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			connectionBinderHistory = (HistoryKeeperService.HistoryKeeperBinder) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}
	};

	private void refreshDataOnServerError() {
		filmNameView.setText(getResources().getString(R.string.err_connecting_to_server));
		filmNameView.setVisibility(View.VISIBLE);
		progressBarLoading.setVisibility(View.GONE);
		buttonRefresh.setVisibility(View.VISIBLE);
	}

	private void refreshDataOnFilmNotFoundError() {
		filmNameView.setText(getResources().getString(R.string.err_not_found_film));
		filmNameView.setVisibility(View.VISIBLE);
		progressBarLoading.setVisibility(View.GONE);
		buttonRefresh.setVisibility(View.VISIBLE);
	}

	private void refreshFilmData(Film film) {
		savedFilm = film;
		filmNameView.setText(film.getName());
		ratingView.setText(film.getRating());

		purposesView.setText(FilmStringPretty.purposesPrint(film));
		annotationView.setText(film.getAnnotation());
		actorsView.setText(FilmStringPretty.actorsPrint(film));

		filmNameView.setVisibility(View.VISIBLE);
		ratingView.setVisibility(View.VISIBLE);
		purposesView.setVisibility(View.VISIBLE);
		annotationView.setVisibility(View.VISIBLE);
		actorsView.setVisibility(View.VISIBLE);

		progressBarLoading.setVisibility(View.GONE);
		buttonSearch.setVisibility(View.VISIBLE);
		buttonOpenInBrowser.setVisibility(View.VISIBLE);
		if (connectionBinderHistory != null) {
			connectionBinderHistory.getService().writeToHistory(
					new HistorySetNode(HistorySetNode.Type.FILM, id, FilmStringPretty.prefixPrint(film)));
		}
	}

	private void initButtons() {
		buttonSearch = (Button) findViewById(R.id.buttonSearch);
		buttonOpenInBrowser = (Button) findViewById(R.id.buttonOpenInBrowser);
		buttonRefresh = (Button) findViewById(R.id.buttonRefresh);

		buttonSearch.setVisibility(View.GONE);
		buttonOpenInBrowser.setVisibility(View.GONE);
		buttonRefresh.setVisibility(View.GONE);

		buttonSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (connectionBinderHistory != null) {
					connectionBinderHistory.getService().writeToHistory(new HistorySetNode(
							HistorySetNode.Type.SUGGESTIONS, id, FilmStringPretty.prefixPrint(savedFilm)));
				}
				startActivity(IntentFactory.createSuggestionsActivity(FilmDetailsActivity.this, id));
			}
		});

		buttonOpenInBrowser.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(IntentFactory.createBrowserIntent(KpPath.makeFilmLink(id)));
			}
		});

		buttonRefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FilmDetailsActivity.this.recreate();
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_film_details);
		Intent incoming_intent = getIntent();
		id = incoming_intent.getStringExtra("id");

		filmNameView = (TextView) findViewById(R.id.textViewName);
		ratingView = (TextView) findViewById(R.id.textViewRating);
		purposesView = (ExpandableTextView) findViewById(R.id.textViewPurposes);
		purposesView.setDefaultTrimmedText(getResources().getString(R.string.purposes_view_default_expand_text));
		annotationView = (ExpandableTextView) findViewById(R.id.textViewAnnotation);
		annotationView.setDefaultTrimmedText(getResources().getString(R.string.annotation_view_default_expand_text));
		actorsView = (ExpandableTextView) findViewById(R.id.textViewActors);
		actorsView.setDefaultTrimmedText(getResources().getString(R.string.actors_view_default_expand_text));

		filmNameView.setVisibility(View.GONE);
		ratingView.setVisibility(View.GONE);
		purposesView.setVisibility(View.GONE);
		annotationView.setVisibility(View.GONE);
		actorsView.setVisibility(View.GONE);

		progressBarLoading = (ProgressBar) findViewById(R.id.progressBarFilmDetails);
		initButtons();
		bindService(new Intent(this, DbConnectionService.class), conn, Context.BIND_AUTO_CREATE);
		bindService(new Intent(this, HistoryKeeperService.class), connHistory, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		unbindService(conn);
		unbindService(connHistory);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.film_details, menu);
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