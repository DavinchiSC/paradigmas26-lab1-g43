import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._
import Types.{Subscription,Post} // Importamos tu tipo
import scala.util.Using


object FileIO {

  // Esto es obligatorio para que 'extract' sepa qué hacer
  implicit val formats: DefaultFormats.type = DefaultFormats

  // Pure function to read subscriptions from a JSON file
  def readSubscriptions(): Option[List[Types.Subscription]] = {
  val filename = "./subscriptions.json"

  // 1. Using abre el archivo y se compromete a cerrarlo automáticamente al final
  Using(Source.fromFile(filename)) { source =>
    
    val content = source.mkString
    val jValue = parse(content)

    // 2. Mapeamos y retornamos la lista directamente
    jValue.children.map { item =>
      val name = (item \ "name").extract[String]
      val url = (item \ "url").extract[String]
      (name, url)
    }
  }.toOption // Captura TODAS las excepciones del bloque y devuelve None si algo falló
}

  // Pure function to download JSON feed from a URL
  def downloadFeed(url: String): Option[String] = {
  // Using encapsula la apertura y el cierre automático del recurso
  Using(Source.fromURL(url)) { source =>
    source.mkString
  }.toOption // .toOption convierte el éxito en Some(texto) y cualquier falla en None
}

  def postListFromSub(sub: Types.Subscription): Option[List[Types.Post]] = {
    val subredditName = sub._1
    val url = sub._2
    Using(Source.fromURL(url)) {source =>
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
    }.toOption
  }
  def postListFromSubList(subs: List[Types.Subscription]): List[Types.Post] = {
   // getOrElse(Nil) dice: "Si es Some(lista), dame la lista. Si es None, dame una lista vacía (Nil)"
   subs.flatMap(sub => postListFromSub(sub).getOrElse(Nil))
  }
}