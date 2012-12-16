package twitter.filter.core.model

import twitter.filter.core.Tweet

interface ITweetStore {
    void storeTweet(Tweet tweet)

    def getStoredTweets()

    def addToKnownUrls(def urlChain)

    def getKnownUrls()

    def getNumberOfStoredTweets()

    def getNumberOfKnownUrls()

    def removeTweets(Closure condition)

    void clear()
}
