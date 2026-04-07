import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


object Formatters {

  // Pure function to format posts from a subscription
  def formatSubscription(url: String, posts: String): String = {
    val header = s"\n${"=" * 80}\nPosts from: $url \n${"=" * 80}"
    val formattedPosts = posts.take(80)
    header + "\n" + formattedPosts
  }

  // Convierte un timestamp UTC (Unix epoch en segundos) a un String legible
  def formatDateFromUTC(utcSeconds: Long): String = {
    val instant = Instant.ofEpochSecond(utcSeconds)
    val formatter = DateTimeFormatter
      .ofPattern("yyyy-MM-dd HH:mm:ss")
      .withZone(ZoneOffset.UTC)
    formatter.format(instant)
  }
}

