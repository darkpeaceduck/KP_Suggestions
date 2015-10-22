package ru.kpsug.server;

public class tcpServerRunner {
    public static void main(String[] args) {
         tcpServer serv= new tcpServer(null);
         serv.start();
    }
}
