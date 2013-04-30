package twitter.filter.core

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

import twitter.filter.core.filters.BlacklistedUserStrategy
import twitter.filter.core.filters.DuplicateTweetStrategy
import twitter.filter.core.filters.DuplicateUrlStrategy
import twitter.filter.core.filters.LevenshteinDistanceStrategy
import twitter.filter.core.model.IProgressReporter
import twitter.filter.core.model.IRelatedTweetsStore
import twitter.filter.core.model.ITweetStore
import twitter.filter.core.model.RedisProgressReporter
import twitter.filter.core.model.RedisRelatedTweetsStore
import twitter.filter.core.model.RedisTweetStore
import twitter.filter.core.model.RedisUrlCache
import twitter.filter.core.model.UrlCache
import redis.clients.jedis.Jedis

class RelatedTweetFilterTest {
    private static final int NUM_THREADS = 10

    private static Jedis jedis
    private static TweetFilter tweetFilter
    private static UrlCache urlCache

    def pagesToFetch = 2

    @BeforeClass
    static void before() {
        jedis = new Jedis("localhost")
        urlCache = new RedisUrlCache(jedis)
    }

    @AfterClass
    static void save() {
        jedis.save()
    }

    @Test
    void filterAndPrintHadoopTweets() {
        def hadoop = "#hadoop"

        filterAndPrintTweets(hadoop)
    }

    @Test
    void filterAndPrintHBaseTweets() {
        def hbase = "#hbase"

        filterAndPrintTweets(hbase)
    }

    private def filterAndPrintTweets(def query) {
        ITweetStore tweetStore = new RedisTweetStore(jedis, query, urlCache)
        IRelatedTweetsStore relatedTweetStore = new RedisRelatedTweetsStore(jedis, query)

        def filterStrategies = [
            new BlacklistedUserStrategy(['d8Pit', 'HBaselog', 'HatzolahNYC', 'ShomrimHatzny']),
        ]

        def duplicateStrategies = [
            new DuplicateUrlStrategy(tweetStore, urlCache),
            new DuplicateTweetStrategy(tweetStore)
        ]

        // TODO: mock
        IProgressReporter progressReporter = new RedisProgressReporter(jedis, query)

        RelatedTweetConsumer consumer = new RelatedTweetConsumer()
                                                    .withUrlCache(urlCache)
                                                    .withThreads(NUM_THREADS)
                                                    .withTweetStore(tweetStore)
                                                    .withRelatedTweetStore(relatedTweetStore)
                                                    .withFilterStrategies(filterStrategies)
                                                    .withDuplicateStrategies(duplicateStrategies)
                                                    .withProgressReporter(progressReporter)

        tweetFilter = new TweetFilter().withTweetConsumer(consumer)

        int newlyAddedTweets = tweetFilter.doFilter(query, pagesToFetch)
        tweetFilter.printLastNTweets(newlyAddedTweets)

        assert tweetStore.numberOfStoredTweets > 0
    }
}
