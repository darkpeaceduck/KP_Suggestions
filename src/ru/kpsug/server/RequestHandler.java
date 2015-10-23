package ru.kpsug.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import ru.kpsug.db.DBOperator;
import ru.kpsug.db.Film;

public class RequestHandler implements Runnable{

    @Override
    public void run() {
        String line = null;
        try {
            while((line = reader.readLine()) != null){
                String response = RequestDispatcher.dispatch(line, this);
                System.out.println(line);
                System.out.println(response);
                writer.println(response);
                writer.flush();
            }
        } catch (IOException e) {
        }
        System.out.println("handler exits");
    }
    
    private BufferedReader reader;
    private PrintWriter writer;
    private Socket socket;
    private DBOperator db;
    
    public RequestHandler(Socket s, DBOperator db) throws IOException {
        socket = s;
        this.db = db;
        InputStream input = socket.getInputStream();
        OutputStream output = socket.getOutputStream();
        reader = new BufferedReader(new InputStreamReader(input));
        writer = new PrintWriter(output);
    }
    
    Suggestions processDb(String id, int depth){
        return new Suggestions(id, depth, db);
    }
    
}
