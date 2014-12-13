package jules;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.sv.SwedishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;

import tagging.PosTagger;
import tagging.Word;

public class QueryPassager {
	public static List<Map<String, String>> query(String querystr, int nbrHits) {
		Analyzer analyzer = new CustomAnalyzer();
		String[] fieldNames = { "title", "text" };
		MultiFieldQueryParser mfqp = new MultiFieldQueryParser(fieldNames,
				analyzer);
		Query query = null;
		try {
			query = mfqp.parse(querystr.toLowerCase().replaceAll(
					"[^a-zåäö\\s]", ""));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// 3. search
		IndexReader reader = null;
		try {
			reader = DirectoryReader.open((FSDirectory.open(new File(
					Indexer.indexDir))));
		} catch (IOException e) {
			e.printStackTrace();
		}

		IndexSearcher searcher = new IndexSearcher(reader);
		searcher.setSimilarity(new BM25Similarity());

		TopScoreDocCollector collector = TopScoreDocCollector.create(nbrHits,
				true);
		try {
			searcher.search(query, collector);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		// 4. display results
		List<Map<String, String>> results = new ArrayList<Map<String, String>>();
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = null;
			try {
				d = searcher.doc(docId);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Map<String, String> tmpResult = new HashMap<String, String>();
			tmpResult.put("Score", Float.toString(hits[i].score));
			for (IndexableField field : d.getFields()) {
				tmpResult.put(field.name(), field.stringValue());
			}
			results.add(tmpResult);
		}

		return results;
	}

	private static String[] nounTags = { "NN", "PM" };

	public static List<ScoreWord> findTopNouns(
			List<Map<String, String>> docs) {
		PosTagger posTagger = null;
		try {
			posTagger = PosTagger.getInstance();
		} catch (IOException e1) {
			e1.printStackTrace();
			return new ArrayList<ScoreWord>();
		}

		HashMap<Word, Double> freqs = new HashMap<Word, Double>();
		
		for (Map<String, String> doc : docs) {
			//Extract score and use it in ScoreWord
			double score = Double.parseDouble(doc.get("Score"));
			Set<String> keys = doc.keySet();
			keys.remove("Score");

			for (String fieldKey : keys) {
				String fieldValue = doc.get(fieldKey);
				List<Word[]> sents = posTagger.tagString(fieldValue);
				for (Word[] sent : sents) {
					for (Word word : sent) {
						if(word.word.length() < 2 && !word.word.equalsIgnoreCase("ö|å")) continue;
						if (matchingPos(nounTags, word.pos)) {
							if (freqs.containsKey(word)) {
								freqs.put(word, freqs.get(word) + score);
							} else {
								freqs.put(word, (double) score);
							}
						}
					}
				}
			}
		}
		
		ArrayList<ScoreWord> scores = new ArrayList<ScoreWord>();
		for (Word freqKey : freqs.keySet()){
			ScoreWord sw = new ScoreWord(freqKey);
			sw.addNounIndexScore(freqs.get(freqKey));
			scores.add(sw);
		}

		Collections.sort(scores);
		return scores.size() > 100 ? scores.subList(0, 100) : scores;
	}


	private static boolean matchingPos(String[] tags, String pos) {
		for (String tag : tags) {
			if (tag.equalsIgnoreCase(pos))
				return true;
		}
		return false;
	}

}
