package jules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import tagging.PosTagger;
import tagging.Word;
import util.Constants;
import util.Pair;

public class LiblinearInit {
	/**
	 * iterate over questions 
	 * 		gather unique question words 
	 * 		query qs and gather all answer words 
	 * 		gather question category stats (scaled?)
	 * 
	 * 
	 * 0/1 List<question> List<Answer> List<catstats>
	 * 
	 * @param args
	 */

	static File dir = new File(Constants.qDir);

	static TreeSet<String> uniqueQuestions = new TreeSet<String>();
	static TreeSet<String> uniqueAnswers = new TreeSet<String>();
	static String[] uniqueCategories = { "concept", "location", "definition",
			"description", "multiplechoice", "amount", "organization",
			"other", "person", "abbreviation", "verb", "title",
			"timepoint", "duration", "money" };
	
	
	public static void headerCreator(){

		try{
			PrintWriter writer = new PrintWriter(Constants.liblinearWordMap, "UTF-8");
			
			String line = "";
			int i = 1;
			
			for(String q : uniqueQuestions){
				line += "\t" + i + ":" + q;
				i++;
			}
			writer.println(line.trim());
			line = "";
			for(String a : uniqueAnswers){
				line += "\t" + i + ":" + a;
				i++;
			}

			writer.println(line.trim());
			line = "";
			for(String c : uniqueCategories){
				line += "\t" + i + ":" + c;
				i++;
			}
			writer.println(line.trim());
			writer.close();
		} catch (Exception e){
			
		}
	}
	
	public static void createLiblinearTrain() {

		List<String> corrAnswers = new ArrayList<String>();
		List<String> questions = new ArrayList<String>();
		List<List<String>> answers = new ArrayList<List<String>>();
		List<List<Pair<String, Double>>> catstats = new ArrayList<List<Pair<String, Double>>>();

		try {
			PosTagger tagger = PosTagger.getInstance();
			PrintWriter writer = new PrintWriter(Constants.liblinearTrain, "UTF-8");
			for (File f : dir.listFiles()) {
				if (f.getName().startsWith("."))
					continue;
				System.out.println("Reading file: " + f.getName());
				BufferedReader br = new BufferedReader(new FileReader(f));
				String line, question, corrAnswer;

				while ((line = br.readLine()) != null) {
					String[] cols = line.split("\t");
					corrAnswer = cols[6].toLowerCase();
					if (corrAnswer.contains(" ")) continue;

					corrAnswers.add(tagger.tagString(corrAnswer).get(0)[0].lemma);

					question = cols[5].toLowerCase();
					questions.add(question);

					List<String> as = new ArrayList<String>();
					for (ScoreWord sw : RankNouns.findTopNouns(QueryPassager.query(question, 100))) {
						as.add(sw.lemma);
						uniqueAnswers.add(sw.lemma);
					}
					answers.add(as);

					for (Word w : tagger.tagString(question).get(0)) {
						uniqueQuestions.add(w.lemma);
					}
					List<Pair<String, Double>> cStat = Categorizer.getCategories(question);
					catstats.add(cStat);
				}
				br.close();

			}
			int qSize = uniqueQuestions.size();
			int aSize = uniqueAnswers.size();
			int qaSize = qSize + aSize;

			String line = "";
			try {
				for (int i = 0; i < questions.size(); i++) {
					for (String str : answers.get(i)) {
						line = "";
						if (str.equalsIgnoreCase(corrAnswers.get(i))) {
							line += "1";
						} else {
							line += "0";
						}
						TreeSet<String> ulq = new TreeSet<String>();
						for (Word word : tagger.tagString(questions.get(i)).get(0)) {
							ulq.add(word.lemma);
						}
						for (String sulq : ulq) {
							line += "\t" + (uniqueQuestions.headSet(sulq).size()+1) + ":1";
						}
						line += "\t" + (uniqueAnswers.headSet(str).size()+1 + qSize) + ":1";
						for (int j = 0; j < uniqueCategories.length; j++) {
							try {
								for (Pair<String, Double> cs : catstats.get(i)) {
									if (cs.fst.equalsIgnoreCase(uniqueCategories[j])) {
										line += "\t" + (qaSize + j + 1) + ":" + cs.snd;
									}
								}
							} catch (Exception e) {
								continue;
							}
						}
						writer.println(line);
					}
				}

			} catch (NullPointerException npe) {
				npe.printStackTrace();
				System.out.println("nullpointer");
				System.out.println(line);
			}
			writer.close();
			headerCreator();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void testRankTopNouns(String q) throws IOException {
		List<Map<String, String>> res = QueryPassager.query(q, 100);
		List<ScoreWord> lm = RankNouns.findTopNouns(res);
		for (ScoreWord sw : lm) {
			System.out.println(sw.word + " : " + sw.lemma + " : " + sw.getTotalRank());
		}

	}
}
