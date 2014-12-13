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
import util.Pair;

public class WekaLiblinear {
	/**
	 * iterate over questions gather unique question words query qs and gather
	 * all answer words gather question category stats (scaled?)
	 * 
	 * 
	 * 0/1 List<question> List<Answer> List<catstats>
	 * 
	 * @param args
	 */

	public static void createLiblinearTrain() {
		String qDir = "./questions/";
		File dir = new File(qDir);

		TreeSet<String> uniqueQuestions = new TreeSet<String>();
		TreeSet<String> uniqueAnswers = new TreeSet<String>();
		String[] uniqueCategories = { "concept", "location", "definition",
				"description", "multiplechoice", "amount", "organization",
				"other", "person", "abbreviation", "verb", "title",
				"timepoint", "duration" };

		List<String> corrAnswers = new ArrayList<String>();
		List<String> questions = new ArrayList<String>();
		List<List<String>> answers = new ArrayList<List<String>>();
		List<List<Pair<String, Double>>> catstats = new ArrayList<List<Pair<String, Double>>>();

		try {
			PosTagger tagger = PosTagger.getInstance();
			PrintWriter writer = new PrintWriter("train_file.scale", "UTF-8");
			for (File f : dir.listFiles()) {
				//if (!f.getName().startsWith("langt"))
				//	continue;
				System.out.println("Reading file: " + f.getName());
				BufferedReader br = new BufferedReader(new FileReader(f));
				String line, question, corrAnswer;

				while ((line = br.readLine()) != null) {
					String[] cols = line.split("\t");
					corrAnswer = cols[6].toLowerCase();
					if (corrAnswer.contains(" "))
						continue;
					corrAnswers
							.add(tagger.tagString(corrAnswer).get(0)[0].lemma);

					question = cols[5].toLowerCase();
					questions.add(question);

					List<String> as = new ArrayList<String>();
					for (ScoreWord sw : QueryPassager
							.findTopNouns(QueryPassager.query(question, 100))) {
						as.add(sw.lemma);
						uniqueAnswers.add(sw.lemma);
					}
					answers.add(as);

					for (Word w : tagger.tagString(question).get(0)) {
						uniqueQuestions.add(w.lemma);
					}
					List<Pair<String, Double>> cStat = Categorizer
							.getCategories(question);
					catstats.add(cStat);
				}
				br.close();

			}
			int qSize = uniqueQuestions.size();
			int aSize = uniqueAnswers.size();
			int qaSize = qSize + aSize;

			String line = "";
			try{
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
						line += "\t" + uniqueQuestions.headSet(sulq).size()
								+ ":1";
					}
					line += "\t" + (uniqueAnswers.headSet(str).size() + qSize)
							+ ":1";
					for (int j = 0; j < uniqueCategories.length; j++) {
						try{
						for (Pair<String, Double> cs : catstats.get(i)){
							if(cs.fst.equalsIgnoreCase(uniqueCategories[j])){
								line += "\t" + (qaSize+j+1) + ":" + cs.snd;
							}
						}
						}catch(Exception e){
							continue;
						}
					}
					writer.println(line);
				}
			}
			
			} catch (NullPointerException npe){
				npe.printStackTrace();
				System.out.println("nullpointer");
				System.out.println(line);
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public static void testRankTopNouns(String q) throws IOException{
		List<Map<String, String>> res = QueryPassager.query(q, 10);
		List<ScoreWord> lm = QueryPassager.findTopNouns(res);
		for(ScoreWord sw : lm){
			System.out.println(sw.word + " : " + sw.lemma + " : " + sw.getTotalRank());
		}
		
		PosTagger stagger = PosTagger.getInstance();
		List<Word[]> ss = stagger.tagString("stockholms");
		System.out.println(ss.get(0)[0].word + " : " + ss.get(0)[0].lemma);
		
	}
}
