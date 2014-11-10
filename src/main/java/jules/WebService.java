package jules;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.lucene.util.fst.PairOutputs;

public class WebService {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String response = "nothing done yet";
            System.out.println("Parsing qmap" + t.getRequestURI().getRawQuery());
            System.out.println("Parsing qmap" + t.getRequestURI().toString());
            System.out.println("added this line " + t.getRequestURI().toString().split("/")[1]);
            //System.out.println("Parsing qmap" + t.getRequestURI().getQuery().toString());
            //Map<String, String> qMap = queryToMap(t.getRequestURI().toString().split("/")[1]);
            Map<String, String> qMap = new HashMap<String, String>();
            qMap.put("q", "kung");

            for (Map.Entry<String, String> entry : qMap.entrySet())
                System.out.println(entry.getKey() + " : " + entry.getValue());

            System.out.println("checking for key");
            if (qMap.containsKey("q")) {
                System.out.println("key found");
                StringBuilder sb = new StringBuilder();
                String q = qMap.get("q");
                List<Map<String, String>> results =  jules.Indexer.query(q);
                for (Map<String, String> result : results) {
                    sb.append("===========================================\n");
                    for (Map.Entry<String, String> entry : result.entrySet()) {
                        // If it's text... we'll just take the context in this baseline
                        sb.append(entry.getKey() + ": \n");
                        if (entry.getKey().equals("text")) {
                            int indexOfHit = entry.getValue().toLowerCase().indexOf(q);
                            String context = entry.getValue().toLowerCase().subSequence(indexOfHit-20, indexOfHit+20).toString()+"\n";
                            sb.append(context);
                        } else {
                            sb.append(entry.getValue() + "\n");
                        }
                    }
                    sb.append("===========================================\n");
                }
                response = sb.toString();
            } else {
                System.out.println("!key found");
                response = "no query found! specify the q parameter!";
            }
            System.out.println("serving response");
            t.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    static private Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        System.out.println("parsing: " + query);
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }
}