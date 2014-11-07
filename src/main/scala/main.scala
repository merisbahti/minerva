import jules.Indexer

object Main {

  def main(args: Array[String]): Unit = {
    Index.query("kung").foreach( x => println(s"Title: ${x._2}\nText:\n${x._3}"))
  }

}
