package ru.kpsug.app.film;

import java.util.Locale;

import ru.kpsug.app.R;
import ru.kpsug.app.R.id;
import ru.kpsug.app.R.layout;
import ru.kpsug.app.R.menu;
import ru.kpsug.app.R.string;
import ru.kpsug.app.service.ConnectionService;
import ru.kpsug.server.AsyncClient;
import ru.kpsug.server.Suggestions;
import ru.kpsug.server.Suggestions.SuggestionsResult;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SuggestionsActivity extends AppCompatActivity{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this
     * becomes too memory intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SuggestionsActivityFragmentAdapter mSectionsPagerAdapter;
    /** 
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    ConnectionService.ConnectionBinder mbinder;
    String id;

    private final int PAGES_NUMBER = 2;
    
    private AsyncTask<SuggestionsResult, Object, Object> detailsSendSaver =  new AsyncTask<SuggestionsResult, Object, Object>(){
        @Override
        protected Object doInBackground(SuggestionsResult... params) {
            return params[0];
        }
        
        protected void onPostExecute(Object result) {
            refreshPages((SuggestionsResult) result);
        };
    };
    
    private void refreshPages(SuggestionsResult result){
        mSectionsPagerAdapter.setResult(result);
        ((ProgressBar)findViewById(R.id.progressBar1)).setVisibility(View.GONE);
    }
    
    private final ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mbinder = (ConnectionService.ConnectionBinder) service;
            mbinder.getService().requestToDb(id, PAGES_NUMBER, detailsSendSaver);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        id = getIntent().getStringExtra("id");
        mSectionsPagerAdapter = new SuggestionsActivityFragmentAdapter(
                getSupportFragmentManager());
        
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
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
        getMenuInflater().inflate(R.menu.suggestions, menu);
        return true;
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
