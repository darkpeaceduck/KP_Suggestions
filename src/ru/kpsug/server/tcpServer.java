package ru.kpsug.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

import ru.kpsug.db.DBOperator;
import ru.kpsug.utils.ParseUtils;

public class tcpServer implements Runnable {
	public static class ServerLog {
		private PrintWriter log;

		public ServerLog(OutputStream log) {
			this.log = new PrintWriter(log);
		}

		public synchronized void print(String s) {
			log.println(s);
			log.flush();
		}
	}

	private int DEFAULT_PORT = 6666;

	private int port = DEFAULT_PORT;
	volatile private DBOperator db;
	volatile private ServerSocket server_socket;
	volatile private BufferedReader control_input;
	volatile private ServerLog log;

	private void parseConfig(String path) throws IOException {
		Map<String, String> result = ParseUtils.parseConfig(path);
		for (Entry<String, String> pair : result.entrySet()) {
			String key = pair.getKey();
			String value = pair.getValue();
			switch (key) {
			case "port":
				port = Integer.parseInt(value);
				break;
			}
		}
	}

	private String in() {
		synchronized (control_input) {
			try {
				return control_input.readLine();
			} catch (IOException e) {
				return null;
			}
		}
	}

	public tcpServer(String conf_path, InputStream control_input, OutputStream log) {
		this.control_input = new BufferedReader(new InputStreamReader(control_input));
		this.log = new ServerLog(log);
		if (conf_path != null) {
			try {
				parseConfig(conf_path);
			} catch (IOException excp) {

			}
		}
		db = new DBOperator(conf_path);
	}

	public void start() {
		// TODO Auto-generated method stub

		try {
			db.connect();
			server_socket = new ServerSocket(port);
		} catch (SQLException | IOException e1) {
			log.print("failed init db and open socket");
			return;
		}
		log.print("success init db and open socket and stream");
		Thread child_control_thread = new Thread(this);
		child_control_thread.start();
		while (true) {
			String command;
			if ((command = in()) == null) {
				continue;
			}
			if (command.equals("quit")) {
				break;
			}
		}

		try {
			server_socket.close();
			log.print("success closed server socket");
			child_control_thread.join();
		} catch (IOException e) {
			log.print("failed close socket, now kill child using stop");
			child_control_thread.interrupt();
		} catch (InterruptedException e) {
			log.print("failed join child, now kill child using stop");
			child_control_thread.interrupt();
		}
		try {
			db.closeAll();
		} catch (SQLException e) {
			log.print("failed close db");
		}
		log.print("OK");
	}

	@Override
	public void run() {
		log.print("Child control thread running now");
		while (!server_socket.isClosed()) {
			Socket s;
			try {
				s = server_socket.accept();
				Thread child = new Thread(new RequestHandler(s, db, log));
				child.start();
			} catch (IOException e) {

			}
		}
	}
}
