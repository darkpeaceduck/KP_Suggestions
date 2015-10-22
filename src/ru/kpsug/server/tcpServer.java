package ru.kpsug.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
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
            db.connect();
            server_socket = new ServerSocket(port);
        } catch (SQLException | IOException e1) {
            System.out.println("failed init db and open socket");
            return;
        }
        System.out.println("success init db and open socket");
        while(true){
            Socket s;
            try {
                s = server_socket.accept();
                Thread child = new Thread(new RequestHandler(s, db));
                child.start();
            } catch (IOException e) {
                try {
                    db.closeAll();
                } catch (SQLException e1) {
                    System.out.println("failed close db and than\n");
                }
                e.printStackTrace();
            }
        }
    }
}
