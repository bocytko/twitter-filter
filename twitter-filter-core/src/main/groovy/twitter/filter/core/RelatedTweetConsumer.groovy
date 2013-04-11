package twitter.filter.core

import java.util.Map

import twitter.filter.core.filters.DuplicateStrategy
import twitter.filter.core.filters.FilterStrategy
import twitter.filter.core.model.IRelatedTweetsStore;
import twitter.filter.core.model.ITweetStore
import twitter.filter.core.model.ListTweetStore

class RelatedTweetConsumer {
    ITweetStore tweetStore
    IRelatedTweetsStore relatedTweetStore

    List<FilterStrategy> filterStrategies = []
    List<DuplicateStrategy> duplicateStrategies = []

    RelatedTweetConsumer withRelatedTweetStore(def relatedTweetStore) {
        this.relatedTweetStore = relatedTweetStore
        this
    }

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
        relatedTweetStore.getRelatedTweets(t)
    }

    int consume(def tweetsToConsume) {
        int consumed = 0
        def tweets = []
        tweets.addAll(tweetsToConsume)

        filterUsingStrategies(tweets)

        tweets.each {
            Tweet original = getOriginalTweetFor(it)
            if (original == null) {
                tweetStore.storeTweet(it)
                consumed++
            } else {
                saveTweetAsDuplicateOf(original, it)
            }
        }

        consumed
    }

    private void filterUsingStrategies(Collection tweets) {
        filterStrategies.each { FilterStrategy fs ->
            tweets.removeAll { fs.apply(it) }
        }
    }

    private Tweet getOriginalTweetFor(Tweet t) {
        for (DuplicateStrategy ds : duplicateStrategies) {
            def originalTweet = ds.apply(t)
            if (originalTweet != null) {
                return originalTweet
            }
        }

        null
    }

    private void saveTweetAsDuplicateOf(Tweet original, Tweet duplicate) {
        relatedTweetStore.add(original, duplicate)
    }
}
