package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import minerva.Minerva;

import org.junit.Before;
import org.junit.Test;

import tagging.ScoreWord;
import util.Constants;

public class RankNounsTest {

	Map<String, String> questions;
	PrintWriter writer;
	int queries = 100;

	@SuppressWarnings("resource")
	@Before
	public void setUp() throws Exception {
		questions = new HashMap<String, String>();
		File dir = new File(Constants.qDir);
		writer = new PrintWriter("test.txt", "UTF-8");
		for (File f : dir.listFiles()) {
			if(f.getName().startsWith(".")) continue;
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
		for (Entry<String, String> question : questions.entrySet()) {
			int i = 0;
			Minerva min = new Minerva(question.getKey(), queries);
			for (ScoreWord sw : min.getTopNouns()) {
				String s = sw.lemma;
				i++;
				if (s.equalsIgnoreCase(question.getValue())){
					//			  index found 		rank				total number of nouns
					writer.println(i + "\t" + sw.getTotalRank() + "\t" + min.getTopNouns().size());
					writer.flush();
					break;
				}
			}
		}
		writer.close();
	}
}
