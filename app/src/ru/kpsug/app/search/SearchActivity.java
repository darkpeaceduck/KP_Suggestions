package ru.kpsug.app.search;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import ru.kpsug.app.R;
import ru.kpsug.app.R.id;
import ru.kpsug.app.film.FilmDetailsActivity;
import ru.kpsug.app.film.FilmStringPretty;
import ru.kpsug.app.service.ConnectionService;
import ru.kpsug.app.service.HistoryKeeperService;
import ru.kpsug.app.service.HistoryKeeperService.Node;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SearchActivity extends Activity {
    private HistoryKeeperService.HistoryKeeperBinder mbinderHistory = null;
    
    private void createAutoComplete(){
        final DelayAutoCompleteTextView text = (DelayAutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
        final AutoCompleteAdapter adapter = new AutoCompleteAdapter(this,R.layout.down2, R.id.textView1);
        text.setAdapter(adapter);
        text.setLoadingIndicator((ProgressBar) findViewById(R.id.progressBar));
        text.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                text.setText("");
                Intent intent = new Intent(SearchActivity.this, FilmDetailsActivity.class);  
                intent.putExtra("id", adapter.getItem(position).getId());
                startActivity(intent);
            }
        });
        ((Button)findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = text.getText().toString();
                if(mbinderHistory != null){
                    mbinderHistory.getService().writeToHistory(new Node(Node.Type.EXTENDED_SEARCH, "0", word));
                }
                Intent intent = new Intent(SearchActivity.this, ExtendedSearchActivity.class);  
                intent.putExtra("word", word);
                text.setText("");
                startActivity(intent);
            }
        });
    }
    
    
    
    private ServiceConnection connHistory = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mbinderHistory = (HistoryKeeperService.HistoryKeeperBinder) service;
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createAutoComplete();
        bindService(new Intent(this, HistoryKeeperService.class), connHistory,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    protected void onDestroy() {
        unbindService(connHistory);
        super.onDestroy();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_history){
            startActivity(new Intent(this, HistoryActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}