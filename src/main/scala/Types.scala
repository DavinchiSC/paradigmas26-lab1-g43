object Types {
    type Subscription = (String, String) // (subredditName, url)

    type Post = (String, String, String, String, Int) // (subreddit, title, selftext, date, score)
}

