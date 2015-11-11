package ru.kpsug.server;

import ru.kpsug.server.Suggestions.SuggestionsResult;

public class AsyncClient {  
    public interface innerFunc<T, R>{
        public R run(T param) throws Exception;
    }
    
    private static class threadWrapper{
       static <T, R> Thread run(final innerFunc<T, R> mainFunc, final innerFunc<?, ?> onCatch, final innerFunc<? super R, ?> callback, final T param){
            Thread thread = (new Thread(new Runnable() {
                @Override
                public void run() {
                    R result = null;
                    try{
                        result = mainFunc.run(param);
                    }catch(Exception excp){
                        try {
                            if(onCatch != null){
                                onCatch.run(null);
                            }
                        } catch (Exception e) {}
                        return;
                    }
                    try {
                        if(callback != null){
                            callback.run(result);
                        }
                    } catch (Exception e) {}
                }
            }));
            thread.start();
            return thread;
        }
    }
    
    volatile Client client = null;
    
    public AsyncClient(String conf){
        client = new Client(conf);
    }
    
    public Thread connect(innerFunc<?, ?> onCatch, innerFunc<Object, ?> callback){
        return threadWrapper.run(new innerFunc<Object, Object>() {
            @Override
            public Object run(Object param) throws Exception {
                client.connect();
                return null;
            }
        }, onCatch, callback, null);
    }
    
    public Thread send(innerFunc<?, ?> onCatch, innerFunc<Object, ?> callback, Request param){
        return threadWrapper.run(new innerFunc<Request, Object>() {
            @Override
            public Object run(Request param) throws Exception {
                client.send(param);
                return null;
            }
        }, onCatch, callback, param);
    }
    
    public Thread nextResponseString(innerFunc<?, ?> onCatch, innerFunc<String, ?> callback){
        return threadWrapper.run(new innerFunc<Object, String>() {
            @Override
            public String run(Object param) throws Exception {
                return client.nextResponseString();
            }
        }, onCatch, callback, null);
    }
    
    public Thread nextResponse(innerFunc<?, ?> onCatch, innerFunc<SuggestionsResult, ?> callback){
        return threadWrapper.run(new innerFunc<Object, SuggestionsResult>() {
            @Override
            public SuggestionsResult run(Object param) throws Exception {
                return client.nextResponse();
            }
        }, onCatch, callback, null);
    }
    
    public Thread closeSocket(innerFunc<?, ?> onCatch, innerFunc<Object, ?> callback){
        return threadWrapper.run(new innerFunc<Object, Object>() {
            @Override
            public Object run(Object param) throws Exception {
                client.closeSocket();
                return null;
            }
        }, onCatch, callback, null);
    }
}
