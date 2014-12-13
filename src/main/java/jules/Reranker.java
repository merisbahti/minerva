package jules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import tagging.PosTagger;
import tagging.Word;
import util.*;

/**
 * VERY experimental
 * 
 * @author timdolck
 *
 */

public class Reranker {
	private static Reranker instance = null;
	private Model model;
	private Map<String, Integer> questionsMap, answersMap, categoriesMap;
	public static void rerankTest() throws IOException{
		String q = "Vad heter Sveriges huvudstad?";
		List<Map<String, String>> list = QueryPassager.query(q, 100);
		List<ScoreWord> topN = QueryPassager.findTopNouns(list);
		for(ScoreWord s : topN){
			System.out.println(s.toString());
		}
		List<Pair<String, Double>> cat = Categorizer.getCategories(q);
		Reranker ins = Reranker.getInstance();
		PosTagger tagger = PosTagger.getInstance();
		List<Word[]> words = tagger.tagString(q);
		List<String> qLemmas = new ArrayList<String>();
		for(Word w : words.get(0)){
			qLemmas.add(w.lemma);
		}
		List<ScoreWord> results = ins.rerank(topN, qLemmas, cat);
		for(ScoreWord res : results){
			System.out.println(res.lemma + ": " + res.getTotalRank());
		}
	}
	
	protected Reranker() throws IOException{
		File file = new File(Constants.liblinearModel);
		model = Model.load(file);
		BufferedReader br = new BufferedReader(new FileReader(new File(Constants.liblinearWordMap)));
		questionsMap = new HashMap<String,Integer>();
		answersMap = new HashMap<String,Integer>();
		categoriesMap = new HashMap<String,Integer>();
		
		String line;
		Map<String,Integer> current = questionsMap;
		while((line = br.readLine()) != null){
			String[] splits = line.split("\t");
			for(String unit : splits){
				String[] ic = unit.split(":", 2);
				int c = Integer.parseInt(ic[0]);
				current.put(ic[1], c-1);
			}
			current = changeCurrentMap(current);
		}
		br.close();
	}
	
	private Map<String,Integer> changeCurrentMap(Map<String,Integer> current){
		if(current == questionsMap){
			return answersMap;
		}else if(current == answersMap){
			return categoriesMap;
		}else{
			return questionsMap;
		}
	}
	
	public static Reranker getInstance() throws IOException{
		if(instance == null){
			instance = new Reranker();
		}
		return instance;
	}
	
	/**
	 * Reranks the topwords using liblinear prediction
	 * 
	 * @param topWords
	 * @param question
	 * @param predictedCategories
	 * @return
	 */
	public List<ScoreWord> rerank(List<ScoreWord> topWords, List<String> question, List<Pair<String,Double>> predictedCategories){
		for(ScoreWord sw : topWords){
			ArrayList<Feature> features = new ArrayList<Feature>();
			for(Pair<String,Double> f : predictedCategories){
				try{
				features.add(new FeatureNode(this.categoriesMap.get(f.fst), f.snd));
				}catch(Exception e){
					
				}
			}
			for(String word : question){
				features.add(new FeatureNode(this.questionsMap.get(word), 1));
			}
			try{
			features.add(new FeatureNode(this.answersMap.get(sw.lemma), 1));
			}catch(Exception e){}
			Collections.sort(features, new Comparator<Feature>(){
				@Override
				public int compare(Feature f1, Feature f2) {
	                return Integer.compare(f2.getIndex(), f1.getIndex());
	            }
			});
			
			Feature[] f = new Feature[features.size()];
			f = (Feature[]) features.toArray(f);
			double prediction = Linear.predict(model, f);
			double[] dbs = {(double) 0,(double) 1};
			Linear.predictProbability(model, f, dbs);
			sw.addliblinRank(dbs[1]);
		}
		Collections.sort(topWords);
		return topWords;
	}

}
