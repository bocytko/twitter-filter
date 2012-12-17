package twitter.filter

import twitter.filter.core.Tweet
import twitter.filter.core.TweetConsumer
import twitter.filter.core.TweetFilter
import twitter.filter.core.filters.BlacklistedUserStrategy
import twitter.filter.core.filters.DuplicateTweetStrategy
import twitter.filter.core.filters.DuplicateUrlStrategy
import twitter.filter.core.filters.LevenshteinDistanceStrategy
import twitter.filter.core.model.IProgressReporter
import twitter.filter.core.model.ITweetStore
import twitter.filter.core.model.RedisProgressReporter
import twitter.filter.core.model.RedisTweetStore
import twitter.filter.core.model.RedisUrlCache
import twitter.filter.core.model.UrlCache
import twitter.filter.core.view.TweetPrinter
import redis.clients.jedis.Jedis

class FilterService {

    ConfigurationService configurationService

    int filterTweets(Jedis jedis, def query) {
        UrlCache urlCache = new RedisUrlCache(jedis)
        ITweetStore tweetStore = new RedisTweetStore(jedis, query)

        def filterStrategies = [
            new BlacklistedUserStrategy(configurationService.getIgnoredUsers(jedis)),
            new DuplicateUrlStrategy(tweetStore, urlCache),
            new DuplicateTweetStrategy(tweetStore)
        ]

        IProgressReporter progressReporter = new RedisProgressReporter(jedis, query)

        TweetConsumer consumer = new TweetConsumer().withTweetStore(tweetStore)
                                                    .withUrlCache(urlCache)
                                                    .withThreads(configurationService.getNumberOfThreads())
                                                    .withFilterStrategies(filterStrategies)
                                                    .withProgressReporter(progressReporter)

        TweetFilter tweetFilter = new TweetFilter().withTweetConsumer(consumer)

        int newlyAddedTweets = tweetFilter.doFilter(query, configurationService.getPagesToFetch())

        newlyAddedTweets
    }

    def getLastNTweets(Jedis jedis, def query, int N) {
        ITweetStore tweetStore = new RedisTweetStore(jedis, query)

        TweetPrinter printer = new TweetPrinter()

        def tweets = tweetStore.getStoredTweets().reverse()
        if (tweets.size() > N) {
            tweets = tweets[0..N-1]
        }

        tweets.each {
            printer.convertUrlsToHtmlLinks(it)
        }

        tweets
    }

    def getProgress(Jedis jedis, def query) {
        // TODO move logic to ProgressReporter
        def progress = jedis.get("progress:${query}")

        progress != null ? progress : ""
    }

    def getDatastoreStats(Jedis jedis, def hashtags) {
        def stats = [:]

        hashtags.each {
            ITweetStore tweetStore = new RedisTweetStore(jedis, it)

            stats[it] = [
                numTweets: tweetStore.getNumberOfStoredTweets(),
                numUrls: tweetStore.getNumberOfKnownUrls()
            ]
        }

        stats
    }

    void removeTweetsFromIgnoredUsers(Jedis jedis, def hashtags, def ignoredUsers) {
        removeTweets(jedis, hashtags, { Tweet t -> ignoredUsers.contains(t.from_user) })
    }

    void removeTweets(Jedis jedis, def hashtags, Closure c) {
        hashtags.each {
            ITweetStore tweetStore = new RedisTweetStore(jedis, it)

            tweetStore.removeTweets(c)
        }
    }

    void clearStoredTweetsAndUrlCache(Jedis jedis, def query) {
        ITweetStore tweetStore = new RedisTweetStore(jedis, query)

        tweetStore.clear()

        // TODO: clear all cached urls from RedisUrlCache
    }
}
