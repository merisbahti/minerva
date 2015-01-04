package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
		List<Integer> ts = new ArrayList<Integer>();
		List<Integer> rs = new ArrayList<Integer>();
		List<Integer> ps = new ArrayList<Integer>();
		//writer.println(Integer.toString(questions.entrySet().size()));
		for (Entry<String, String> question : questions.entrySet()) {
			Minerva min = new Minerva(question.getKey(), queries);
			String ans = question.getValue();
			ScoreWord swAns = new ScoreWord(tagger.tagString(ans).get(0)[0]);
			List<ScoreWord> topNouns = min.getTopNouns();
			int t = 1 + topNouns.indexOf(swAns);
			List<ScoreWord> results = min.getRankedTopNouns();
			int r = 1 + results.indexOf(swAns);
			List<ScoreWord> punched = min.getPunchedRankedTopNouns();
			int p = 1 + punched.indexOf(swAns);
			if(t > 0){
				ts.add(t);
				rs.add(r);
				ps.add(p);
				writer.println(t + "\t" + r + "\t" + p + "\t" + question);
				writer.flush();
			}
			
		}
		Collections.sort(ts);
		Collections.sort(rs);
		Collections.sort(ps);
		int tMedian = ts.get((int)(ts.size()/2));
		int rMedian = rs.get((int)(rs.size()/2));
		int pMedian = ps.get((int)(ps.size()/2));
		writer.println();
		writer.println(tMedian + "\t" + rMedian + "\t" + pMedian);
		double tMean = 0;
		double rMean = 0;
		double pMean = 0;
		for(int i = 0; i < ts.size(); i++){
			tMean += ts.get(i);
			rMean += rs.get(i);
			pMean += ps.get(i);
		}
		tMean = tMean / ts.size();
		rMean = rMean / rs.size();
		pMean = pMean / ps.size();
		writer.println(tMean + "\t" + rMean + "\t" + pMean);
		
		writer.close();
	}


}
