package ru.kpsug;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import ru.kpsug.db.Film;
import ru.kpsug.server.Client;
import ru.kpsug.server.Request;
import ru.kpsug.server.Suggestions;
import ru.kpsug.server.Suggestions.SuggestionsResult;

public class ClientRunner {
    public static void re(Client client){
        while(true){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                client.reconnect();
            } catch (IOException e2) {
                System.out.println("io host");
                continue;
            }
            break;
        }
    }
    
    public static void main(String[] args) throws IOException {
            Client client = new Client(null);
//          Socket s = null;
//          try {
//              s = new Socket(InetAddress.getByName("127.0.0.1"), 6666);
//              BufferedReader inp = new BufferedReader(new InputStreamReader(s.getInputStream()));
//              PrintWriter writer = new PrintWriter(s.getOutputStream());
//              String request = "2=1=373314";
//              writer.println(request);
//              writer.flush();
//              String line = inp.readLine();
//              System.out.println(line);
            try {
                client.connect();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                re(client);
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } 
            SuggestionsResult sresult = null ;
            while(true){
                client.send(new Request(0, 0, "373314"));
                try{
                     sresult = client.nextResponse();
                }catch(IOException ex){
                    re(client);
                    continue;
                }
                break;
            }
            for(Entry<String, Film> entry: sresult.getFilms().entrySet()){
                System.out.println(Double.parseDouble(entry.getValue().getRating()));
            }
            System.out.println();
            
            try {
                client.closeSocket();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

    }
}
