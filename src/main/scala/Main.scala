object Main {
  def main(args: Array[String]): Unit = {
    val header = s"Reddit Post Parser\n${"=" * 40}"

    val subscriptions: List[Types.Subscription] = FileIO.readSubscriptions("./subscriptions.json")
    println(subscriptions)

    val allPosts: List[Types.Post] = FileIO.postListFromSubList(subscriptions)
    val filteredPosts = allPosts.filter(post => post._2.trim.nonEmpty && post._3.trim.nonEmpty)

    allPosts.foreach { case (subreddit, title, selftext, date) =>
      println(s"[$date] ($subreddit) $title")
      if (selftext.nonEmpty) println(s"  $selftext")}
  }
}
