package ru.kpsug.app.activity;

import android.content.Intent;
import android.os.Bundle;
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
import ru.kpsug.app.service.DbConnectionManager;
import ru.kpsug.app.service.DbConnectionManager.DbConnectionManagerResponse;
import ru.kpsug.app.service.DbConnectionManager.DbConnectionTaskCallback;
import ru.kpsug.server.SuggestionsCalculator.SuggestionsResult;

public class SuggestionsActivity extends AppCompatActivity {

	private static final int DEFAULT_DEPTH_LEVEL = 2;

	private SuggestionsActivityFragmentAdapter sectionsPagerAdapter;
	private ViewPager viewPager;
	private String filmId;
	private DepthDialog depthDlg;
	private LimitDialog limitDlg;
	private SortedDialog sortedDlg;

	private DbConnectionTaskCallback dbConnectionTaskCallback = new DbConnectionTaskCallback() {

		@Override
		public void onDbConnectionTaskCallback(DbConnectionManagerResponse result) {
			if (!result.isError()) {
				refreshPages(result.getResult());
			}
		}
	};

	private void refreshPages(SuggestionsResult result) {
		sectionsPagerAdapter.setResult(result);
		((ProgressBar) findViewById(R.id.progressBarFilmDetails)).setVisibility(View.GONE);
	}

	public void onLoad(int page) {
		viewPager.setAdapter(sectionsPagerAdapter);
		((ProgressBar) findViewById(R.id.progressBarFilmDetails)).setVisibility(View.VISIBLE);
		DbConnectionManager.requestToDb(getResources(), filmId, page, dbConnectionTaskCallback);
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
		depthDlg = new DepthDialog(this);
		limitDlg = new LimitDialog(this);
		sortedDlg = new SortedDialog(this);
		sectionsPagerAdapter = new SuggestionsActivityFragmentAdapter(getSupportFragmentManager());
		onLoad(DEFAULT_DEPTH_LEVEL);
	}

	@Override
	protected void onDestroy() {
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
		if (id == R.id.action_history) {
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
