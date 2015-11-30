package ru.kpsug.app.film;

import java.util.ArrayList;
import java.util.Map.Entry;

import ru.kpsug.app.R;
import ru.kpsug.app.search.ExtendedSearchActivity;
import ru.kpsug.app.search.SearchActivity;
import ru.kpsug.app.service.ConnectionService;
import ru.kpsug.db.Film;
import ru.kpsug.server.AsyncClient;
import ru.kpsug.server.Suggestions.SuggestionsResult;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class FilmDetailsActivity extends Activity {
    private String id = null;
    private ConnectionService.ConnectionBinder mbinder = null;
    private TextView filmNameView = null;
    private TextView ratingView = null;
    private TextView purposesView = null;
    private TextView annotationView = null;
    private TextView actorsView = null;
    
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
    
    
    private void refreshFilmData(Film film) {
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
    }

    private void initSugButtion(){
        ((Button)findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilmDetailsActivity.this, SuggestionsActivity.class);  
                intent.putExtra("id", id);
                startActivity(intent);
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
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}