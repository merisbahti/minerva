package minerva;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tagging.PosTagger;
import tagging.Word;
import util.Pair;
import jules.Categorizer;
import jules.QueryPassager;
import jules.RankNouns;
import jules.Reranker;
import jules.ScoreWord;

public class Minerva {
	private static Pair<String, List<Map<String,String>>> lastQuery;
	private static Pair<String, List<ScoreWord>> lastTopNouns;
	
	public static List<Map<String, String>> queryParagraphs(String query){
		List<Map<String, String>> list = QueryPassager.query(query, 100);
		lastQuery = new Pair<String, List<Map<String, String>>>(query, list);
		return list;
	}
	
	public static List<ScoreWord> findTopNouns(String query){
		List<Map<String, String>> paragraphs;
		if(lastQuery.fst.equalsIgnoreCase(query)){
			paragraphs = lastQuery.snd;
		}else{
			paragraphs = queryParagraphs(query);
		}
		List<ScoreWord> topNouns = RankNouns.findTopNouns(paragraphs);
		lastTopNouns = new Pair<String, List<ScoreWord>>(query, topNouns);
		return topNouns;
	}
	
	public static List<ScoreWord> findRerankedNouns(String query) throws IOException{
		List<ScoreWord> nouns;
		if(lastTopNouns.fst.equalsIgnoreCase(query)){
			nouns = lastTopNouns.snd;
		}else{
			nouns = findTopNouns(query);
		}
		List<Pair<String, Double>> predictedCategories = Categorizer.getCategories(query);
		List<Word[]> words = PosTagger.getInstance().tagString(query);
		List<String> qLemmas = new ArrayList<String>();
		for(Word w : words.get(0)){
			qLemmas.add(w.lemma);
		}
		return Reranker.getInstance().rerank(nouns, qLemmas, predictedCategories);
	}

}
