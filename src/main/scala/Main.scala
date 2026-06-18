object Main {
  def main(args: Array[String]): Unit = {
    val header = s"Reddit Post Parser\n${"=" * 40}"
    println(header)

    FileIO.readSubscriptions() match {

      case Some(subscriptions) =>
        println(s"Suscripciones cargadas: $subscriptions")

        val allPosts = FileIO.postListFromSubList(subscriptions)

        val filteredPosts = allPosts.filter(p =>
          p._2.trim.nonEmpty && p._3.trim.nonEmpty
        )

        println(s"Total de posts descargados: ${allPosts.length}")
        println(s"Posts válidos tras filtrar: ${filteredPosts.length}")

        // Pre-calculamos todo una sola vez, sin volver a pegarle a la red
        val postsBySubreddit  = filteredPosts.groupBy(_._1)
        val freqsBySubreddit  = TextProcessing.wordFrequenciesBySubreddit(filteredPosts)

        subscriptions.foreach { case (name, url) =>
          val postsForSub  = postsBySubreddit.getOrElse(name, Nil)
          val totalScore   = FileIO.totalScore(postsForSub)
          val topWords     = freqsBySubreddit.getOrElse(name, Nil).take(10)
          val samplePosts  = postsForSub.take(5)

          println(s"\n${"=" * 60}")
          println(s"Subscription: $name")
          println(s"URL: $url")
          println("=" * 60)

          println(s"Total de scores: $totalScore")

          println("Palabras más frecuentes:")
          topWords.foreach { case (word, count) =>
            println(f"  $word%-30s $count")
          }

          println("Posts de muestra (5 primeros):")
          samplePosts.foreach { case (_, title, _, date, _) =>
            println(s"  -> [$date] $title  url:$url")
          }
        }

      case None =>
        println("ERROR: No se pudo cargar el archivo de suscripciones.")
        println("Asegurate de que 'subscriptions.json' exista y tenga el formato correcto.")
    }
  }
}

