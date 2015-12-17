package ru.kpsug.app.film;

import java.util.List;

import ru.kpsug.app.R;
import ru.kpsug.db.Film;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SuggestionsActivityFragment extends Fragment {
    private List<Film> pagedFilms;
    private LinearLayout lm;
    private View rootView;

    public SuggestionsActivityFragment(List<Film> pagedFilms) {
        this.pagedFilms = pagedFilms;
    }

    private void refresh() {
        lm.removeAllViews();
        for (final Film item : pagedFilms) {
            View v = LayoutInflater.from(rootView.getContext()).inflate(
                    R.layout.list_item, null);
            TextView product = (TextView) v.findViewById(R.id.tadaText);
            product.setText(FilmStringPretty.prefixPrint(item));
            product.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(rootView.getContext(),
                            FilmDetailsActivity.class);
                    intent.putExtra("id", item.getId());
                    startActivity(intent);
                }
            });
            lm.addView(v);
        }
    }

    private void viewPage(final View rootView) {
        lm = (LinearLayout) rootView.findViewById(R.id.LinearLayout2);
        this.rootView = rootView;
        refresh();
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
