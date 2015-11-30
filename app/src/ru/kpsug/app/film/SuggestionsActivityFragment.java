package ru.kpsug.app.film;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import ru.kpsug.app.R;
import ru.kpsug.app.search.ExtendedSearchActivity;
import ru.kpsug.app.search.SearchActivity;
import ru.kpsug.db.Film;
import ru.kpsug.server.Suggestions.SuggestionsResult;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SuggestionsActivityFragment extends Fragment{
    private List<Film> pagedFilms;
    
    public SuggestionsActivityFragment(List<Film> pagedFilms) {
        this.pagedFilms = pagedFilms;
        //SEE, sorting here
        Collections.sort(this.pagedFilms, new Comparator<Film>() {

            @Override
            public int compare(Film lhs, Film rhs) {
                String r1 = lhs.getRating();
                String r2 = rhs.getRating();
                if(r1 == null){
                    return (r2 == null ? 0 : 1);
                }
                if(r2 == null){
                    return -1;
                }
                return Double.compare(Double.parseDouble(r2), Double.parseDouble(r1)); 
            }
        });
    }
    
    private void viewPage(final View rootView){
        LinearLayout lm = (LinearLayout) rootView.findViewById(R.id.LinearLayout2);
        for (final Film item : pagedFilms) {
            View v = LayoutInflater.from(rootView.getContext()).inflate(R.layout.list_item, null);
            TextView product = (TextView) v.findViewById(R.id.tadaText);
            product.setText(FilmStringPretty.prefixPrint(item));
            product.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(rootView.getContext(), FilmDetailsActivity.class);  
                    intent.putExtra("id", item.getId());
                    startActivity(intent);
                }
            });
            lm.addView(v);
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_suggestions,
                container, false);
        viewPage(rootView);
        return rootView;
    }
}
