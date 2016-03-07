package ru.kpsug.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import ru.kpsug.db.DBOperator;
import ru.kpsug.server.Suggestions.SuggestionsResult;
import ru.kpsug.server.tcpServer.ServerLog;

public class RequestHandler implements Runnable {

	@Override
	public void run() {
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				String response = RequestDispatcher.dispatch(line, this);
				writer.println(response);
				writer.flush();
				log.print("RQ : \"" + line + "\" || RE : \"" + response.substring(0, 1) + "\"");
			}
		} catch (IOException e) {
		}
		log.print("handler exits\n");
	}

	private BufferedReader reader;
	private PrintWriter writer;
	private Socket socket;
	private DBOperator db;
	private ServerLog log;

	public RequestHandler(Socket s, DBOperator db, ServerLog log) throws IOException {
		socket = s;
		this.db = db;
		this.log = log;
		InputStream input = socket.getInputStream();
		OutputStream output = socket.getOutputStream();
		reader = new BufferedReader(new InputStreamReader(input));
		writer = new PrintWriter(output);
	}

	SuggestionsResult processDb(String id, int depth) {
		return Suggestions.getFilmsAround(id, depth, db);
	}

}
