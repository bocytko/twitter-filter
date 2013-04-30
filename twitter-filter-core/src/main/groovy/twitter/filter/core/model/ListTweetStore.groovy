package twitter.filter.core.model

import twitter.filter.core.Tweet

class ListTweetStore implements ITweetStore {
    private def storedTweets = []
    private def urlToTweet = [:]

    @Override
    public void storeTweet(Tweet tweet) {
        storedTweets << tweet

        tweet.urls.each { urlToTweet[it] = tweet }
    }

    @Override
    def getStoredTweets() {
        storedTweets
    }

    @Override
    Tweet getTweetForUrl(def url) {
        urlToTweet[url]
    }

    @Override
    def removeTweets(Closure condition) {
        storedTweets.removeAll(condition)
    }

    @Override
    def getNumberOfStoredTweets() {
        storedTweets.size()
    }

    @Override
    void clear() {
        storedTweets = []
    }
}
