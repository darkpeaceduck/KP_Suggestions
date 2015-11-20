package ru.kpsug.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.TreeMap;

import ru.kpsug.server.Suggestions.SuggestionsResult;
import ru.kpsug.utils.ConfigParser;

public class Client {
    private String host = "127.0.0.1";
    private int port = 6666;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    
    void parseConfig(InputStream config) throws IOException{
        Map<String, String> parsed = ConfigParser.parseConfigInp(config);
        if(parsed.containsKey("host")){
            host = parsed.get("host");
        }
        if(parsed.containsKey("port")){
            port = Integer.parseInt(parsed.get("port"));
        }
    }
    
    public Client(InputStream input) {
        if(input != null){
            try{
                parseConfig(input);
            }catch(IOException e){
            }
        }
    }
    
    public void connect() throws UnknownHostException, IOException{
        socket =new Socket(InetAddress.getByName(host), port); 
        writer = new PrintWriter(socket.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    
    public void send(Request request) {
        String result = String.valueOf(request.getType());
        result += "=" + request.getDepth();
        result += "=" + request.getId();
        writer.println(result);
        writer.flush();
    }
        
    public String nextResponseString() throws IOException{
        return reader.readLine();
    }
    
    public SuggestionsResult nextResponse() throws IOException{
        String s = nextResponseString();
        SuggestionsResult sresult = new SuggestionsResult();
        sresult.refreshStateFromJSONString(s);
        return sresult;
    }
    
    
    public void closeSocket() throws IOException{
        reader.close();
        writer.close();
        socket.close();
        writer = null;
        reader = null;
        socket = null;
    }
    
}
