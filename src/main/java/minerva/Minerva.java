package minerva;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tagging.PosTagger;
import tagging.Word;
import util.Constants;
import util.Pair;
import jules.Categorizer;
import jules.QueryPassager;
import jules.RankNouns;
import jules.Reranker;
import jules.ScoreWord;

public class Minerva {
	private static List<Map<String,String>> lastQuery;
	private static List<ScoreWord> lastTopNouns;
	private static List<ScoreWord> topNouns;
	private static String q;
	private static List<ScoreWord> topRerankedNouns;

	public Minerva(String query) {
		q = Constants.whiteList(query);
		lastTopNouns = null;
		topNouns = null;
		topRerankedNouns = null;
		lastQuery = QueryPassager.query(q, 100);
	}

	public List<ScoreWord> findTopNouns(){
		if (lastTopNouns == null) {
			topNouns = RankNouns.findTopNouns(lastQuery);
		}
		return topNouns;
	}
	
	public List<ScoreWord> findRerankedNouns() throws IOException{
		if (topRerankedNouns == null) {
			List<Pair<String, Double>> predictedCategories = Categorizer.getCategories(q);
			List<Word[]> words = PosTagger.getInstance().tagString(q);
			List<String> qLemmas = new ArrayList<String>();
			for (Word w : words.get(0)) {
				qLemmas.add(w.lemma);
			}
			topRerankedNouns = Reranker.getInstance().rerank(findTopNouns(), qLemmas, predictedCategories);
		}
		return topRerankedNouns;
	}

}
