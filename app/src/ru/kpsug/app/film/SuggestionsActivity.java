package ru.kpsug.app.film;

import ru.kpsug.app.R;
import ru.kpsug.app.film.SuggestionsActivityFragmentAdapter.SortedMode;
import ru.kpsug.app.service.ConnectionService;
import ru.kpsug.app.service.IntentFactory;
import ru.kpsug.server.Suggestions.SuggestionsResult;
import android.support.v7.app.AppCompatActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

public class SuggestionsActivity extends AppCompatActivity {

    private class detailsSendSaver extends
            AsyncTask<SuggestionsResult, Object, Object> {
        @Override
        protected Object doInBackground(SuggestionsResult... params) {
            return params[0];
        }

        protected void onPostExecute(Object result) {
            refreshPages((SuggestionsResult) result);
        };
    };

    private SuggestionsActivityFragmentAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ConnectionService.ConnectionBinder mbinder;
    private String id;
    private DepthDialog depthDlg;
    private LimitDialog limitDlg;
    private SortedDialog sortedDlg;
    private static final int DEFAULT_LEVEL = 2;

    private final ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mbinder = (ConnectionService.ConnectionBinder) service;
            onLoad(DEFAULT_LEVEL);
        }
    };

    private void refreshPages(SuggestionsResult result) {
        mSectionsPagerAdapter.setResult(result);
        ((ProgressBar) findViewById(R.id.progressBar1))
                .setVisibility(View.GONE);
    }

    public void onLoad(int page) {
        mViewPager.setAdapter(mSectionsPagerAdapter);
        ((ProgressBar) findViewById(R.id.progressBar1))
                .setVisibility(View.VISIBLE);
        mbinder.getService().requestToDb(id, page, new detailsSendSaver());
    }

    public void onLimitChange(int limit) {
        mSectionsPagerAdapter.setLimit(limit);
    }

    public void onSortedModeChange(SortedMode mode) {
        mSectionsPagerAdapter.setSortedMode(mode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);
        Intent incomingIntent = getIntent();
        id = incomingIntent.getStringExtra("id");
        mViewPager = (ViewPager) findViewById(R.id.pager);
        bindService(new Intent(this, ConnectionService.class), conn,
                Context.BIND_AUTO_CREATE);
        depthDlg = new DepthDialog(this);
        limitDlg = new LimitDialog(this);
        sortedDlg = new SortedDialog(this);
        mSectionsPagerAdapter = new SuggestionsActivityFragmentAdapter(
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
            depthDlg.show(getFragmentManager(), "depth_dialog");
            return true;
        }
        if (id == R.id.action_limit_dialog) {
            limitDlg.show(getFragmentManager(), "limit_dialog");
            return true;
        }
        if (id == R.id.action_sorted_dialog) {
            sortedDlg.show(getFragmentManager(), "sorted_dialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
