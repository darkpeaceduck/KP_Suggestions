package ru.kpsug.app.film;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.kpsug.app.R;
import ru.kpsug.db.Film;
import ru.kpsug.server.Suggestions.SuggestionsResult;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SuggestionsActivityFragmentAdapter extends FragmentPagerAdapter{
        private SuggestionsResult result = null;
        public SuggestionsActivityFragmentAdapter(FragmentManager fm) {
            super(fm);
        }
        
        public void setResult(SuggestionsResult result) {
            this.result = result;
            notifyDataSetChanged();
        }


        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class
            // below).
            ArrayList<Film> pagedFilms = new ArrayList<Film>();
            if(result != null){
                Map<String, Film> films = result.getFilms();
                Map<Integer, List<String> > levels = result.getLevelsEdges();
                for(String id : levels.get(position)){
                    pagedFilms.add(films.get(id));
                }
            }
            return new SuggestionsActivityFragment(pagedFilms);
        }

        @Override
        public int getCount() {
            if(result == null){
                return 0;
            } 
            return result.getLevelsEdges().size();
        }
}
