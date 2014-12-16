package test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jules.*;
import minerva.Minerva;
import org.junit.Before;
import org.junit.Test;

/**
 * Simple naive precision test for query results.
 * Only checks how much of the total answer is the desired one.
 * The goal of our program is to answer questions with as high precision as possible
 * 
 * @author Tim Dolck
 *
 */
public class PrecisionTest {
	private static Map<String,String> testStrings = new HashMap<String,String>();

    @Before
    public void setUp() throws Exception {
        initMap(testStrings);
    }

    @Test
	public void test() {
		System.out.println("====================");
		System.out.println("Precision test");
		System.out.println("====================");
		for(String query : testStrings.keySet()){
			double score = testQuery(query, testStrings.get(query));
			System.out.println("Query: " + query + "\t Score: " + score);
		}
	}


	
	private static double testQuery(String query, String answer){
		//List<Map<String, String>> result = QueryPassager.query(query, 100);
		Minerva min = new Minerva(query, 100);
		List<Map<String, String>> result = min.getPassages();
		// find all occurrences of the answer
		// divide by all words in the result
		int ansOcc = 0;
		int wordCount = 0;
		String[] splittedAns = answer.replaceAll("[!?,]", "").split("\\s+");
		int n = splittedAns.length; //n-gram answer
		
		for(Map<String,String> map : result){
			for(String quest : map.keySet()){
				String q = map.get(quest).replaceAll("[!?,]", "");
				String[] words = q.split("\\s+");
				
				for(int i = 0; i <= words.length - n; i++){
					int matches = 0;
					
					for(int j = 0; j < n; j++){
						if(words[i+j].equals(splittedAns[j]))
							matches ++;
					}
					
					if (matches == n)
						ansOcc++;
				}
				
				wordCount += words.length - n + 1;
			}
		}
		System.out.println("Matches: " + ansOcc);
		System.out.println("Wordcount: " + wordCount);
		
		return (double)ansOcc/wordCount;
	}
	
	private static void initMap(Map<String,String> map){
		map.put("Sverige", "Stockholm");
		map.put("Vilket år är Göran Persson född?", "1949");
		map.put("Vad heter Sveriges huvudstad?", "Stockholm");
		map.put("Hur gammal blev Kurt Cobain?", "27");
		map.put("Vilken är Kinas tredje-största stad?", "Guangzhou");
		
	}

}
