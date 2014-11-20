package test;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jules.Indexer;

import org.junit.Before;
import org.junit.Test;

public class AnswerPresentTest {
	Map<String, String> questions;
	String qDir = "/home/julian/School/edan70/questions/";
	int[] querySizes = {25, 50, 75, 100, 150, 200, 250, 300};

	@Before
	public void setUp() throws Exception {
		questions = new HashMap<String, String>();
		File dir = new File(qDir);
		for(File f : dir.listFiles()){
			System.out.println("Reading file: " + f.getName());
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			while((line = br.readLine()) != null){
				String[] cols = line.split("\t");
				if(!cols[6].trim().contains(" "))
					questions.put(cols[5].trim().toLowerCase(), cols[6].trim().toLowerCase());
			}
		}
	}

	@Test
	public void test() {
		System.out.println(questions.size());
		int nbrTests = questions.size();
		int presentAnswers;
		for(int size : querySizes){
			presentAnswers = 0;
			for(Map.Entry<String, String> entry : questions.entrySet()){
				List<Map<String, String>> results = Indexer.query(entry.getKey(), size);
				if(checkAnswer(results, entry.getValue()))
					presentAnswers++;
			}
			System.out.println(size + "\t" + (float)presentAnswers/nbrTests);
		}
	}
	
	private boolean checkAnswer(List<Map<String,String>> results, String answer){
		for(Map<String, String> result : results){
			for(Map.Entry<String, String> field : result.entrySet()){
				if(field.getValue().toLowerCase().contains(answer))
					return true;
			}
		}
		return false;
	}

}
