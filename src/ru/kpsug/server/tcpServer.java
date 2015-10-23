package ru.kpsug.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import ru.kpsug.conf.ConfigParser;
import ru.kpsug.db.DBOperator;

public class tcpServer implements Runnable{
    private int port = 6666;
    volatile private DBOperator db;
    volatile private ServerSocket server_socket;
    
    private void parseConfig(String path) throws IOException{
        Map<String, String> result = ConfigParser.parseConfig(path);
        for(Entry<String, String> pair : result.entrySet()){
            String key = pair.getKey();
            String value = pair.getValue();
            switch(key){
            case "port" : 
                port = Integer.parseInt(value);
                break;
            }
        }
    }
    
    public tcpServer(String path) {
        if(path != null){
            try{
                parseConfig(path);
            }catch(IOException excp){
                
            }
        }
        db = new DBOperator(path);
    }
    
    public void start() {
        // TODO Auto-generated method stub
        
        try {
            db.connect();
            server_socket = new ServerSocket(port);
        } catch (SQLException | IOException e1) {
            System.out.println("failed init db and open socket");
            return;
        }
        BufferedReader reader  = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("success init db and open socket and stream");
        Thread child_control_thread = new Thread(this);
        child_control_thread.start();
        while(true){
            String command;
            try {
                command = reader.readLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                continue;
            }
            if(command.equals("quit")){
                break;
            }
        }
        
        try {
            server_socket.close();
            System.out.println("success closed server socket");
            child_control_thread.join();
        } catch (IOException e) {
            System.out.println("failed close socket, now kill child using stop");
            child_control_thread.stop();
        } catch (InterruptedException e) {
            System.out.println("failed join child, now kill child using stop");
            child_control_thread.stop();
        }
        try {
            db.closeAll();
        } catch (SQLException e) {
            System.out.println("failed close db");
        }
        System.out.println("OK");
    }

    @Override
    public void run() {
        System.out.println("Child control thread running now");
        while(!server_socket.isClosed()){
            Socket s;
            try {
                s = server_socket.accept();
                Thread child = new Thread(new RequestHandler(s, db));
                child.start();
            } catch (IOException e) {
                
            }
       }
    }
}
