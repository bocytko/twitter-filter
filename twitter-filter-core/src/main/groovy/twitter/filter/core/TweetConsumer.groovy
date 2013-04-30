package twitter.filter.core

import groovy.util.logging.Log4j
import twitter.filter.core.filters.FilterStrategy
import twitter.filter.core.model.IProgressReporter
import twitter.filter.core.model.ITweetStore
import twitter.filter.core.model.UrlCache
import twitter.filter.core.util.ParallelUrlResolver
import twitter.filter.core.util.UrlResolver;

@Log4j("log")
class TweetConsumer implements ITweetConsumer {
    // tweet and known links storage
    private ITweetStore tweetStore

    // url cache for caching resolved urls
    private UrlCache urlCache

    private int threads

    private def filterStrategies

    IProgressReporter progressReporter

    TweetConsumer withTweetStore(ITweetStore tweetStore) {
        this.tweetStore = tweetStore
        this
    }

    TweetConsumer withUrlCache(UrlCache urlCache) {
        this.urlCache = urlCache
        this
    }

    TweetConsumer withThreads(int threads) {
        this.threads = threads
        this
    }

    TweetConsumer withFilterStrategies(def filterStrategies) {
        this.filterStrategies = filterStrategies
        this
    }

    TweetConsumer withProgressReporter(IProgressReporter progressReporter) {
        this.progressReporter = progressReporter
        this
    }

    @Override
    def getTweets() {
        tweetStore.storedTweets
    }

    @Override
    int consume(def tweets) {
        populateUrlCache(tweets)
        filterTweets(tweets)
    }

    private void populateUrlCache(def tweets) {
        ParallelUrlResolver urlResolver = new ParallelUrlResolver(threads)
        urlCache.populateWithUrlsFromTweets(tweets, urlResolver)
    }

    private int filterTweets(def tweets) {
        int numberOfConsumedTweets = 0
        def before = System.currentTimeMillis()
        log.info "Filtering tweets..."

        progressReporter?.startIteration(tweets.size())

        tweets.each {
            progressReporter?.incrementProcessedElements()

            if (!containsTweet(it)) {
                tweetStore.storeTweet(it)
                numberOfConsumedTweets++
            }
        }

        def after = System.currentTimeMillis()
        log.info "Took ${after-before} ms"

        numberOfConsumedTweets
    }

    private def containsTweet(Tweet tweet) {
        for (FilterStrategy strategy : filterStrategies) {
            if (strategy.apply(tweet)) {
                return true
            }
        }

        false
    }

    @Override
    public getRelatedTweets(Tweet tweet) {
        []
    }
}
