package ru.kpsug.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import ru.kpsug.db.DBOperator;
import ru.kpsug.db.Film;

public class RequestHandler implements Runnable{

    @Override
    public void run() {
        String line = null;
        try {
            while((line = reader.readLine()) != null){
                RequestDispatcher.dispatch(line, this);
            }
        } catch (IOException e) {
        }
        System.out.println("handler exits");
    }
    
    private BufferedReader reader;
    private BufferedWriter writer;
    private Socket socket;
    private DBOperator db;
    public RequestHandler(Socket s, DBOperator db) throws IOException {
        socket = s;
        this.db = db;
        InputStream input = socket.getInputStream();
        OutputStream output = socket.getOutputStream();
        reader = new BufferedReader(new InputStreamReader(input));
        writer = new BufferedWriter(new OutputStreamWriter(output));
    }
    
    String processDb(String id, int depth){
        Film film = db.selectFilm(id);
        return null;
    }
    
}
