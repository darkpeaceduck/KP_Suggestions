package ru.kpsug.app.film;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import ru.kpsug.app.R;
import ru.kpsug.app.service.ConnectionService;
import ru.kpsug.db.Film;
import ru.kpsug.server.AsyncClient.innerFunc;
import ru.kpsug.server.Suggestions.SuggestionsResult;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class FilmDetailsActivity extends Activity {
    private String id = null;
    private ConnectionService.ConnectionBinder mbinder = null;
    private volatile TextView filmNameView = null;
    private volatile TextView ratingView = null;
    private volatile TextView purposesView = null;
    private volatile TextView annotationView = null;
    private volatile TextView actorsView = null;

    private void refreshFilmData() {
        // mbinder.getService().send(id, new innerFunc<SuggestionsResult,
        // Object>(){
        // @Override
        // public Object run(SuggestionsResult sresult) throws Exception {
        // System.out.println(sresult);
        // if(sresult != null){
        // Film film = sresult.getFilms().get(id);
        // if(film != null){
        // synchronized (FilmDetailsActivity.this) {
        // filmNameView.setText(film.getName());
        // ratingView.setText(film.getRating());
        // String s = "";
        // for(Entry<String, ArrayList<String>> entry :
        // film.getPurposes().entrySet()){
        // s += entry.getKey() + " - " + entry.getValue();
        // // boolean before = false;
        // // for(String elem : entry.getValue()){
        // // if(before){
        // // s += ", ";
        // // }
        // // before = true;
        // // s += elem;
        // // }
        // s += "\n";
        // }
        // purposesView.setText(s);
        // annotationView.setText(film.getAnnotation());
        // actorsView.setText(film.getActors().toString());
        // }
        // }
        // }
        // return null;
        // }
        // });
        SuggestionsResult sresult = mbinder.getService().send(id);
        // @Override
        // public Object run(SuggestionsResult sresult) throws Exception {
        if (sresult != null) {
            Film film = sresult.getFilms().get(id);
            if (film != null) {
                filmNameView.setText(film.getName());
                ratingView.setText(film.getRating());
                String s = "";
                for (Entry<String, ArrayList<String>> entry : film
                        .getPurposes().entrySet()) {
                    s += entry.getKey() + " - " + entry.getValue();
                    // boolean before = false;
                    // for(String elem : entry.getValue()){
                    // if(before){
                    // s += ", ";
                    // }
                    // before = true;
                    // s += elem;
                    // }
                    s += "\n";
                }
                purposesView.setText(s);
                annotationView.setText(film.getAnnotation());
                actorsView.setText(film.getActors().toString());
            }
        }
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mbinder = (ConnectionService.ConnectionBinder) service;
            refreshFilmData();
        }
    };

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
