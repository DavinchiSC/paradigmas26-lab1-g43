import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._
import Types.{Subscription,Post}
import scala.util.{Using,Try}


object FileIO {

  implicit val formats: DefaultFormats.type = DefaultFormats

  def readSubscriptions(): Option[List[Types.Subscription]] = {
  val filename = "./subscriptions.json"

  Using(Source.fromFile(filename)) { source =>
      val content = source.mkString
      val jValue = parse(content)

      jValue.children.flatMap { item =>
        Try {
          val name = (item \ "name").extract[String]
          val url = (item \ "url").extract[String]
          (name, url)
        }.toOption 
      }
    }.toOption 
  }

  def downloadFeed(url: String): Option[String] = {
    Using(Source.fromURL(url)) { source =>
      source.mkString
    }.toOption
  }

  def postListFromSub(sub: Types.Subscription): Option[List[Types.Post]] = {
    val subredditName = sub._1
    val url = sub._2
    downloadFeed(url).flatMap { content =>
      Try {
        val jValue = parse(content)
        val children = (jValue \ "data" \ "children").children
        
        children.flatMap { child =>
          Try {
            val data = child \ "data"
            val title     = (data \ "title").extract[String]
            val selftext  = (data \ "selftext").extract[String]
            val createdUtc = (data \ "created_utc").extract[Double].toLong
            val date      = Formatters.formatDateFromUTC(createdUtc)
            val score = (data \ "score").extract[Int]

            (subredditName, title, selftext, date, score)
          }.toOption
        }
      }.toOption
    }
  }
  def postListFromSubList(subs: List[Types.Subscription]): List[Types.Post] = {
   subs.flatMap(sub => postListFromSub(sub).getOrElse(Nil))
  }

  def totalScore(posts: List[Types.Post]): Int = {
    posts.foldLeft(0)((acc, post) => acc + post._5)
  }
}