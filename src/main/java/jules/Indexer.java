package jules;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.jhu.nlp.wikipedia.*;


public class Indexer {
	public static void Main(String[] args){
		Indexer indexer = new Indexer();
		indexer.index();
	}
	private String wikiFile;
	private String indexDir;
	
	
	public Indexer(){
		wikiFile = "./svwiki-20141031-pages-meta-current.xml";
		indexDir = "./indexDir/";
	}
	
	public void index(){
		//WikiXMLParser wxp = WikiXMLParserFactory.getDOMParser(wikiFile);
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LATEST, analyzer);
		try{
			final IndexWriter writer = new IndexWriter(FSDirectory.open(new File(indexDir)), iwc);
			
			WikiXMLParser wxsp = WikiXMLParserFactory.getSAXParser(wikiFile);
			
			wxsp.setPageCallback(new PageCallbackHandler() {
				
				@Override
				public void process(WikiPage page) {
					Document doc = new Document();
					System.out.println(page.getID() + ", title: " + page.getTitle());
					doc.add(new IntField("id", Integer.parseInt(page.getID()), Field.Store.YES));
					doc.add(new TextField("title", page.getTitle(), Field.Store.YES));
					doc.add(new TextField("text", page.getText(), Field.Store.YES));
					try {
						writer.addDocument(doc);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			});
			
//			wxp.parse();
//			WikiPageIterator it = wxp.getIterator();
//			while(it.hasMorePages()){
//				page = it.nextPage();
//				Document doc = new Document();
//				System.out.println(page.getID() + ": Adding File: " + page.getTitle());
//				doc.add(new IntField("id", Integer.parseInt(page.getID()), Field.Store.NO));
//				doc.add(new TextField("title", page.getTitle(), Field.Store.NO));
//				doc.add(new TextField("text", page.getText(), Field.Store.NO));
//				
//				/*
//				 * Check also
//				 * 
//				 * Infobox
//				 * Categories
//				 * Links
//				 * Redirect pages
//				 */
//				
//				writer.addDocument(doc);
//			}
			writer.commit();
			writer.close();
		}catch(Exception e){ e.printStackTrace(); }
	}
}
