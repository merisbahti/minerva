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
            StringBuilder sb = new StringBuilder();
            Map<String, String> queryParams = queryToMap(t.getRequestURI().getQuery());
            if (queryParams.containsKey("q") && !queryParams.get("q").trim().equals("")) {
                sb.append(queryParams.get("q"));
                List<Map<String, String>> results = jules.Indexer.query(queryParams.get("q"));
                for (Map<String, String> result : results) {
                    sb.append("====================================================\n");
                    for (Map.Entry<String, String> entry : result.entrySet()) {
                        sb.append(entry.getKey() + ":\n");
                        sb.append(entry.getValue()+ "\n");
                    }
                    sb.append("====================================================\n");
                }
            } else {
                sb.append("No q specified.");
            }
            String response = sb.toString();
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }


    static private Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
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