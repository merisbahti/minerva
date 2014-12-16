package web;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.FileInputStream;
import java.io.File;
import java.net.URI;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import minerva.Minerva;

import org.json.*;

import ranker.Reranker;
import tagging.PosTagger;
import tagging.ScoreWord;
import util.Constants;

public class WebService {

    public static void main(String[] args) {
        try {
            runner();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runner() throws Exception {
        System.out.println("Initializing server... plz w8");
        HttpServer server = HttpServer.create(new InetSocketAddress(1337), 0);
        System.out.println("Initializing pos-tagger ... plz w8");
        PosTagger.getInstance();
        System.out.println("Pos-tagger initialized....");
        System.out.println("Initializing re-ranker... plz w8");
        Reranker.getInstance();
        System.out.println("Re-ranker initialized....");
        server.createContext("/query", new QueryHandler());
        server.createContext("/", new StaticHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class QueryHandler implements HttpHandler {
        @SuppressWarnings("unused")
		public void handle(HttpExchange t) throws IOException {
            String response = "No query found, specify the query parameter.";
            Map <String,String> qMap = queryToMap(t.getRequestURI().getQuery() == null ? "" : t.getRequestURI().getQuery());
            JSONObject jsonResponse = new JSONObject();
            if (qMap.containsKey("q")) {
                StringBuilder sb = new StringBuilder();
                String q = qMap.get("q").toLowerCase();
                q = Constants.whiteList(q);
                Minerva min = new Minerva(q);
                List<Map<String, String>> results = min.getPassages();
                JSONArray paragraphs = new JSONArray();
                for (Map<String, String> result : results) {
                    JSONObject currArticle = new JSONObject();
                    for (Map.Entry<String, String> entry : result.entrySet()) {
                        // If it's text... we'll just take the context in this baseline
                        sb.append(entry.getKey() + ": \n");
                        if (entry.getKey().equals("text")) {
                            int indexOfHit = entry.getValue().toLowerCase().indexOf(q);
                            //String context = entry.getValue().toLowerCase().subSequence(indexOfHit-20, indexOfHit+20).toString()+"\n";
                            String context = entry.getValue();
                            sb.append(context);
                            try {
								currArticle.put(entry.getKey(), entry.getValue());
							} catch (JSONException e) {
								e.printStackTrace();
							}
                        } else {
                            sb.append(entry.getValue() + "\n");
                            try {
								currArticle.put(entry.getKey(), entry.getValue());
							} catch (JSONException e) {
								e.printStackTrace();
							}
                        }
                    }
                    paragraphs.put(currArticle);
                }
                JSONArray topAnswers = scoreWordToJsonArray(min.getTopNouns());
                JSONArray punchedTopAnswers= scoreWordToJsonArray(min.getPunchedTopNouns());
                JSONArray rankedPunchedTopAnswers = scoreWordToJsonArray(min.getRankedPunchedTopNouns());
                try{
	                jsonResponse.put("paragraphs", paragraphs);
	                jsonResponse.put("topAnswers", topAnswers);
	                jsonResponse.put("punchedTopAnswers", punchedTopAnswers);
                    jsonResponse.put("rankedPunchedTopAnswers", rankedPunchedTopAnswers);
	                response = jsonResponse.toString();
                } catch(Exception e){}
            }
            System.out.println("serving response");
            //t.setAttribute("content-type", "application/json");
            Headers h = t.getResponseHeaders();
            h.add("Access-Control-Allow-Origin", "*");
            h.add("content-type", "application/json; charset=utf-8");
            t.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static JSONArray scoreWordToJsonArray(List<ScoreWord> sws) {
        JSONArray topAnswers = new JSONArray();
        for (ScoreWord sw : sws) {
        	try{
	            JSONObject topAnswerObject = new JSONObject();
	            topAnswerObject.put("score", sw.getTotalRank());
	            topAnswerObject.put("word", sw.lemma);
	            topAnswers.put(topAnswerObject);
        	} catch (Exception e){}
        }
        return topAnswers;
    }

    static class StaticHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String root = "./wwwroot";
            URI uri = t.getRequestURI();
            System.out.println("looking for: "+ root + uri.normalize().getPath());
            String path = uri.normalize().getPath();
            System.out.println(path);
            File file = new File(path.equals("/") ? (root + "/index.html"): root+path).getCanonicalFile();
            System.out.println(file.getPath());
            System.out.println(file.isFile());
            if (uri.normalize().getPath().contains("..")) {
              String response = "420 blaze it. (Seriously, try a bit harder maybe.)\n";
              t.sendResponseHeaders(420, response.length());
              OutputStream os = t.getResponseBody();
              os.write(response.getBytes());
              os.close();
            } else if (!file.isFile()) {
              // Object does not exist or is not a file: reject with 404 error.
              String response = "404 (Not Found)\n";
              t.sendResponseHeaders(404, response.length());
              OutputStream os = t.getResponseBody();
              os.write(response.getBytes());
              os.close();
            } else {
              // Object exists and is a file: accept with response code 200.
              String mime = "text/html";
              if(path.endsWith(".js")) mime = "application/javascript";
              if(path.endsWith(".css")) mime = "text/css";            
              Headers h = t.getResponseHeaders();
              h.set("Content-Type", mime);
              t.sendResponseHeaders(200, 0);              
              OutputStream os = t.getResponseBody();
              FileInputStream fs = new FileInputStream(file);
              final byte[] buffer = new byte[0x10000];
              int count = 0;
              while ((count = fs.read(buffer)) >= 0) {
                os.write(buffer,0,count);
              }
              fs.close();
              os.close();
            }  
        }
    }

    private static Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        System.out.println("parsing: " + query);
        for (String param : query.split("&",1)) {
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
