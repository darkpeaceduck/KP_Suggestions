package ru.kpsug.server;

import java.io.PrintWriter;

public class tcpServerRunner {
    public static void main(String[] args) {
         tcpServer serv= new tcpServer(null, System.in, System.out);
         serv.start();
    }
}
