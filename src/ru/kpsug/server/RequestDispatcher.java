package ru.kpsug.server;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class RequestDispatcher {
    private static String Recall(Request request, RequestHandler caller) {
          
       return RequestFormatter.makeResponse(request, caller.processDb(request.getId(), request.getDepth() ));
    }


    public static String dispatch(String s, RequestHandler caller) {
        Request parsed_request = RequestFormatter.parse(s);
        if(parsed_request == null){
            return RequestFormatter.makeError();
        }
        return Recall(parsed_request, caller);
    }
}
