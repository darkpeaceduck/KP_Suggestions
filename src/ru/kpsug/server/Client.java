package ru.kpsug.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        Socket s = null;
        try {
            s = new Socket(InetAddress.getByName("127.0.0.1"), 6666);
            OutputStreamWriter outp = new OutputStreamWriter(s.getOutputStream());
            outp.append("FUCK THE WORLD\n");
            outp.flush();
            Thread.sleep(1000);
            outp.append("FUCK THE WORLD AGAIN\n");
            outp.flush();
        } catch (IOException e) {
            System.out.println("failed open socket");
        }
        System.out.println("OK");
    }
}
