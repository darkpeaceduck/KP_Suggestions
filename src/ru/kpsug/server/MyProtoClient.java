package ru.kpsug.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

import ru.kpsug.server.Suggestions.SuggestionsResult;
import ru.kpsug.utils.MyParseUtils;

public class MyProtoClient {
	private static final String HOST_DEFAULT = "127.0.0.1";
	private static final int PORT_DEFAULT = 6666;

	private String host = HOST_DEFAULT;
	private int port = PORT_DEFAULT;
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;

	void parseConfig(InputStream config) throws IOException {
		Map<String, String> parsed = MyParseUtils.parseConfigInp(config);
		if (parsed.containsKey("host")) {
			host = parsed.get("host");
		}
		if (parsed.containsKey("port")) {
			port = Integer.parseInt(parsed.get("port"));
		}
	}

	public MyProtoClient(InputStream input) {
		if (input != null) {
			try {
				parseConfig(input);
			} catch (IOException e) {
			}
		}
	}

	public synchronized void connect() throws UnknownHostException, IOException {
		socket = new Socket(InetAddress.getByName(host), port);
		writer = new PrintWriter(socket.getOutputStream());
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public synchronized void send(MyProtoRequest request) {
		String result = String.valueOf(request.getType());
		result += "=" + request.getDepth();
		result += "=" + request.getId();
		writer.println(result);
		writer.flush();
	}

	public synchronized String nextResponseString() throws IOException {
		String ret = reader.readLine();
		if (ret == null) {
			throw new IOException();
		}
		return ret;
	}

	public synchronized SuggestionsResult nextResponse() throws IOException {
		String s = nextResponseString();
		SuggestionsResult sresult = new SuggestionsResult();
		sresult.refreshStateFromJSONString(s);
		return sresult;
	}

	public synchronized void closeSocket() throws IOException {
		if (reader != null) {
			reader.close();
		}
		if (writer != null) {
			writer.close();
		}
		if (socket != null) {
			socket.close();
		}
		writer = null;
		reader = null;
		socket = null;
	}

	public synchronized void reconnect() throws IOException {
		closeSocket();
		connect();
	}
	
	public synchronized boolean isConnected(){
		return socket != null;
	}

}
