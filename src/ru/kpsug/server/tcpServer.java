package ru.kpsug.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Map.Entry;

import ru.kpsug.conf.ConfigParser;
import ru.kpsug.db.DBOperator;

public class tcpServer{
    private int port = 6666;
    private DBOperator db;
    
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
        ServerSocket server_socket;
        try {
            server_socket = new ServerSocket(port);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            return;
        }
        while(true){
            Socket s;
            try {
                s = server_socket.accept();
                Thread child = new Thread(new RequestHandler(s, db));
                child.start();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
