package ru.kpsug;

import ru.kpsug.server.tcpServer;

public class tcpServerRunner {
	public static void main(String[] args) {
		tcpServer serv = new tcpServer(null, System.in, System.out);
		serv.start();
	}
}
