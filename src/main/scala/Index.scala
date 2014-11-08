import java.io.File

import edu.jhu.nlp.wikipedia.{WikiPage, PageCallbackHandler, WikiXMLParser, WikiXMLParserFactory}
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document
import org.apache.lucene.document._
import org.apache.lucene.index.{IndexWriter, IndexWriterConfig, IndexReader, DirectoryReader}
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{TopScoreDocCollector, IndexSearcher}
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.Version

/**
 * Created by meris on 11/7/14.
 */
object Index {
  //val wikiFile = "./svwiki-20141031-pages-meta-current.xml";
  //val indexDir = "./indexDir/";
  val wikiFile = "./sewiki.xml";
  val indexDir = "./indexDir/";

  def main(args: Array[String]): Unit = {
    println("Starting indexing")
    println("are you really sure?")
    val i: jules.Indexer = new jules.Indexer()
    i.index()
    //index
  }

  def query(queryString: String): List[(Int, String, String)] = {
    println(s"Querying: $queryString")
    val analyzer = new StandardAnalyzer
    println("line 2")
    val queryParser: QueryParser = new QueryParser(Version.LUCENE_4_10_2, "text", analyzer)
    println("line 2")
    val query = queryParser.parse(queryString)
    println("line 2")
    val reader = DirectoryReader.open(FSDirectory.open(new File("indexDir")))
    println("line 2")
    val searcher = new IndexSearcher(reader)
    println("line 2")
    val collector = TopScoreDocCollector.create(10, true)
    println("line 2")
    collector.topDocs().scoreDocs.toList.map { x =>
      val d: Document = searcher.doc(x.doc)
      println(d.getField("title").stringValue())
      (d.getField("id").numericValue().intValue(), d.getField("title").stringValue(), d.getField("text").stringValue())
    }
  }

  def index = {
    val analyzer = new StandardAnalyzer
    val iwc = new IndexWriterConfig(Version.LATEST, analyzer)
    val wxsp: WikiXMLParser = WikiXMLParserFactory.getSAXParser(wikiFile)
    val writer = new IndexWriter(FSDirectory.open(new File(indexDir)), iwc);
    wxsp.setPageCallback(new PageCallbackHandler {
      override def process(page: WikiPage): Unit = {
        if (page.isDisambiguationPage() || page.isRedirect() || page.isSpecialPage()
          || page.isStub() || page.getTitle().startsWith("Användare:") || page.getTitle().startsWith("Användardiskussion:")) {
          println("returning!")
          return;
        }
        val doc = new Document;
        doc.add(new IntField("id", Integer.parseInt(page.getID()), Field.Store.YES));
        doc.add(new TextField("title", page.getTitle(), Field.Store.YES));
        doc.add(new TextField("text", page.getText(), Field.Store.YES));
        print(s"Adding article #${page.getID+1} title: ${page.getTitle}")
        writer.addDocument(doc)
      }
    })
    wxsp.parse
    writer.commit
    writer.close
  }
}
/*
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
                    // We don't want to store unnecessary pages.
                    if (page.isDisambiguationPage() || page.isRedirect() || page.isSpecialPage()
                        || page.isStub() || page.getTitle().startsWith("Användare:") || page.getTitle().startsWith("Användardiskussion:")) {
                        return;
                    }
                    Document doc = new Document();
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
            wxsp.parse();
            writer.commit();
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
*
* */
