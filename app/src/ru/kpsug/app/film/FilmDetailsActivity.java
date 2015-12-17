package ru.kpsug.app.film;

import java.util.ArrayList;
import java.util.Map.Entry;

import ru.kpsug.app.R;
import ru.kpsug.app.search.ExtendedSearchActivity;
import ru.kpsug.app.search.SearchActivity;
import ru.kpsug.app.service.ConnectionService;
import ru.kpsug.app.service.HistoryKeeperService;
import ru.kpsug.app.service.HistoryKeeperService.Node;
import ru.kpsug.db.Film;
import ru.kpsug.kp.KpPath;
import ru.kpsug.server.AsyncClient;
import ru.kpsug.server.Suggestions.SuggestionsResult;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FilmDetailsActivity extends AppCompatActivity{
    private String id = null;
    private ConnectionService.ConnectionBinder mbinder = null;
    private HistoryKeeperService.HistoryKeeperBinder mbinderHistory = null;
    private TextView filmNameView = null;
    private TextView ratingView = null;
    private TextView purposesView = null;
    private TextView annotationView = null;
    private TextView actorsView = null;
    private Film savedFilm = null;
    
    private AsyncTask<Film, Object, Object> detailsSendSaver =  new AsyncTask<Film, Object, Object>(){
        @Override
        protected Object doInBackground(Film... params) {
            return params[0];
        }
        
        protected void onPostExecute(Object result) {
            refreshFilmData((Film)result);
        };
    };
    
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mbinder = (ConnectionService.ConnectionBinder) service;
            mbinder.getService().requestToDb(id, detailsSendSaver);
        }
    };
    

    private ServiceConnection connHistory = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mbinderHistory = (HistoryKeeperService.HistoryKeeperBinder) service;
        }
    };
    
    
    private void refreshFilmData(Film film) {
        savedFilm = film;
        filmNameView.setText(film.getName());
        ratingView.setText(film.getRating());
        String s = "";
        for (Entry<String, ArrayList<String>> entry : film
                .getPurposes().entrySet()) {    
            s += entry.getKey() + " - " + entry.getValue();
            s += "\n";
        }
        purposesView.setText(s);
        annotationView.setText(film.getAnnotation());
        actorsView.setText(film.getActors().toString());
        ((ProgressBar)findViewById(R.id.progressBar1)).setVisibility(View.GONE);
        if(mbinderHistory != null){
            mbinderHistory.getService().writeToHistory(new Node(Node.Type.FILM, id, FilmStringPretty.prefixPrint(film)));
        }
    }

    private void initSugButtion(){
        ((Button)findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mbinderHistory != null){
                    mbinderHistory.getService().writeToHistory(new Node(Node.Type.SUGGESTIONS, id, FilmStringPretty.prefixPrint(savedFilm)));
                }
                Intent intent = new Intent(FilmDetailsActivity.this, SuggestionsActivity.class);  
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        ((Button)findViewById(R.id.button2)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(KpPath.makeFilmLink(id)));
                startActivity(browserIntent);
            }
        });
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_details);
        Intent incoming_intent = getIntent();
        id = incoming_intent.getStringExtra("id");
        filmNameView = (TextView) findViewById(R.id.textView1);
        ratingView = (TextView) findViewById(R.id.textView5);
        purposesView = (TextView) findViewById(R.id.textView2);
        annotationView = (TextView) findViewById(R.id.textView3);
        actorsView = (TextView) findViewById(R.id.textView4);
        initSugButtion();
        bindService(new Intent(this, ConnectionService.class), conn,
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.film_details, menu);
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