package ru.kpsug.app.search;

import java.util.ArrayList;

import ru.kpsug.app.R;
import ru.kpsug.app.film.FilmStringPretty;
import ru.kpsug.db.Film;
import ru.kpsug.kp.Search;
import ru.kpsug.kp.Search.SearchException;
import ru.kpsug.kp.Search.SearchResult;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class AutoCompleteAdapter extends BaseAdapter implements Filterable {

    private final Context context;
    private ArrayList<Film> results = new ArrayList<Film>();
    private LayoutInflater mInflater;
    private int mFieldId = 0;
    private int mResource;

    public AutoCompleteAdapter(Context context, int ResId, int mFieldId) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mResource = ResId;
        this.mFieldId = mFieldId;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Film getItem(int position) {
        return results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    SearchResult result;
                    try {
                        result = Search.prefixSearch(constraint.toString());
                        filterResults.values = result.getFilms();
                        filterResults.count = result.getNumber();
                    } catch (SearchException e) {
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint,
                    FilterResults results) {
                if (results != null && results.count > 0) {
                    AutoCompleteAdapter.this.results = (ArrayList<Film>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }

    /* imported for ArrayAdapter */

    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(mInflater, position, convertView, parent,
                mResource);
    }

    private View createViewFromResource(LayoutInflater inflater, int position,
            View convertView, ViewGroup parent, int resource) {
        View view;
        TextView text;

        if (convertView == null) {
            view = inflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }

        try {
            if (mFieldId == 0) {
                // If no custom field is assigned, assume the whole resource is
                // a TextView
                text = (TextView) view;
            } else {
                // Otherwise, find the TextView field within the layout
                text = (TextView) view.findViewById(mFieldId);
            }
        } catch (ClassCastException e) {
            Log.e("ArrayAdapter",
                    "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "ArrayAdapter requires the resource ID to be a TextView", e);
        }

        Film item = getItem(position);
        text.setText(FilmStringPretty.prefixPrint(item));
        return view;
    }

}