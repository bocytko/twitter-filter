package twitter.filter.core

import java.util.Map

import twitter.filter.core.filters.DuplicateStrategy
import twitter.filter.core.filters.FilterStrategy
import twitter.filter.core.model.ITweetStore
import twitter.filter.core.model.ListTweetStore

class RelatedTweetConsumer {
    ITweetStore tweetStore

    // TODO: add abstraction
    Map<Tweet, List<Tweet> > relatedTweets = [:]

    List<FilterStrategy> filterStrategies = []
    List<DuplicateStrategy> duplicateStrategies = []

    RelatedTweetConsumer withTweetStore(def tweetStore) {
        this.tweetStore = tweetStore
        this
    }

    RelatedTweetConsumer withFilterStrategies(def filterStrategies) {
        this.filterStrategies = filterStrategies
        this
    }

    RelatedTweetConsumer withDuplicateStrategies(def duplicateStrategies) {
        this.duplicateStrategies = duplicateStrategies
        this
    }

    def getTweets() {
        tweetStore.storedTweets
    }

    def getRelatedTweets(Tweet t) {
        def related = relatedTweets[t]

        related != null ? related : []
    }

    int consume(def tweetsToConsume) {
        int consumed = 0
        def tweets = []
        tweets.addAll(tweetsToConsume)

        filterUsingStrategies(tweets)

        tweets.each {
            Tweet duplicate = findDuplicate(it)
            if (duplicate == null) {
                tweetStore.storeTweet(it)
                consumed++
            } else {
                saveAsDuplicateOf(it, duplicate)
            }
        }

        consumed
    }

    private void filterUsingStrategies(Collection tweets) {
        filterStrategies.each { FilterStrategy fs ->
            tweets.removeAll { fs.apply(it) }
        }
    }

    private Tweet findDuplicate(Tweet t) {
        for (DuplicateStrategy ds : duplicateStrategies) {
            def duplicate = ds.apply(t)
            if (duplicate != null) {
                return duplicate
            }
        }

        null
    }

    private void saveAsDuplicateOf(Tweet t, Tweet duplicate) {
        if (relatedTweets.containsKey(duplicate)) {
            relatedTweets[duplicate].add(t)
        } else {
            relatedTweets[duplicate] = [t]
        }
    }
}
