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
        it
        val freqsBySubreddit = TextProcessing.wordFrequenciesBySubreddit(filteredPosts)

        freqsBySubreddit.foreach { case (subreddit, freqs) =>
          println(s"\n${"=" * 60}")
          println(s"Top palabras en r/$subreddit")
          println("=" * 60)
          freqs.take(10).foreach { case (word, count) =>
            println(f"  $word%-30s $count")
          }
        }

        subscriptions.foreach { sub =>
          FileIO.postListFromSub(sub) match {
            case Some(posts) =>
              val scores    = FileIO.totalScore(posts)
              val wordList  = TextProcessing.wordFrequencies(posts)
              val firstPosts = posts.take(5)

              println(s"Subscription: ${sub._1}")
              println(s"Total de scores: $scores")
              println(s"Palabras mas frecuentes: $wordList")
              println("Posts de muestra:")
              firstPosts.foreach { case (subreddit, title, selftext, date, score) =>
                println(s"  -> [$date] $title url:${sub._2}")
              }
            case None =>
              println(s"No se pudieron cargar posts de: ${sub._1}")
          }
        }

      case None =>
        println("ERROR: No se pudo cargar el archivo de suscripciones.")
        println("Asegurate de que 'subscriptions.json' exista y tenga el formato correcto.")
    }
  }
}
