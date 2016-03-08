package ru.kpsug.app.activity;

import ru.kpsug.app.R;
import ru.kpsug.app.adapter.AutoCompleteAdapter;
import ru.kpsug.app.etc.DelayAutoCompleteTextView;
import ru.kpsug.app.etc.IntentFactory;
import ru.kpsug.app.service.HistoryKeeperService;
import ru.kpsug.app.service.HistoryKeeperService.HistorySetNode;
import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

public class SearchActivity extends AppCompatActivity {
    private HistoryKeeperService.HistoryKeeperBinder binderHistory = null;
    
    private ServiceConnection connHistory = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binderHistory = (HistoryKeeperService.HistoryKeeperBinder) service;
        }

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
    };

    private void createAutoComplete() {
        final DelayAutoCompleteTextView text = (DelayAutoCompleteTextView) findViewById(R.id.autoCompleteTextViewMainActivity);
        final AutoCompleteAdapter adapter = new AutoCompleteAdapter(this,
                R.layout.down2, R.id.textViewName);
        text.setAdapter(adapter);
        text.setLoadingIndicator((ProgressBar) findViewById(R.id.progressBarMainActivity));
        text.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                text.setText("");
                startActivity(IntentFactory.createFilmDetailsActivity(
                        SearchActivity.this, adapter.getItem(position).getId()));
            }
        });
        ((Button) findViewById(R.id.buttonSearch))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String word = text.getText().toString();
                        if (binderHistory != null) {
                            binderHistory.getService().writeToHistory(
                                    new HistorySetNode(HistorySetNode.Type.EXTENDED_SEARCH, "0",
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