package ru.kpsug.app.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import ru.kpsug.app.etc.FilmStringPretty;
import ru.kpsug.db.Film;
import ru.kpsug.kp.Search;
import ru.kpsug.kp.Search.SearchException;
import ru.kpsug.kp.Search.SearchResult;

public class AutoCompleteAdapter extends BaseAdapter implements Filterable {

	private static final int FIELD_ID_DEFAULT = 0;

	private List<Film> results = new ArrayList<Film>();
	private LayoutInflater inflater;

	private int fieldId = FIELD_ID_DEFAULT;
	private int resource;

	public AutoCompleteAdapter(Context context, int ResId, int mFieldId) {
		this.inflater = LayoutInflater.from(context);
		this.resource = ResId;
		this.fieldId = mFieldId;
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
			protected void publishResults(CharSequence constraint, FilterResults results) {
				if (results != null && results.count > 0) {
					AutoCompleteAdapter.this.results = (List<Film>) results.values;
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}
		};

		return filter;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(inflater, position, convertView, parent, resource);
	}

	private View createViewFromResource(LayoutInflater inflater, int position, View convertView, ViewGroup parent,
			int resource) {
		View view;
		TextView text;

		if (convertView == null) {
			view = inflater.inflate(resource, parent, false);
		} else {
			view = convertView;
		}

		try {
			if (fieldId == 0) {
				text = (TextView) view;
			} else {
				text = (TextView) view.findViewById(fieldId);
			}
		} catch (ClassCastException e) {
			Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
			throw new IllegalStateException("ArrayAdapter requires the resource ID to be a TextView", e);
		}

		Film item = getItem(position);
		text.setText(FilmStringPretty.prefixPrint(item));
		return view;
	}

}