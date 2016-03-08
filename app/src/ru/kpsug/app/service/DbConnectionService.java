package ru.kpsug.app.service;

import java.io.IOException;
import java.net.UnknownHostException;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import ru.kpsug.app.R;
import ru.kpsug.server.MyProtoClient;
import ru.kpsug.server.MyProtoRequest;
import ru.kpsug.server.Suggestions.SuggestionsResult;

public class DbConnectionService extends Service {

	public class DbConnectionBinder extends Binder {
		public DbConnectionService getService() {
			return DbConnectionService.this;
		}
	}

	public interface DbConnectionTaskCallback {
		void onDbConnectionTaskCallback(SuggestionsResult result);
	}

	private class DbAsyncRequest {
		private MyProtoRequest request;
		private DbConnectionTaskCallback callback;

		public MyProtoRequest getRequest() {
			return request;
		}

		public DbConnectionTaskCallback getCallback() {
			return callback;
		}

		public DbAsyncRequest(MyProtoRequest request, DbConnectionTaskCallback callback) {
			super();
			this.request = request;
			this.callback = callback;
		}

	}

	private class DbConnectionSendReceiveTask extends AsyncTask<DbAsyncRequest, Object, SuggestionsResult> {
		private static final int SLEEP_RECONNECT_TIMEOUT = 1000;
		private final MyProtoClient client;
		private DbAsyncRequest savedRequest;

		public DbConnectionSendReceiveTask(MyProtoClient client) {
			this.client = client;
		}

		private void reconnect() {
			while (true) {
				try {
					client.reconnect();
				} catch (IOException e2) {
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

		private SuggestionsResult send() {
			SuggestionsResult sresult = null;
			if(!client.isConnected()){
				try{
					client.connect();
				}catch(IOException e){
					e.printStackTrace();
					reconnect();
				}
			}
			while (true) {
				client.send(savedRequest.getRequest());
				try {
					sresult = client.nextResponse();
				} catch (Exception e) {
					e.printStackTrace();
					reconnect();
					continue;
				}
				break;
			}
			return sresult;
		}

		@Override
		protected SuggestionsResult doInBackground(DbAsyncRequest... params) {
			savedRequest = params[0];
			return send();
		}

		@Override
		protected void onPostExecute(SuggestionsResult result) {
			savedRequest.getCallback().onDbConnectionTaskCallback(result);
		}
	}

	private MyProtoClient dbClient;

	@Override
	public IBinder onBind(Intent intent) {
		return new DbConnectionBinder();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		dbClient = new MyProtoClient(getResources().openRawResource(R.raw.db_conf));
	}

	@Override
	public void onDestroy() {
		try {
			dbClient.closeSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	public void requestToDb(String id, Integer level, DbConnectionTaskCallback callback) {
		new DbConnectionSendReceiveTask(dbClient)
				.execute(new DbAsyncRequest(new MyProtoRequest(0, level, id), callback));
	}

	public void requestToDb(String id, DbConnectionTaskCallback callback) {
		requestToDb(id, 0, callback);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
}