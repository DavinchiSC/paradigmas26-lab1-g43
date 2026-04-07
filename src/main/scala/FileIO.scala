import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._
import Types.{Subscription,Post} // Importamos tu tipo



object FileIO {

  // Esto es obligatorio para que 'extract' sepa qué hacer
  implicit val formats: DefaultFormats.type = DefaultFormats

  // Pure function to read subscriptions from a JSON file
  def readSubscriptions(filename: String): List[Types.Subscription] = {
    val source = Source.fromFile(filename)
    try {
      val content = source.mkString
      // 1. Convertimos el String a JValue
      val jValue = parse(content)

      // 2. Mapeamos cada elemento de la lista al tipo Subscription (tupla)
      jValue.children.map { item =>
        val name = (item \ "name").extract[String]
        val url = (item \ "url").extract[String]
        (name, url) // Retornamos la tupla
      }
    } finally {
      source.close() // Requisito: No generar resource leaks
    }
  }

  // Pure function to download JSON feed from a URL
  def downloadFeed(url: String): String = {
    val source = Source.fromURL(url)
    try {
      source.mkString
    } finally {
      source.close() // También cerramos aquí para evitar fugas
    }
  }

  def postListFromSub(sub: Types.Subscription): List[Types.Post] = {
    val subredditName = sub._1
    val url = sub._2

    val source = Source.fromURL(url)
    try {
      val content = source.mkString
      val jValue = parse(content)

      val children = (jValue \ "data" \ "children").children
      
      // Mapeamos cada elemento al post
      children.map { child =>
        val data = child \ "data"

        val title     = (data \ "title").extract[String]
        val selftext  = (data \ "selftext").extract[String]
        val createdUtc = (data \ "created_utc").extract[Double].toLong
        val date      = Formatters.formatDateFromUTC(createdUtc)

        (subredditName, title, selftext, date) // Types.Post
    }  
    } finally {
    source.close()
    }
  }

  def postListFromSubList(subs: List[Types.Subscription]): List[Types.Post] = {
    subs.flatMap(sub => postListFromSub(sub))
  }
}
