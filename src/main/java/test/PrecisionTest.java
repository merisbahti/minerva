package test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jules.*;

public class PrecisionTest {
	private static Map<String,String> testStrings = new HashMap<String,String>();
	
	public static void main(String[] args) {
		initMap(testStrings);
		
		System.out.println("====================");
		System.out.println("Precision test");
		System.out.println("====================");
		for(String query : testStrings.keySet()){
			double score = testQuery(query, testStrings.get(query));
			System.out.println("Query: " + query + "\t Score: " + score);
		}
	}
	
	private static double testQuery(String query, String answer){
		List<Map<String, String>> result = Indexer.query(query, true);
		// find all occurances of the answer
		// divide by all words in the result
		int ansOcc = 0;
		int wordCount = 0;
		String[] splittedAns = answer.replaceAll("[!?,]", "").split("\\s+");
		int n = splittedAns.length; //n-gram answer
		
		for(Map<String,String> map : result){
			for(String quest : map.keySet()){
				String q = quest.replaceAll("[!?,]", "");
				String[] words = q.split("\\s+");
				
				for(int i = 0; i <= words.length - n; i++){
					int matches = 0;
					
					for(int j = 0; j < n; j++){
						if(words[i+j].equals(splittedAns[j]))
							matches ++;
					}
					
					if(matches == n)
						ansOcc++;
				}
				
				wordCount += words.length - n + 1;
			}
		}
		
		return (double)ansOcc/wordCount;
	}
	
	private static void initMap(Map<String,String> map){
		map.put("Vilket år är Göran Persson född?", "1949");
		map.put("Vad heter Sveriges huvudstad", "Stockholm");
		
	}

}
