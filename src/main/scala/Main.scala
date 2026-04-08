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

        // Aquí podrías imprimir una muestra como hacías antes
        filteredPosts.take(5).foreach { case (sub, title, _, _) =>
          println(s"[$sub] $title")
        }

      case None =>
        // CASO ERROR: El archivo no existía, estaba mal formado o faltaban campos [cite: 152, 154]
        println("ERROR: No se pudo cargar el archivo de suscripciones.")
        println("Asegurate de que 'subscriptions.json' exista y tenga el formato correcto.")
    }
  }
}
