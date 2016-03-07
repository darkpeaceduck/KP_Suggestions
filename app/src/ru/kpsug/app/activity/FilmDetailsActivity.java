package ru.kpsug.app.activity;

import java.util.List;
import java.util.Map.Entry;

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
import ru.kpsug.app.etc.FilmStringPretty;
import ru.kpsug.app.etc.IntentFactory;
import ru.kpsug.app.service.DbConnectionService;
import ru.kpsug.app.service.HistoryKeeperService;
import ru.kpsug.app.service.HistoryKeeperService.HistorySetNode;
import ru.kpsug.db.Film;
import ru.kpsug.kp.KpPath;
import ru.kpsug.server.Suggestions.SuggestionsResult;

public class FilmDetailsActivity extends AppCompatActivity implements DbConnectionService.DbConnectionTaskCallback{
    private String id = null;
    private DbConnectionService.DbConnectionBinder connectionBinder = null;
    private HistoryKeeperService.HistoryKeeperBinder connectionBinderHistory = null;
    private TextView filmNameView = null;
    private TextView ratingView = null;
    private TextView purposesView = null;
    private TextView annotationView = null;
    private TextView actorsView = null;
    private Film savedFilm = null;
    
    @Override
	public void onDbConnectionTaskCallback(SuggestionsResult result) {
		refreshFilmData(result.getFilms().entrySet().iterator().next().getValue());
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

    private void refreshFilmData(Film film) {
        savedFilm = film;
        filmNameView.setText(film.getName());
        ratingView.setText(film.getRating());
        StringBuilder purposesViewText = new StringBuilder();
        for (Entry<String, List<String>> entry : film.getPurposes()
                .entrySet()) {
        	purposesViewText.append(entry.getKey());
        	purposesViewText.append(" - ");
        	purposesViewText.append(entry.getValue());
        	purposesViewText.append("\n");
        }
        purposesView.setText(purposesViewText.toString());
        annotationView.setText(film.getAnnotation());
        actorsView.setText(film.getActors().toString());
        ((ProgressBar) findViewById(R.id.progressBarFilmDetails))
                .setVisibility(View.GONE);
        if (connectionBinderHistory != null) {
            connectionBinderHistory.getService().writeToHistory(
                    new HistorySetNode(HistorySetNode.Type.FILM, id, FilmStringPretty
                            .prefixPrint(film)));
        }
    }

    private void initSugButtion() {
        ((Button) findViewById(R.id.buttonSearch))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (connectionBinderHistory != null) {
                            connectionBinderHistory.getService().writeToHistory(
                                    new HistorySetNode(HistorySetNode.Type.SUGGESTIONS, id,
                                            FilmStringPretty
                                                    .prefixPrint(savedFilm)));
                        }
                        startActivity(IntentFactory.createSuggestionsActivity(
                                FilmDetailsActivity.this, id));
                    }
                });
        ((Button) findViewById(R.id.buttonOpenInBrowser))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(IntentFactory.createBrowserIntent(KpPath
                                .makeFilmLink(id)));
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
        purposesView = (TextView) findViewById(R.id.textViewPurposes);
        annotationView = (TextView) findViewById(R.id.textViewAnnotation);
        actorsView = (TextView) findViewById(R.id.textViewActors);
        initSugButtion();
        bindService(new Intent(this, DbConnectionService.class), conn,
                Context.BIND_AUTO_CREATE);
        bindService(new Intent(this, HistoryKeeperService.class), connHistory,
                Context.BIND_AUTO_CREATE);
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
        if(id == R.id.action_history){
            startActivity(IntentFactory.createHistoryActivity(this));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}