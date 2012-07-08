package twitter.filter.core

import groovy.util.logging.Log4j
import twitter.filter.core.filters.FilterStrategy
import twitter.filter.core.model.IProgressReporter
import twitter.filter.core.model.ITweetStore
import twitter.filter.core.model.UrlCache
import twitter.filter.core.util.ParallelUrlResolver

@Log4j("log")
class TweetConsumer {
    // tweet and known links storage
    private ITweetStore tweetStore

    // url cache for caching resolved urls
    private UrlCache urlCache

    private int threads

    private def filterStrategies

    private IProgressReporter progressReporter

    def withTweetStore(ITweetStore tweetStore) {
        this.tweetStore = tweetStore
        this
    }

    def withUrlCache(UrlCache urlCache) {
        this.urlCache = urlCache
        this
    }

    def withThreads(int threads) {
        this.threads = threads
        this
    }

    def withFilterStrategies(def filterStrategies) {
        this.filterStrategies = filterStrategies
        this
    }

    def withProgressReporter(IProgressReporter progressReporter) {
        this.progressReporter = progressReporter
        this
    }

    def getTweets() {
        tweetStore.storedTweets
    }

    /**
     * Adds the given tweets to the collection.
     * May be called incrementally.
     *
     * @param tweets
     * @return number of tweets, that have been added to the internal collection
     */
    int consume(def tweets) {
        populateUrlCache(tweets)
        filterTweets(tweets)
    }

    /**
     * Retrieves all URLs from the given tweets
     * and resolves its currently unknown urls using
     * {@link ParallelUrlResolver}.
     *
     * The resolved urls (url chain) are saved
     * as (url: url chain) into the urlCache.
     *
     * @param tweets
     */
    private void populateUrlCache(def tweets) {
        def before = System.currentTimeMillis()
        log.info "Getting all URLs..."
        def urls = []
        tweets.each { urls = urls + it.urls }
        log.info "Got a total of ${urls.size()} URLs..."

        log.info "Filtering already known URLs..."
        urls = urlCache.filterKnownUrls(urls)

        log.info "Got ${urls.size()} URLs. Removing duplicate urls..."
        urls = urls.sort().unique()

        log.info "Resolving ${urls.size()} unknown URLs..."
        ParallelUrlResolver urlResolver = new ParallelUrlResolver(threads)
        def resolvedUrls = urlResolver.resolveUrls(urls)

        urlCache.putAll(urls, resolvedUrls)

        def after = System.currentTimeMillis()
        log.info "Took ${after-before} ms"
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
}
