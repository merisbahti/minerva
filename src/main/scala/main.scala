package jules;
import edu.jhu.nlp.wikipedia._;

object Main {

  def main(args: Array[String]): Unit = {
    val ind: Indexer = new Indexer
    ind.query("kung");
  }

}
