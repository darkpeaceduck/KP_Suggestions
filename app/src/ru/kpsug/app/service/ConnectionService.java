package ru.kpsug.app.service;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import ru.kpsug.server.AsyncClient;
import ru.kpsug.server.AsyncClient.innerFunc;
import ru.kpsug.server.Request;
import ru.kpsug.server.Suggestions.SuggestionsResult;

public class ConnectionService extends Service {

    private AsyncClient client = null;

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
        client = new AsyncClient(null);
        try {
            client.connect(null, null).join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        try {
            client.closeSocket(null, null).join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
    
    private class sendSaver implements AsyncClient.innerFunc<SuggestionsResult, Object>{
        private SuggestionsResult result = null;
        @Override
        public Object run(SuggestionsResult result) throws Exception {
            this.result = result;
            return null;
        }
        
        public SuggestionsResult getResult(){
            return result;
        }
        
    }
    
    public SuggestionsResult send(String id){ 
        final sendSaver saver = new sendSaver();
        try {
            client.send(null, new innerFunc<Object, Object>() {
                @Override
                public Object run(Object param) throws Exception {
                    client.nextResponse(null, saver).join();
                    return null;
                }
            }, new Request(0, 0, id)).join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return saver.getResult();
    }
    

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    
}
