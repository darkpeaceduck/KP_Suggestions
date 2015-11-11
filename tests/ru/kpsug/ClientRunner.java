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
    private static String convert(String s){
//        try {
//            return new String(s.getBytes("UTF-8"), ("windows-1251"));
//        } catch (UnsupportedEncodingException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return null;
        return s;
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
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            client.send(new Request(0, 0, "373314"));
            SuggestionsResult sresult = client.nextResponse();
            for(Entry<String, Film> entry: sresult.getFilms().entrySet()){
                System.out.println(convert(entry.getKey()));
                System.out.println(convert(entry.getValue().toString()));
            }
//          TreeMap<Film, Integer> res = Suggestions.parseDepthMap(result);
//            System.out.println(res);
//            for(Entry<Film, Integer> e : res.entrySet()){
//                System.out.println(e.getKey());
//                System.out.println(e.getValue());
//            }
//            for(int i = 0; i < res.size(); i++){
//                System.out.println("CUrrent level " + i);
//                for(Film f : res.get(i)){
//                    System.out.println(f);
//                }
//            }
            try {
                client.closeSocket();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

    }
}
