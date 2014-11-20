package jules;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.lucene.analysis.Analyzer;
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

public class QueryPassenger {
	public static List<Map<String, String>> query(String querystr, int nbrHits) {
		Analyzer analyzer = new SwedishAnalyzer();
		String[] fieldNames = { "title", "text" };
		MultiFieldQueryParser mfqp = new MultiFieldQueryParser(fieldNames,
				analyzer);
		Query query = null;
		try {
			query = mfqp.parse(querystr.replaceAll("[^a-zåäö\\s]", ""));
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

	public static LinkedHashMap<String, Integer> findTopNouns(
			List<Map<String, String>> docs) {
		PosTagger posTagger = null;
		try {
			posTagger = PosTagger.getInstance();
		} catch (IOException e1) {
			e1.printStackTrace();
			return new LinkedHashMap<String, Integer>();
		}

		TreeMap<String, Integer> freqs = new TreeMap<String, Integer>();
		for (Map<String, String> doc : docs) {
			for (String fieldValue : doc.values()) {
				List<Word[]> sents = posTagger.tagString(fieldValue);
				for (Word[] sent : sents) {
					for (Word word : sent) {
						if (matchingPos(nounTags, word.pos)) {
							if (freqs.containsKey(word.lemma)) {
								freqs.put(word.lemma, freqs.get(word.lemma) + 1);
							} else {
								freqs.put(word.lemma, 1);
							}
						}
					}
				}
			}
		}

		return sortByValue(freqs);
	}

	private static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(
			Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		LinkedHashMap<K, V> result = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	private static boolean matchingPos(String[] tags, String pos) {
		for (String tag : tags) {
			if (tag.equalsIgnoreCase(pos))
				return true;
		}
		return false;
	}

}
