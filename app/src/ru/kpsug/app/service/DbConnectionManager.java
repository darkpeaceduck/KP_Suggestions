package ru.kpsug.app.service;

import java.io.IOException;

import android.content.res.Resources;
import android.os.AsyncTask;
import ru.kpsug.app.R;
import ru.kpsug.server.SugProtoClient;
import ru.kpsug.server.SugProtoRequest;
import ru.kpsug.server.SuggestionsCalculator.SuggestionsResult;

public class DbConnectionManager {
	public static class DbConnectionManagerResponse {
		public enum State {
			ERROR, SUCCESS
		}

		private State state;
		private SuggestionsResult result;

		public DbConnectionManagerResponse(SuggestionsResult result) {
			super();
			this.result = result;
			this.state = State.SUCCESS;
			if (result == null) {
				state = State.ERROR;
			}
		}

		public State getState() {
			return state;
		}

		public SuggestionsResult getResult() {
			return result;
		}

		public boolean isError() {
			return state == State.ERROR;
		}
	}

	public interface DbConnectionTaskCallback {
		void onDbConnectionTaskCallback(DbConnectionManagerResponse result);
	}

	private static class DbAsyncRequest {
		private SugProtoRequest request;
		private DbConnectionTaskCallback callback;

		public SugProtoRequest getRequest() {
			return request;
		}

		public DbConnectionTaskCallback getCallback() {
			return callback;
		}

		public DbAsyncRequest(SugProtoRequest request, DbConnectionTaskCallback callback) {
			super();
			this.request = request;
			this.callback = callback;
		}

	}

	private static class DbConnectionSendReceiveTask
			extends AsyncTask<DbAsyncRequest, Object, DbConnectionManagerResponse> {
		private static final int SLEEP_RECONNECT_TIMEOUT = 300;
		private static final int RECONNECT_TRYES = 2;
		private static final int SEND_TRYES = 1;

		private final SugProtoClient client;
		private DbAsyncRequest savedRequest;

		public DbConnectionSendReceiveTask(SugProtoClient client) {
			this.client = client;
		}

		private void reconnect() {
			for (int i = 0; i < RECONNECT_TRYES; i++) {
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

		private DbConnectionManagerResponse send() {
			SuggestionsResult sresult = null;
			if (!client.isConnected()) {
				try {
					client.connect();
				} catch (IOException e) {
					e.printStackTrace();
					reconnect();
				}
			}

			if (client.isConnected()) {
				for (int i = 0; i < SEND_TRYES; i++) {
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
			}
			return new DbConnectionManagerResponse(sresult);
		}

		@Override
		protected DbConnectionManagerResponse doInBackground(DbAsyncRequest... params) {
			savedRequest = params[0];
			return send();
		}

		@Override
		protected void onPostExecute(DbConnectionManagerResponse result) {
			savedRequest.getCallback().onDbConnectionTaskCallback(result);
		}
	}

	private static SugProtoClient newSugProtoClient(Resources resourses) {
		return new SugProtoClient(resourses.openRawResource(R.raw.db_conf));
	}

	public static void requestToDb(Resources resourses, String id, Integer level, DbConnectionTaskCallback callback) {
		new DbConnectionSendReceiveTask(newSugProtoClient(resourses))
				.execute(new DbAsyncRequest(new SugProtoRequest(0, level, id), callback));
	}

	public static void requestToDb(Resources resourses, String id, DbConnectionTaskCallback callback) {
		requestToDb(resourses, id, 0, callback);
	}
}