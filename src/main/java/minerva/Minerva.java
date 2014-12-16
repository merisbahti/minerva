package minerva;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ranker.Categorizer;
import ranker.Puncher;
import ranker.RankNouns;
import ranker.Reranker;
import tagging.PosTagger;
import tagging.ScoreWord;
import tagging.Word;
import util.Constants;
import util.Pair;
import lucene.QueryPassager;

public class Minerva {
	private List<Map<String,String>> lastQuery;
	private List<ScoreWord> topNouns;
	private List<ScoreWord> topRerankedNouns;
	private String q;
	private Puncher puncher;
	private List<Pair<String, Double>> predictedCategories;
	private List<ScoreWord> topRerankedPunchedNouns;
	private List<ScoreWord> topPunchedNouns;

	public Minerva(String query, int nbrHits) {
		q = Constants.whiteList(query);
		lastQuery = QueryPassager.query(q, nbrHits);
		puncher = new Puncher();
		predictedCategories = Categorizer.getCategories(q);
	}

	public Minerva(String query) {
		q = Constants.whiteList(query);
		lastQuery = QueryPassager.query(q, 100);
		puncher = new Puncher();
		predictedCategories = Categorizer.getCategories(q);
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

	public List<ScoreWord> getPunchedTopNouns() {
		if (topPunchedNouns == null) {
            topPunchedNouns = puncher.punch(getTopNouns(), predictedCategories);
		}
		return topPunchedNouns;
	}

	public List<ScoreWord> getRankedPunchedTopNouns() throws IOException {
		if (topRerankedPunchedNouns == null) {
			List<Word[]> words = PosTagger.getInstance().tagString(q);
			List<String> qLemmas = new ArrayList<String>();
			for (Word w : words.get(0)) {
				qLemmas.add(w.lemma);
			}
			topRerankedPunchedNouns = Reranker.getInstance().rerank(getPunchedTopNouns(), qLemmas, predictedCategories);
		}
		return topRerankedPunchedNouns;

	}
}
