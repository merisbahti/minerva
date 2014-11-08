import jules.Indexer

object Main {

  def main(args: Array[String]): Unit = {
    def s(q: String) = Index.query(q).foreach( x => println(s"Title: ${x._2}\nText:\n${x._3}"))
    //def s(q: String) = println(q)
    val i: jules.Indexer = new jules.Indexer()
    while (true) (i.query(Console.readLine()))
  }

}
