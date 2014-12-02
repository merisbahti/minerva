package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jules.QueryPassager;

import org.junit.Before;
import org.junit.Test;

public class RankNounsTest {

	Map<String, String> questions;
	String qDir = "./questions/";
	PrintWriter writer;

	@SuppressWarnings("resource")
	@Before
	public void setUp() throws Exception {
		questions = new HashMap<String, String>();
		File dir = new File(qDir);
		writer = new PrintWriter("rankNounsMedianMrrD10test.txt", "UTF-8");
		for (File f : dir.listFiles()) {
			System.out.println("Reading file: " + f.getName());
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			while ((line = br.readLine()) != null) {
				String[] cols = line.split("\t");
				if (!cols[6].trim().contains(" "))
					questions.put(cols[5].trim().toLowerCase(), cols[6].trim()
							.toLowerCase());
			}
		}
	}

	@Test
	public void test() {
		//writer.println(Integer.toString(questions.entrySet().size()));
		for (Entry<String, String> question : questions.entrySet()) {
			//System.out.println(question.getKey());
			List<Map<String, String>> res = QueryPassager.query(
					question.getKey(), 10);

			LinkedHashMap<String, Integer> lm = QueryPassager
					.findTopNouns(res);
			
			int i = 0;
			for (String s : lm.keySet()) {
				i++;
				if (s.equalsIgnoreCase(question.getValue())){
					writer.println(i + "\t" + lm.keySet().size());
					writer.flush();
					break;
				}
			}
		}
		writer.close();

	}

}
