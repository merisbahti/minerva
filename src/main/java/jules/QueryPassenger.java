package jules;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.apache.lucene.store.FSDirectory;

public class QueryPassenger {
	public static List<Map<String, String>> query(String querystr,int nbrHits) {
		Analyzer analyzer = new SwedishAnalyzer();
		String[] fieldNames = {"title","text"};
		MultiFieldQueryParser mfqp = new MultiFieldQueryParser(fieldNames, analyzer);
		Query query = null;
		try {
			query = mfqp.parse(querystr.replaceAll("[^a-zåäö\\s]", ""));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// 3. search
		IndexReader reader = null;
		try {
			reader = DirectoryReader
					.open((FSDirectory.open(new File(Indexer.indexDir))));
		} catch (IOException e) {
			e.printStackTrace();
		}

		IndexSearcher searcher = new IndexSearcher(reader);
		//searcher.setSimilarity(new BM25Similarity());
		TopScoreDocCollector collector = TopScoreDocCollector.create(
				nbrHits, true);
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

}
