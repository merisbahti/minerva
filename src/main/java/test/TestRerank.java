package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import minerva.Minerva;
import tagging.PosTagger;
import tagging.ScoreWord;
import util.Constants;

public class TestRerank {
	
	static Map<String, String> questions;
	static PrintWriter writer;
	static int queries = 100;
	
	private static void setUp() throws Exception {
		questions = new HashMap<String, String>();
		File dir = new File(Constants.qDir);
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String outFile = queries + "P:" + sdf.format(date) + ".txt";
		System.out.println("OutFile: " + outFile);
		writer = new PrintWriter(outFile, "UTF-8");
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
			br.close();
		}
	}
	
	public static void testReranker() throws Exception {
		setUp();
		PosTagger tagger = PosTagger.getInstance();
		//writer.println(Integer.toString(questions.entrySet().size()));
		for (Entry<String, String> question : questions.entrySet()) {
			Minerva min = new Minerva(question.getKey(), queries);
			List<ScoreWord> topNouns = min.getTopNouns();
			List<ScoreWord> results = min.getRankedTopNouns();
			List<ScoreWord> punched = min.getPunchedRankedTopNouns();
			//int i = 0;
//			for (ScoreWord sw : results) {
//				String s = sw.lemma;
//				//i++;
//				if (s.equalsIgnoreCase(question.getValue())){
//					//			  index found 		rank				total number of nouns
//					writer.println(i + "\t" + sw.getTotalRank() + "\t" + results.size() + "\t" + question.getKey());
//					writer.flush();
//					break;
//				}
//			}
			String ans = question.getValue();
			
			ScoreWord swAns = new ScoreWord(tagger.tagString(ans).get(0)[0]);
			int t = topNouns.indexOf(swAns);
			int r = results.indexOf(swAns);
			int p = punched.indexOf(swAns);
			if(t >= 0){
				writer.println((t+1) + "\t" + (r+1) + "\t" + (p+1));
				writer.flush();
			}
			
		}
		writer.close();
	}


}
