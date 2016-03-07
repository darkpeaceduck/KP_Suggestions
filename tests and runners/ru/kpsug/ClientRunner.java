package ru.kpsug;

import java.io.IOException;

import ru.kpsug.server.MyProtoClient;
import ru.kpsug.server.MyProtoRequest;
import ru.kpsug.server.Suggestions.SuggestionsResult;

public class ClientRunner {
	public static void re(MyProtoClient client) {
		while (true) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				client.reconnect();
			} catch (IOException e2) {
				System.out.println("io host");
				continue;
			}
			break;
		}
	}

	public static void main(String[] args) throws IOException {
		MyProtoClient client = new MyProtoClient(null);
		// Socket s = null;
		// try {
		// s = new Socket(InetAddress.getByName("127.0.0.1"), 6666);
		// BufferedReader inp = new BufferedReader(new
		// InputStreamReader(s.getInputStream()));
		// PrintWriter writer = new PrintWriter(s.getOutputStream());
		// String request = "2=1=373314";
		// writer.println(request);
		// writer.flush();
		// String line = inp.readLine();
		// System.out.println(line);
		try {
			client.connect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			re(client);
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		SuggestionsResult sresult = null;
		while (true) {
			client.send(new MyProtoRequest(0, 0, "373314"));
			try {
				sresult = client.nextResponse();
			} catch (IOException ex) {
				re(client);
				continue;
			}
			break;
		}

		System.out.println(sresult);

		try {
			client.closeSocket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
