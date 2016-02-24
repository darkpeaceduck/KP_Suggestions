package ru.kpsug.app.service;

import java.io.IOException;
import java.util.LinkedList;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import ru.kpsug.app.R;
import ru.kpsug.db.Film;
import ru.kpsug.kp.KpParser;
import ru.kpsug.kp.PageLoader;
import ru.kpsug.server.Client;
import ru.kpsug.server.Request;
import ru.kpsug.server.Suggestions.SuggestionsResult;

public class ConnectionService extends Service {

    public class ConnectionBinder extends Binder {
        public ConnectionService getService() {
            return ConnectionService.this;
        }
    }

    private class AssyncRequest {
        private Request request;
        private AsyncTask<SuggestionsResult, Object, Object> callback;

        public Request getRequest() {
            return request;
        }

        public AsyncTask<SuggestionsResult, Object, Object> getCallback() {
            return callback;
        }

        public AssyncRequest(Request request,
                AsyncTask<SuggestionsResult, Object, Object> callback) {
            super();
            this.request = request;
            this.callback = callback;
        }

    }

    private class ConnectTaskLoop implements Runnable {
        private static final int SLEEP_RECONNECT_TIMEOUT = 1000;
        private final Client client;
        private final LinkedList<AssyncRequest> toSendQueue = new LinkedList<AssyncRequest>();
        private volatile boolean off = false;

        public ConnectTaskLoop(Client client) {
            this.client = client;
        }

        private void reconnect() throws InterruptedException {
            while (true) {
                if (off) {
                    throw new InterruptedException();
                }
                try {
                    client.reconnect();
                } catch (IOException e2) {
                    System.out
                            .println("failed connected to db server with io host");
                    try {
                        Thread.sleep(SLEEP_RECONNECT_TIMEOUT);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                }
                break;
            }
        }

        private SuggestionsResult send(Request request)
                throws InterruptedException {
            SuggestionsResult sresult = null;
            while (true) {
                if (off) {
                    throw new InterruptedException();
                }
                client.send(request);
                try {
                    sresult = client.nextResponse();
                } catch (Exception ex) {
                    reconnect();
                    continue;
                }
                break;
            }
            return sresult;
        }

        private void connect() throws InterruptedException {
            try {
                client.connect();
            } catch (Exception e) {
                reconnect();
            }
        }

        @Override
        public void run() {
            try {
                connect();
                while (!off) {
                    while (!toSendQueue.isEmpty() && !off) {
                        AssyncRequest msg = toSendQueue.getFirst();
                        toSendQueue.pop();
                        SuggestionsResult result = send(msg.getRequest());
                        msg.getCallback().execute(result);
                    }
                    synchronized (this) {
                        wait();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                client.closeSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public synchronized void send(AssyncRequest request) {
            toSendQueue.add(request);
            notify();
        }

        public synchronized void off() {
            off = true;
            notify();
        }
    }

    private class AloneFilmProvider extends
            AsyncTask<SuggestionsResult, Object, Object> {
        AsyncTask<Film, Object, Object> callback;

        public AloneFilmProvider(AsyncTask<Film, Object, Object> callback) {
            super();
            this.callback = callback;
        }

        @Override
        protected Object doInBackground(SuggestionsResult... params) {
            synchronized (callback) {
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                for (Film film : params[0].getFilms().values()) {
                    runFilmGetterCallBack(callback, film);
                }
            }
            return null;
        }

    };

    private ConnectTaskLoop looper;
    private Client dbClient;

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
        dbClient = new Client(getResources().openRawResource(R.raw.db_conf));
        looper = new ConnectTaskLoop(dbClient);
        new Thread(looper).start();
    }

    @Override
    public void onDestroy() {
        looper.off();
        super.onDestroy();
    }

    public void requestToDb(String id, Integer level,
            AsyncTask<SuggestionsResult, Object, Object> callback) {
        looper.send(new AssyncRequest(new Request(0, level, id), callback));
    }

    private void runFilmGetterCallBack(
            final AsyncTask<Film, Object, Object> callback, Film film) {
        synchronized (callback) {
            if (callback.getStatus() == AsyncTask.Status.PENDING) {
                callback.execute(film);
            }
        }
    }

    private void requestToKp(final String id,
            final AsyncTask<Film, Object, Object> callback) {
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                synchronized (callback) {
                    try {
                        Film film = KpParser.parseFilm(PageLoader.loadFilm(id),
                                PageLoader.loadFilmSuggestions(id));
                        if (film != null) {
                            runFilmGetterCallBack(callback, film);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

        }.execute();
    }

    public void requestToDb(String id, AsyncTask<Film, Object, Object> callback) {
        requestToDb(id, 0, new AloneFilmProvider(callback));
        // under the question
        // requestToKp(id, callback);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}