package twitter.filter.core.model

import twitter.filter.core.Tweet

class ListTweetStore implements ITweetStore {
    private def storedTweets = []
    private def knownUrls = []

    @Override
    public void storeTweet(Tweet tweet) {
        storedTweets << tweet
    }

    @Override
    def getStoredTweets() {
        storedTweets
    }

    @Override
    def removeTweets(Closure condition) {
        storedTweets.removeAll(condition)
    }

    @Override
    def addToKnownUrls(def urlChain) {
        knownUrls = knownUrls + urlChain

        knownUrls
    }

    @Override
    def getKnownUrls() {
        knownUrls
    }

    @Override
    def getNumberOfStoredTweets() {
        storedTweets.size()
    }

    @Override
    def getNumberOfKnownUrls() {
        knownUrls.size()
    }

    @Override
    void clear() {
        storedTweets = []
    }
}
