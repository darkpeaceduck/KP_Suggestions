package ru.kpsug.app.service;

import ru.kpsug.app.film.FilmDetailsActivity;
import ru.kpsug.app.film.SuggestionsActivity;
import ru.kpsug.app.search.ExtendedSearchActivity;
import ru.kpsug.app.search.HistoryActivity;
import ru.kpsug.app.search.SearchActivity;
import ru.kpsug.kp.KpPath;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class IntentFactory {
    public static Intent createSearchActivity(Context context){
        Intent intent = new Intent(context, SearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }
    
    public static Intent createHistoryActivity(Context context){
        return new Intent(context, HistoryActivity.class);
    }
    
    public static Intent createExtendedSearchActivity(Context context, String word){
        Intent intent = new Intent(context, ExtendedSearchActivity.class);
        intent.putExtra("word", word);
        return intent;
    }
    
    public static Intent createSuggestionsActivity(Context context, String id){
        Intent intent = new Intent(context, SuggestionsActivity.class);
        intent.putExtra("id", id);
        return intent;
    }
    
    public static Intent createFilmDetailsActivity(Context context, String id){
        Intent intent = new Intent(context, FilmDetailsActivity.class);
        intent.putExtra("id", id);
        return intent;
    }
    
    public static Intent createBrowserIntent(String url){
        return new Intent(Intent.ACTION_VIEW,
                Uri.parse(url));
    }
}
