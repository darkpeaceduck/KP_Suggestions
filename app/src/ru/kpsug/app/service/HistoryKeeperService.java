package ru.kpsug.app.service;

import java.util.HashSet;
import java.util.Set;

import ru.kpsug.app.R;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.os.IBinder;

public class HistoryKeeperService extends Service {
    public static class HistorySetNode {
        public static enum Type {
            SUGGESTIONS, EXTENDED_SEARCH, FILM,
        }
        
        private static final Type TYPE_DEFAULT = Type.FILM;
        private static final String INFO_DEFAUT = "no_info";
        private static final String ID_DEFAUT = "0";
        private final static String sep = "~~~~~~";
        
        private Type type = TYPE_DEFAULT;
        private String info = INFO_DEFAUT;
        private String id = ID_DEFAUT;
    

        public HistorySetNode(Type type, String id, String info) {
            super();
            this.id = id;
            this.type = type;
            this.info = info;
        }

        public String getId() {
            return id;
        }

        public HistorySetNode(String s) {
            String[] parts = s.split(sep);
            if (parts.length == 3) {
                type = getTypeString(parts[0]);
                id = parts[1];
                info = parts[2];
            }
        }

        private String getStringType(Type s) {
            switch (s) {
            case SUGGESTIONS:
                return "0";
            case EXTENDED_SEARCH:
                return "1";
            case FILM:
                return "2";
            }
            return null;
        }

        public Type getTypeString(String s) {
            switch (s) {
            case "0":
                return Type.SUGGESTIONS;
            case "1":
                return Type.EXTENDED_SEARCH;
            case "2":
                return Type.FILM;
            }
            return null;
        }

        @Override
        public String toString() {
            return getStringType(type) + sep + id + sep + info;
        }
        
        public String prettyPrint(){
            String result = "";
            switch (type) {
            case SUGGESTIONS:
                result += "Предположения на фильм : ";
                break;
            case EXTENDED_SEARCH:
                result += "Расширенный поиск слова : ";
                break;
            case FILM:
                result += "Детали фильма : ";
            }
            return result + info;
        }
        
        public Type getType(){
            return type;
        }
        
        public String getInfo(){
            return info;
        }
    }

    private String filename = null;
    private String rowname = null;
    private Context context = null;
    private SharedPreferences pref = null;
    private Set<String> currentStringSet = new HashSet<String>();
    private Set<HistorySetNode> currentNodeSet = new HashSet<HistorySetNode>();

    private void transformSets() {
        currentNodeSet = new HashSet<HistorySetNode>();
        for (String str : currentStringSet) {
            currentNodeSet.add(new HistorySetNode(str));
        }
    }

    public void saveContext(Context context) {
        writeHistory();
        this.context = context;
        filename = context.getString(R.string.history_file_tag);
        rowname = context.getString(R.string.history_tag);
        pref = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        currentStringSet = new HashSet<String>(pref.getStringSet(rowname,
                new HashSet<String>()));
        transformSets();
    }

    public Set<HistorySetNode> getHistory() {
        return currentNodeSet;
    }

    public Set<String> getHistoryStr() {
        return currentStringSet;
    }

    public void writeToHistory(HistorySetNode node) {
        currentNodeSet.add(node);
        currentStringSet.add(node.toString());
        writeHistory();
    }

    public void writeHistory() {
        if (context != null) {
            Editor ed = pref.edit();
            ed.putStringSet(rowname, currentStringSet);
            ed.commit();
        }
    }

    public void cleanHistory() {
        currentStringSet.clear();
        currentNodeSet.clear();
        writeHistory();
    }

    public class HistoryKeeperBinder extends Binder {
        public HistoryKeeperService getService() {
            return HistoryKeeperService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new HistoryKeeperBinder();
    }

    @Override
    public void onDestroy() {
        writeHistory();
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        saveContext(this);
    }
}
