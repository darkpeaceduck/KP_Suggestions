package ru.kpsug.app.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import ru.kpsug.app.R;
import ru.kpsug.app.adapter.SuggestionsActivityFragmentAdapter;
import ru.kpsug.app.dialog.DepthDialog;
import ru.kpsug.app.dialog.LimitDialog;
import ru.kpsug.app.dialog.SortedDialog;
import ru.kpsug.app.etc.IntentFactory;
import ru.kpsug.app.etc.SuggestionsActivitySortedMode;
import ru.kpsug.app.service.DbConnectionService;
import ru.kpsug.server.Suggestions.SuggestionsResult;

public class SuggestionsActivity extends AppCompatActivity implements DbConnectionService.DbConnectionTaskCallback{
	
    @Override
	public void onDbConnectionTaskCallback(SuggestionsResult result) {
		refreshPages(result);
	}

    private static final int DEFAULT_DEPTH_LEVEL = 2;

    private SuggestionsActivityFragmentAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private DbConnectionService.DbConnectionBinder connectionBinder;
    private String filmId;
    private DepthDialog depthDlg;
    private LimitDialog limitDlg;
    private SortedDialog sortedDlg;
    

    private final ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            connectionBinder = (DbConnectionService.DbConnectionBinder) service;
            onLoad(DEFAULT_DEPTH_LEVEL);
        }

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
    };

    private void refreshPages(SuggestionsResult result) {
        sectionsPagerAdapter.setResult(result);
        ((ProgressBar) findViewById(R.id.progressBarFilmDetails))
                .setVisibility(View.GONE);
    }

    public void onLoad(int page) {
        viewPager.setAdapter(sectionsPagerAdapter);
        ((ProgressBar) findViewById(R.id.progressBarFilmDetails))
                .setVisibility(View.VISIBLE);
        connectionBinder.getService().requestToDb(filmId, page, this);
    }

    public void onLimitChange(int limit) {
        sectionsPagerAdapter.setLimit(limit);
    }

    public void onSortedModeChange(SuggestionsActivitySortedMode mode) {
        sectionsPagerAdapter.setSortedMode(mode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);
        Intent incomingIntent = getIntent();
        filmId = incomingIntent.getStringExtra("id");
        viewPager = (ViewPager) findViewById(R.id.pager);
        bindService(new Intent(this, DbConnectionService.class), conn,
                Context.BIND_AUTO_CREATE);
        depthDlg = new DepthDialog(this);
        limitDlg = new LimitDialog(this);
        sortedDlg = new SortedDialog(this);
        sectionsPagerAdapter = new SuggestionsActivityFragmentAdapter(
                getSupportFragmentManager());
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.suggestions, menu);
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
        if (id == R.id.action_depth_dialog) {
            depthDlg.show(getFragmentManager(), DepthDialog.TAG);
            return true;
        }
        if (id == R.id.action_limit_dialog) {
            limitDlg.show(getFragmentManager(), LimitDialog.TAG);
            return true;
        }
        if (id == R.id.action_sorted_dialog) {
            sortedDlg.show(getFragmentManager(), SortedDialog.TAG);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
}
