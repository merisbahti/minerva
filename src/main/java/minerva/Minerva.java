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
	private String q;
	private Puncher puncher;
	private List<Pair<String, Double>> predictedCategories;
	private List<ScoreWord> topRerankedPunchedNouns;
	private List<ScoreWord> topPunchedNouns;
	private List<ScoreWord> rankedTopNouns;

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

	public static void normalize(List<ScoreWord> sws) {
		double sum = 0;
		for (ScoreWord sw : sws)
			sum += sw.getTotalRank();
		for (ScoreWord sw : sws)
			sw.normalizeScore(sum);
	}

	public List<Map<String, String>> getPassages() {
		return lastQuery;
	}

	public List<ScoreWord> getTopNouns(){
		if (topNouns == null) {
			topNouns = RankNouns.findTopNouns(lastQuery);
			topNouns = topNouns.subList(0, topNouns.size()>100 ? 100 : topNouns.size());
		}
		return topNouns;
	}

	public List<ScoreWord> getRankedTopNouns() throws IOException {
		if (rankedTopNouns == null) {
			List<Word[]> words = PosTagger.getInstance().tagString(q);
			List<String> qLemmas = new ArrayList<String>();
			for (Word w : words.get(0)) {
				qLemmas.add(w.lemma);
			}
			rankedTopNouns = Reranker.getInstance().rerank(getTopNouns(), qLemmas, predictedCategories);
		}
		return rankedTopNouns;
	}

	public List<ScoreWord> getPunchedRankedTopNouns() throws IOException {
		if (topRerankedPunchedNouns == null) {
			topRerankedPunchedNouns = puncher.punch(getRankedTopNouns(), predictedCategories);
			normalize(topRerankedPunchedNouns);
		}
		return topRerankedPunchedNouns;

	}
}
