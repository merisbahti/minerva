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
import util.Pair;


/**
 * VERY experimental
 * 
 * @author timdolck
 *
 */

public class Reranker {
	
	private static String modelFile = "MDOELFILE";
	private static final String indexFile = "index";
	private Reranker instance = null;
	private Model model;
	private Map<String, Integer> questionsMap, answersMap, categoriesMap;
	
	protected Reranker() throws IOException{
		File file = new File(modelFile);
		model = Model.load(file);
		BufferedReader br = new BufferedReader(new FileReader(new File(indexFile)));
		questionsMap = new HashMap<String,Integer>();
		answersMap = new HashMap<String,Integer>();
		categoriesMap = new HashMap<String,Integer>();
		
		String line;
		Map<String,Integer> current = questionsMap;
		while((line = br.readLine()) != null){
			if(line.isEmpty())
				current = changeCurrentMap(current);
			String[] splits = line.split("\t");
			current.put(splits[1], Integer.getInteger(splits[0]));
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
	
	public Reranker getInstance() throws IOException{
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
				features.add(new FeatureNode(this.categoriesMap.get(f.fst), f.snd));
			}
			for(String word : question){
				features.add(new FeatureNode(this.questionsMap.get(word), 1));
			}
			features.add(new FeatureNode(this.answersMap.get(sw.lemma), 1));
			
			Collections.sort(features, new Comparator<Feature>(){
				@Override
				public int compare(Feature f1, Feature f2) {
	                return Integer.compare(f2.getIndex(), f1.getIndex());
	            }
			});
			
			Feature[] instance = (Feature[]) features.toArray();
			double prediction = Linear.predict(model, instance);
			sw.addliblinRank(prediction);
		}
		Collections.sort(topWords);
		return topWords;
	}

}
