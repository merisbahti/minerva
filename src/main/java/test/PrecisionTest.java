package test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jules.*;

public class PrecisionTest {
	private static Map<String,String> testStrings = new HashMap<String,String>();
	
	public static void main(String[] args) {
		initMap(testStrings);
		
		
		for(String query : testStrings.keySet()){
			testQuery(query, testStrings.get(query));
		}
	}
	
	private static float testQuery(String query, String answer){
		List<Map<String, String>> result = Indexer.query(query);
		// find all occurances of the answer
		// divide by all words in the result
		int ansOcc = 0;
		int wordCount = 0;
		for(Map<String,String> map : result){
			for(String quest : map.keySet()){
				String q = quest.replaceAll("[!?,]", "");
				String[] words = q.split("\\s+");
				
				wordCount += words.length;
			}
		}
		
		return (float)ansOcc/wordCount;
	}
	
	private static void initMap(Map<String,String> map){
		map.put("Vilket år är Göran Persson född?", "1949");
		map.put("Vad heter Sveriges huvudstad", "Stockholm");
		
	}

}
