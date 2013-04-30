package twitter.filter.core.model

import twitter.filter.core.Tweet

interface ITweetStore {
    void storeTweet(Tweet tweet)

    def getStoredTweets()

    Tweet getTweetForUrl(def url)

    def getNumberOfStoredTweets()

    def removeTweets(Closure condition)

    void clear()
}
