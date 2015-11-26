package ru.kpsug.app.service;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import ru.kpsug.app.R;
import ru.kpsug.kp.Search.SearchResult;
import ru.kpsug.server.AsyncClient;
import ru.kpsug.server.AsyncClient.innerFunc;
import ru.kpsug.server.Request;
import ru.kpsug.server.Suggestions.SuggestionsResult;

public class ConnectionService extends Service {

    private AsyncClient dbClient = null;

    public class ConnectionBinder extends Binder {
        public ConnectionService getService() {
            return ConnectionService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ConnectionBinder();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }
    

    @Override
    public void onCreate() {
        super.onCreate();
        dbClient = new AsyncClient(getResources().openRawResource(R.raw.db_conf));
        try {
            dbClient.connect(null, null).join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        try {
            dbClient.closeSocket(null, null).join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
    
    public void requestToDb(String id, Integer level, final AsyncClient.innerFunc<SuggestionsResult, Object> saver){
        dbClient.send(null, new innerFunc<Object, Object>() {
            @Override
            public Object run(Object param) throws Exception {
                dbClient.nextResponse(null, saver);
                return null;
            }
        }, new Request(0, level, id));
    }
    
    public void requestToDb(String id, final AsyncClient.innerFunc<SuggestionsResult, Object> saver){
        requestToDb(id, 0, saver);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    
    

    
}