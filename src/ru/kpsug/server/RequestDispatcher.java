package ru.kpsug.server;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class RequestDispatcher {
    private static String Recall(Request request, RequestHandler caller) {
          
       return Formatter.makeResponse(request, caller.processDb(request.getId(), request.getDepth() ));
    }


    public static String dispatch(String s, RequestHandler caller) {
        Request parsed_request = Formatter.parse(s);
        if(parsed_request == null){
            return Formatter.makeError();
        }
        return Recall(parsed_request, caller);
    }
}
