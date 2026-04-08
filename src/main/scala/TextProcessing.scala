object TextProcessing {

  val stopwords: Set[String] = Set(
    "the", "about", "above", "after", "again", "against", "all", "am", "an",
    "and", "any", "are", "aren't", "as", "at", "be", "because", "been",
    "before", "being", "below", "between", "both", "but", "by", "can't",
    "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't",
    "doing", "don't", "down", "during", "each", "few", "for", "from", "further",
    "had", "hadn't", "has", "hasn't", "have", "haven't", "having", "he", "he'd",
    "he'll", "he's", "her", "here", "here's", "hers", "herself", "him",
    "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if",
    "in", "into", "is", "isn't", "it", "it's", "its", "itself", "let's", "me",
    "more", "most", "mustn't", "my", "myself", "no", "nor", "not", "of", "off",
    "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves",
    "out", "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's",
    "should", "shouldn't", "so", "some", "such", "than", "that", "that's",
    "their", "theirs", "them", "themselves", "then", "there", "there's",
    "these", "they", "they'd", "they'll", "re", "they've", "this", "those",
    "through", "to", "too", "under", "until", "up", "very", "was", "wasn't",
    "we", "we'd", "we'll", "we're", "we've", "were", "weren't", "what",
    "what's", "when", "when's", "where", "where's", "which", "while", "who",
    "who's", "whom", "why", "why's", "with", "won't", "would",
    "wouldn't", "you", "you'd", "you'll", "you're", "you've", "your", "yours",
    "yourself", "yourselves"
  )

  // Extrae palabras de un texto, divide por espacios y puntuación
  def extractWords(text: String): List[String] =
    text.split("[^a-zA-Z']+").toList.filter(_.nonEmpty)

  // Filtra palabras que empiezan con mayúscula y no son stopwords
  def isRelevant(word: String): Boolean =
    word.nonEmpty &&
    word.head.isUpper &&
    !stopwords.contains(word.toLowerCase)

  // Cuenta frecuencias de palabras relevantes en una lista de posts del mismo subreddit
  // Devuelve Map[palabra -> frecuencia], ordenado por frecuencia descendente
  def wordFrequencies(posts: List[Types.Post]): List[(String, Int)] = {
    val allWords = posts.flatMap { case (_, title, selftext, _, _) =>
      extractWords(title + " " + selftext)
    }

    val relevantWords = allWords.filter(isRelevant)

    // groupBy agrupa todas las ocurrencias de la misma palabra
    val grouped: Map[String, List[String]] = relevantWords.groupBy(identity)

    // Convertimos a (palabra, count) y ordenamos por frecuencia descendente
    grouped
      .map { case (word, occurrences) => (word, occurrences.length) }
      .toList
      .sortBy { case (_, count) => -count }
  }

  // Calcula frecuencias por subreddit a partir de la lista completa de posts
  def wordFrequenciesBySubreddit(posts: List[Types.Post]): Map[String, List[(String, Int)]] = {
    posts
      .groupBy(_._1) // agrupa por nombre de subreddit (primer campo del Post)
      .map { case (subreddit, subredditPosts) =>
        subreddit -> wordFrequencies(subredditPosts)
      }
  }
}