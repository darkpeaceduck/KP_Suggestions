package ru.kpsug.app.search;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import ru.kpsug.app.R;
import ru.kpsug.app.R.id;
import ru.kpsug.app.film.FilmDetailsActivity;
import ru.kpsug.app.service.ConnectionService;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

public class SearchActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final AutoCompleteTextView text = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
        final AutoCompleteAdapter adapter = new AutoCompleteAdapter(this,
                R.layout.down2, R.id.textView1);
        text.setAdapter(adapter);
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
                Intent intent = new Intent(SearchActivity.this, ExtendedSearchActivity.class);  
                intent.putExtra("word", text.getText().toString());
                text.setText("");
                startActivity(intent);
            }
        });
        startService(new Intent(this, ConnectionService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    protected void onDestroy() {
        stopService(new Intent(this, ConnectionService.class));
        super.onDestroy();
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
