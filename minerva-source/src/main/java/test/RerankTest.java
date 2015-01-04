package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import minerva.Minerva;
import ranker.Categorizer;
import ranker.Reranker;
import tagging.PosTagger;
import tagging.ScoreWord;
import tagging.Word;
import util.Pair;

public class RerankTest {

	public static void rerankTest() throws IOException{
		String q =  "Vad heter Sveriges huvudstad?";//"Vilket land ligger Reykjavik i?";
		Minerva minerva = new Minerva(q, 100);
		List<ScoreWord> topN = minerva.getTopNouns();
		List<Pair<String, Double>> cat = Categorizer.getCategories(q);
		Reranker ins = Reranker.getInstance();
		PosTagger tagger = PosTagger.getInstance();
		List<Word[]> words = tagger.tagString(q);
		List<String> qLemmas = new ArrayList<String>();
		for(Word w : words.get(0)){
			qLemmas.add(w.lemma);
			System.out.println(w);
		}
		List<ScoreWord> results = ins.rerank(topN, qLemmas, cat);
		for(ScoreWord res : results){
			System.out.println(res.lemma + ": " + res.getTotalRank() + " : " + res.getLiblinRank() + " : " + res.getNounIndexRank());
		}
	}
	
}
