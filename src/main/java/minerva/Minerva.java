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
	private List<Map<String,String>> lastQuery;
	private List<ScoreWord> topNouns;
	private List<ScoreWord> topRerankedNouns;
	private String q;

	public Minerva(String query, int nbrHits) {
		q = Constants.whiteList(query);
		lastQuery = QueryPassager.query(q, nbrHits);
	}

	public Minerva(String query) {
		q = Constants.whiteList(query);
		lastQuery = QueryPassager.query(q, 100);
	}

	public List<Map<String, String>> getPassages() {
		return lastQuery;
	}

	public List<ScoreWord> getTopNouns(){
		if (topNouns == null) {
			topNouns = RankNouns.findTopNouns(lastQuery);
		}
		return topNouns;
	}
	
	public List<ScoreWord> getRerankedTopNouns() throws IOException{
		if (topRerankedNouns == null) {
			List<Pair<String, Double>> predictedCategories = Categorizer.getCategories(q);
			List<Word[]> words = PosTagger.getInstance().tagString(q);
			List<String> qLemmas = new ArrayList<String>();
			for (Word w : words.get(0)) {
				qLemmas.add(w.lemma);
			}
			topRerankedNouns = Reranker.getInstance().rerank(getTopNouns(), qLemmas, predictedCategories);
		}
		return topRerankedNouns;
	}

}
