package ru.kpsug.app.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import ru.kpsug.app.activity.fragment.SuggestionsActivityFragment;
import ru.kpsug.app.etc.SuggestionsActivitySortedMode;
import ru.kpsug.db.Film;
import ru.kpsug.server.Suggestions.SuggestionsResult;

public class SuggestionsActivityFragmentAdapter extends
        FragmentStatePagerAdapter {
    
    private final static int PAGELIMIT_DEFAULT = 10;
    private SuggestionsResult result = null;
    private int pageLimit = PAGELIMIT_DEFAULT;
    private SuggestionsActivitySortedMode mode = SuggestionsActivitySortedMode.RATING;

    public SuggestionsActivityFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public SuggestionsActivityFragmentAdapter(FragmentManager fm, int pageLimit) {
        super(fm);
        this.pageLimit = pageLimit;
    }

    public void setResult(SuggestionsResult result) {
        this.result = result;
        notifyDataSetChanged();
    }

    public void setLimit(int limit) {
        pageLimit = limit;
        notifyDataSetChanged();
    }

    public void setSortedMode(SuggestionsActivitySortedMode mode) {
        this.mode = mode;
        notifyDataSetChanged();
    }

    private List<Film> getPagedFilms(int position) {
        List<Film> pagedFilms = new ArrayList<Film>();
        if (result != null) {
            switch (mode) {
            case RATING:
                pagedFilms = result.getFilmsSortedByRating(position, pageLimit);
                break;
            case YEAR_MORE:
                pagedFilms = result
                        .getFilmSortedByYearMore(position, pageLimit);
                break;
            case YEAR_LESS:
                pagedFilms = result
                        .getFilmSortedByYearLess(position, pageLimit);
                break;
            }
        }
        return pagedFilms;
    }

    @Override
    public Fragment getItem(int position) {
        return new SuggestionsActivityFragment(getPagedFilms(position));
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        if (result == null) {
            return 0;
        }
        return result.getLevelsEdges().size();
    }
}
