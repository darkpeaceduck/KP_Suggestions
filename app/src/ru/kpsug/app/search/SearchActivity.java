package ru.kpsug.app.search;

import ru.kpsug.app.R;
import ru.kpsug.app.service.HistoryKeeperService;
import ru.kpsug.app.service.IntentFactory;
import ru.kpsug.app.service.HistoryKeeperService.Node;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

public class SearchActivity extends Activity {
    private HistoryKeeperService.HistoryKeeperBinder mbinderHistory = null;
    private ServiceConnection connHistory = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mbinderHistory = (HistoryKeeperService.HistoryKeeperBinder) service;
        }
    };

    private void createAutoComplete() {
        final DelayAutoCompleteTextView text = (DelayAutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
        final AutoCompleteAdapter adapter = new AutoCompleteAdapter(this,
                R.layout.down2, R.id.textView1);
        text.setAdapter(adapter);
        text.setLoadingIndicator((ProgressBar) findViewById(R.id.progressBar));
        text.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                text.setText("");
                startActivity(IntentFactory.createFilmDetailsActivity(
                        SearchActivity.this, adapter.getItem(position).getId()));
            }
        });
        ((Button) findViewById(R.id.button1))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String word = text.getText().toString();
                        if (mbinderHistory != null) {
                            mbinderHistory.getService().writeToHistory(
                                    new Node(Node.Type.EXTENDED_SEARCH, "0",
                                            word));
                        }
                        text.setText("");
                        startActivity(IntentFactory
                                .createExtendedSearchActivity(
                                        SearchActivity.this, word));
                    }
                });
    }

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
        int id = item.getItemId();
        if (id == R.id.action_history) {
            startActivity(new Intent(this, HistoryActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}