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
  val wikiFile = "./svwiki-20141031-pages-meta-current.xml";
  val indexDir = "./indexDir/";

  def main(args: Array[String]): Unit = {
    println("Starting indexing")
    index
  }

  def query(queryString: String): List[(Int, String, String)] = {
    println(s"Querying: $queryString")
    val analyzer = new StandardAnalyzer
    val queryParser: QueryParser = new QueryParser(Version.LUCENE_4_10_2, "text", analyzer)
    val query = queryParser.parse(queryString)
    val reader = DirectoryReader.open(FSDirectory.open(new File("indexDir")))
    val searcher = new IndexSearcher(reader)
    val collector = TopScoreDocCollector.create(10, true)
    collector.topDocs().scoreDocs.toList.map { x =>
      val d: Document = searcher.doc(x.doc)
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
          || page.isStub() || page.getTitle().startsWith("Användare:") || page.getTitle().startsWith("Användardiskussion:"))
          return;
        val doc = new Document;
        doc.add(new IntField("id", Integer.parseInt(page.getID), org.apache.lucene.document.Field.Store.YES))
        doc.add(new StringField("title", page.getTitle, org.apache.lucene.document.Field.Store.YES))
        doc.add(new TextField("text", page.getText, org.apache.lucene.document.Field.Store.YES))
        println(s"Adding article #${page.getID+1} title: ${page.getTitle}")
        writer.addDocument(doc)
      }
    })
    wxsp.parse
    writer.commit
    writer.close
  }
}
