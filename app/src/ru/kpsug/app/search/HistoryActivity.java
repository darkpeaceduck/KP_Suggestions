package ru.kpsug.app.search;

import java.util.Set;

import ru.kpsug.app.R;
import ru.kpsug.app.R.id;
import ru.kpsug.app.R.layout;
import ru.kpsug.app.R.menu;
import ru.kpsug.app.film.FilmDetailsActivity;
import ru.kpsug.app.film.FilmStringPretty;
import ru.kpsug.app.service.HistoryKeeperService;
import ru.kpsug.app.service.HistoryKeeperService.Node;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HistoryActivity extends AppCompatActivity{
    private HistoryKeeperService.HistoryKeeperBinder mbinderHistory = null;
    private LinearLayout lm;
    private ServiceConnection connHistory = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mbinderHistory = (HistoryKeeperService.HistoryKeeperBinder) service;
            viewHistory(mbinderHistory.getService().getHistoryStr());
        }
    };
    
    private void viewHistory(Set<String> value){
        TextView tv= (TextView)findViewById(R.id.textView1); 
        for(final String str:value){
            View v = LayoutInflater.from(this).inflate(R.layout.list_item, null);
            TextView product = (TextView) v.findViewById(R.id.tadaText);
            product.setText((new HistoryKeeperService.Node(str)).toString());
            product.setText(str);
            product.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HistoryActivity.this, FilmDetailsActivity.class);  
                    intent.putExtra("id", new HistoryKeeperService.Node(str).getId());
                    startActivity(intent);
                }
            });
            lm.addView(v);
        }
       
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        lm = (LinearLayout)findViewById(R.id.LinearLayout1);
        ((Button)findViewById(R.id.toggleButton1)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mbinderHistory != null){
                    mbinderHistory.getService().cleanHistory();
                }
                lm.removeViews(1, lm.getChildCount() - 1);
            }
        });
        bindService(new Intent(this, HistoryKeeperService.class), connHistory,
                Context.BIND_AUTO_CREATE);
    }
    
    @Override
    protected void onDestroy() {
        unbindService(connHistory);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
