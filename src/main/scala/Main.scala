object Main {
  def main(args: Array[String]): Unit = {
    val header = s"Reddit Post Parser\n${"=" * 40}"
    println(header)
    // 1. readSubscriptions ahora devuelve Option[List[Subscription]] [cite: 151, 158]
    FileIO.readSubscriptions() match {
      
      case Some(subscriptions) =>
        // CASO ÉXITO: Tenemos la lista de suscripciones
        println(s"Suscripciones cargadas: $subscriptions")

        // 2. Obtenemos todos los posts. 
        // Nota: postListFromSubList debe manejar internamente que cada sub devuelve un Option.
        val allPosts = FileIO.postListFromSubList(subscriptions)
        
        // 3. Tu lógica de filtrado del Ejercicio 3 (sigue igual, es pura) [cite: 131, 136]
        val filteredPosts = allPosts.filter(p => 
          p._2.trim.nonEmpty && p._3.trim.nonEmpty
        )

        // 4. Mostramos resultados
        println(s"Total de posts descargados: ${allPosts.length}")
        println(s"Posts válidos tras filtrar: ${filteredPosts.length}")

              // 5. Frecuencias de palabras por subreddit
        val freqsBySubreddit = TextProcessing.wordFrequenciesBySubreddit(filteredPosts)

        freqsBySubreddit.foreach { case (subreddit, freqs) =>
          println(s"\n${"=" * 60}")
          println(s"Top palabras en r/$subreddit")
          println("=" * 60)
          freqs.take(10).foreach { case (word, count) =>
            println(f"  $word%-30s $count")
          }
        }
      
        // Aquí podrías imprimir una muestra como hacías antes
        filteredPosts.take(5).foreach { case (sub, title, _, _, _) =>
          println(s"[$sub] $title")
        }

        // imprimimos el score total de cada subscription
        subscriptions.foreach { sub =>
          FileIO.postListFromSub(sub) match {
            case Some(posts) =>
              val scores    = FileIO.totalScore(posts)
              val wordList  = TextProcessing.wordFrequencies(posts)
              val firstPosts = posts.take(5)

              println(s"Subscription: ${sub._1}")
              println(s"Total de scores: $scores")
              println(s"Palabras mas frecuentes: $wordList")
              println(s"Posts: $firstPosts")

            case None =>
              println(s"No se pudieron cargar posts de: ${sub._1}")
          }
        }

      case None =>
        // CASO ERROR: El archivo no existía, estaba mal formado o faltaban campos [cite: 152, 154]
        println("ERROR: No se pudo cargar el archivo de suscripciones.")
        println("Asegurate de que 'subscriptions.json' exista y tenga el formato correcto.")
    }
  }
}
