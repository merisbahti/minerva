
object Main {

  def main(args: Array[String]) = {
    println("hej!")
    allPerms("abcdef", 2).foreach{ println(_) }
  }

  def allPerms(letters: String, length: Int) = {
    letters.toSet[Char].subsets.map(_.toList).filter( _.length == length )
  }

}
