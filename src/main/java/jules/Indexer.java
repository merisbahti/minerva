package jules;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.sv.SwedishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;
import edu.jhu.nlp.wikipedia.WikiXMLParser;
import edu.jhu.nlp.wikipedia.WikiXMLParserFactory;

public class Indexer {
	public static void main(String[] args) {
		bzIndexer();
		/*
		 * while(true){ System.out.println("Enter query:");
		 * System.out.print("> "); query(System.console().readLine(), false); }
		 */
	}

	private static String wikiFile = "./sewiki-20141104-pages-meta-current.xml";
	private static String indexDir = "./indexDir/";
	private static String outputDir = "./output/";
	private static int counter = 0;
	private static Analyzer analyzer;
	private static IndexWriterConfig iwc;
	private static IndexWriter writer;

	public static void bzIndexer() {
		analyzer = new SwedishAnalyzer();
		iwc = new IndexWriterConfig(Version.LATEST, analyzer);
		iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		try {
			writer = new IndexWriter(FSDirectory.open(new File(indexDir)), iwc);
		} catch (IOException e) {
			e.printStackTrace();
		}

		File output = new File(outputDir);
		ArrayList<File> files = new ArrayList<File>();
		for (File f : output.listFiles()) {
			for (File g : f.listFiles()) {
				files.add(g.getAbsoluteFile());
			}
		}
		for (File f : files) {
			doIndex(f.getAbsolutePath());
		}
		try {
			writer.commit();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void doIndex(String file) {
		try {
			BufferedReader br = getBufferedReaderForBZ2File(file);
			StringBuilder sb = null;
			String line;
			Document doc = null;
			String title;
			String subtitle;
			String text;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("<doc")) {
					sb = new StringBuilder();
				} else if (line.startsWith("</doc>")) {
					title = sb.toString().substring(0,
							sb.toString().indexOf("\n"));
					for (String s : sb.toString().split("subtitle123:")) {
						subtitle = s.substring(0, s.indexOf("\n"));
						text = s.substring(s.indexOf("\n") + 1);
						if (text.trim().isEmpty()) continue;
						
						doc = new Document();
						counter++;
						doc.add(new IntField("id", counter, Field.Store.YES));
						doc.add(new TextField("title", title, Field.Store.YES));
						doc.add(new TextField("subtitle", subtitle,
								Field.Store.YES));
						doc.add(new TextField("text", text, Field.Store.YES));
						writer.addDocument(doc);
						if (counter % 1000 == 0)
							System.out.println(counter);
					}
				} else if (line.startsWith("<h")) {
					sb.append("subtitle123:" + line.replaceAll("</?h\\d>", "")
							+ "\n");
				} else {
					sb.append(line.replaceAll("</?li>", "") + "\n");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(file);
		}

	}

	public static BufferedReader getBufferedReaderForBZ2File(String fileIn)
			throws FileNotFoundException, CompressorException {
		FileInputStream fin = new FileInputStream(fileIn);
		BufferedInputStream bis = new BufferedInputStream(fin);
		CompressorInputStream input = new CompressorStreamFactory()
				.createCompressorInputStream(bis);
		BufferedReader br2 = new BufferedReader(new InputStreamReader(input));

		return br2;
	}

	public static List<Map<String, String>> query(String querystr,int nbrHits) {
		Analyzer analyzer = new SwedishAnalyzer();
		String[] fieldNames = {"title","subtitle","text"};
		MultiFieldQueryParser mfqp = new MultiFieldQueryParser(fieldNames, analyzer);
		Query query = null;
		try {
			query = mfqp.parse(querystr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// 3. search
		IndexReader reader = null;
		try {
			reader = DirectoryReader
					.open((FSDirectory.open(new File(indexDir))));
		} catch (IOException e) {
			e.printStackTrace();
		}

		IndexSearcher searcher = new IndexSearcher(reader);
		searcher.setSimilarity(new BM25Similarity(2f, 0.75f));
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

	public static void index() {
		Analyzer analyzer = new SwedishAnalyzer();
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LATEST, analyzer);
		iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		try {
			final IndexWriter writer = new IndexWriter(
					FSDirectory.open(new File(indexDir)), iwc);
			WikiXMLParser wxsp = WikiXMLParserFactory.getSAXParser(wikiFile);
			wxsp.setPageCallback(new PageCallbackHandler() {
				@Override
				public void process(WikiPage page) {

					page.getText().split("=+ [A-ZÅÄÖa-zåäö]+ =+");
					/*
					 * We don't want to store unnecessary pages. Text starts
					 * with #OMDIRIGERING
					 */
					if (page.isDisambiguationPage()
							|| page.isRedirect()
							|| page.isSpecialPage()
							|| page.isStub()
							|| page.getTitle().startsWith("Användare:")
							|| page.getTitle()
									.startsWith("Användardiskussion:")
							|| page.getText().trim().toLowerCase()
									.startsWith("#redirect")
							|| page.getText().trim().startsWith("#omdirig")
							|| page.getID() == null
							|| page.getID().length() == 0) {
						System.out.println(page.getTitle());
						return;
					}
					Document doc = new Document();
					doc.add(new IntField("id", Integer.parseInt(page.getID()),
							Field.Store.YES));
					doc.add(new TextField("title", page.getTitle(),
							Field.Store.YES));
					doc.add(new TextField("text", page.getText(),
							Field.Store.YES));

					/**
					 * Should we add: Infobox, Links, Categories?
					 */

					if (doc.getField("text").stringValue().toLowerCase()
							.startsWith("#omdirigering"))
						return;

					try {
						writer.addDocument(doc);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			wxsp.parse();
			writer.commit();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
