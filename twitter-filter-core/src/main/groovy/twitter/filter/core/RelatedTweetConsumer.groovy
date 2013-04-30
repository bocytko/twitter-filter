package twitter.filter.core

import java.util.Map

import twitter.filter.core.filters.DuplicateStrategy
import twitter.filter.core.filters.FilterStrategy
import twitter.filter.core.model.IProgressReporter
import twitter.filter.core.model.IRelatedTweetsStore
import twitter.filter.core.model.ITweetStore
import twitter.filter.core.model.ListTweetStore
import twitter.filter.core.model.UrlCache
import twitter.filter.core.util.ParallelUrlResolver


/**
 * Consumes tweets:
 * -# populates url cache
 * -# applies filtering strategies
 * -# applies duplicate strategies, each duplicate is stored as related tweet
 * -# stores unique tweets using @{link {@link ITweetStore}.
 *
 * URLs are cached using @{link UrlCache} and resolved using @{link ParallelUrlResolver}.
 * Tweets and URL -> tweet relation stored using @{link ITweetStore}.
 * Tweet -> related tweet relation stored using {@link IRelatedTweetsStore}.
 */
class RelatedTweetConsumer implements ITweetConsumer {
    UrlCache urlCache
    int threads = 1
    ParallelUrlResolver urlResolver

    ITweetStore tweetStore
    IRelatedTweetsStore relatedTweetStore
    IProgressReporter progressReporter

    List<FilterStrategy> filterStrategies = []
    List<DuplicateStrategy> duplicateStrategies = []

    RelatedTweetConsumer withUrlCache(def urlCache) {
        this.urlCache = urlCache
        this
    }

    RelatedTweetConsumer withThreads(int threads) {
        this.threads = threads
        this.urlResolver = new ParallelUrlResolver(this.threads)
        this
    }

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

    RelatedTweetConsumer withProgressReporter(IProgressReporter reporter) {
        this.progressReporter = reporter
        this
    }

    // TODO: build()

    @Override
    def getTweets() {
        tweetStore.storedTweets
    }

    def getRelatedTweets(Tweet t) {
        relatedTweetStore.getRelatedTweets(t)
    }

    @Override
    int consume(def tweetsToConsume) {
        int numberOfConsumedTweets = 0
        def tweets = []
        tweets.addAll(tweetsToConsume)

        urlCache?.populateWithUrlsFromTweets(tweetsToConsume, urlResolver)

        filterUsingStrategies(tweets)

        progressReporter?.startIteration(tweetsToConsume.size())
        tweets.each {
            progressReporter?.incrementProcessedElements()

            Tweet original = getOriginalTweetFor(it)
            if (original == null) {
                tweetStore.storeTweet(it)
                numberOfConsumedTweets++
            } else {
                saveTweetAsDuplicateOf(original, it)
            }
        }

        numberOfConsumedTweets
    }

    private void filterUsingStrategies(Collection tweets) {
        filterStrategies.each { FilterStrategy fs -> tweets.removeAll { fs.apply(it) } }
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
        if (!original.equals(duplicate)) {
            relatedTweetStore.add(original, duplicate)
        }
    }
}
