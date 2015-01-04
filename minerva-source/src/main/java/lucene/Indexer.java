package lucene;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import util.Constants;
import edu.jhu.nlp.wikipedia.PageCallbackHandler;
import edu.jhu.nlp.wikipedia.WikiPage;
import edu.jhu.nlp.wikipedia.WikiXMLParser;
import edu.jhu.nlp.wikipedia.WikiXMLParserFactory;

public class Indexer {
	private static int counter = 0;
	private static Analyzer analyzer;
	private static IndexWriterConfig iwc;
	private static IndexWriter writer;

	public static void bzIndexer() {
		long start = System.currentTimeMillis();
		analyzer = new CustomAnalyzer();
		iwc = new IndexWriterConfig(Version.LATEST, analyzer);
		iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		try {
			writer = new IndexWriter(FSDirectory.open(new File(Constants.indexDir)), iwc);
		} catch (IOException e) {
			e.printStackTrace();
		}

		File output = new File(Constants.outputDir);
		ArrayList<File> files = new ArrayList<File>();
		for (File f : output.listFiles()) {
			for (File g : f.listFiles()) {
				files.add(g.getAbsoluteFile());
			}
		}
		for (File f : files) {
			//doIndexDocuments(f.getAbsolutePath());
			doIndex(f.getAbsolutePath());
		}
		try {
			writer.commit();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Total time: " + (System.currentTimeMillis()-start)/1000);

	}

	/**
	 * Do index by paragrafs
	 * 
	 * @param file
	 */
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
	
	/**
	 * Do index by documents
	 * 
	 * @param file
	 */
	public static void doIndexDocuments(String file){
		try {
			BufferedReader br = getBufferedReaderForBZ2File(file);
			StringBuilder sb = null;
			String line;
			Document doc = null;
			String title;
			String text;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("<doc")) {
					sb = new StringBuilder();
				} else if (line.startsWith("</doc>")) {
					title = sb.toString().substring(0,sb.toString().indexOf("\n"));
					text = sb.toString().substring(sb.toString().indexOf("\n")+1);
					doc = new Document();
					counter++;
					doc.add(new IntField("id", counter, Field.Store.YES));
					doc.add(new TextField("title", title, Field.Store.YES));
					doc.add(new TextField("text", text, Field.Store.YES));
					writer.addDocument(doc);
					if (counter % 1000 == 0)
						System.out.println(counter);

				} else if (line.startsWith("<h")) {
					sb.append(line.replaceAll("</?h\\d>", "") + "\n");
				} else {
					sb.append(line.replaceAll("</?li>", "") + "\n");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(file);
		}

	}

	private static BufferedReader getBufferedReaderForBZ2File(String fileIn)
			throws FileNotFoundException, CompressorException {
		FileInputStream fin = new FileInputStream(fileIn);
		BufferedInputStream bis = new BufferedInputStream(fin);
		CompressorInputStream input = new CompressorStreamFactory()
				.createCompressorInputStream(bis);
		BufferedReader br2 = new BufferedReader(new InputStreamReader(input));

		return br2;
	}

	

	public static void index() {
		Analyzer analyzer = new CustomAnalyzer();
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LATEST, analyzer);
		iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		try {
			final IndexWriter writer = new IndexWriter(
					FSDirectory.open(new File(Constants.indexDir)), iwc);
			WikiXMLParser wxsp = WikiXMLParserFactory.getSAXParser(Constants.wikiFile);
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
