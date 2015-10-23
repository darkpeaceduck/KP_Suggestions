package ru.kpsug.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        Socket s = null;
        try {
            s = new Socket(InetAddress.getByName("127.0.0.1"), 6666);
//            OutputStreamWriter outp = new OutputStreamWriter(s.getOutputStream());
            BufferedReader inp = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter writer = new PrintWriter(s.getOutputStream());
            String request = "2=1=373314";
            writer.println(request);
            writer.flush();
            String line = inp.readLine();
            System.out.println(line);
        } catch (IOException e) {
            System.out.println("failed");
            return;
        }
        System.out.println("OK");
    }
}
