package twitter.filter.core

import org.junit.Before
import org.junit.Test

import twitter.filter.core.filters.BlacklistedUserStrategy
import twitter.filter.core.filters.DuplicateUrlStrategy
import twitter.filter.core.filters.LevenshteinDistanceStrategy
import twitter.filter.core.model.ListTweetStore
import twitter.filter.core.model.RedisUrlCache
import twitter.filter.core.view.TweetPrinter
import redis.clients.jedis.Jedis

class TweetFetcherTest {
    @Before
    void doMockTweetFetcher() {
        TweetFetcher.metaClass.getJsonPayLoad = { getFileFromResource("tweets.json").text }
    }

    private def getFileFromResource(String resource) {
        new File(ClassLoader.getSystemResource(resource).toURI())
    }

    @Test
    void canParseTweets() {
        // given
        def query = "#hadoop"
        def page = 1
        def results = 100

        TweetFetcher fetcher = new TweetFetcher(query, page, results)

        // when
        def tweets = fetcher.getTweets()

        // then
        assert 90 == tweets.size()

        compareWithExpectedTweets(tweets, "nonfiltered.expected")

        printTweets(tweets)
    }

    @Test
    void canParseAndFilterTweets() {
        // given
        def query = "#hadoop"
        def page = 1
        def results = 100

        TweetFetcher fetcher = new TweetFetcher(query, page, results)
        def tweets = fetcher.getTweets()

        def tweetStore = new ListTweetStore()
        def jedis = new Jedis("localhost")
        jedis.select(1)

        def urlCache = new RedisUrlCache(jedis)
        def threads = 10

        // when
        def filterStrategies = [
            new BlacklistedUserStrategy(['d8Pit']),
            new DuplicateUrlStrategy(tweetStore, urlCache),
            // new DuplicateTweetStrategy(tweetStore),
            new LevenshteinDistanceStrategy(tweetStore)
        ]

        TweetConsumer consumer = new TweetConsumer()
                .withTweetStore(tweetStore)
                .withUrlCache(urlCache)
                .withThreads(threads)
                .withFilterStrategies(filterStrategies)

        consumer.consume(tweets)

        // then
        def filteredTweets = consumer.getTweets()

        jedis.flushDB()

        assert 90 == tweets.size()
        assert 57 == filteredTweets.size()

        compareWithExpectedTweets(filteredTweets, "filtered.expected")
        printTweets(filteredTweets)
    }

    private def printTweets(def filteredTweets) {
        def printer = new TweetPrinter()

        filteredTweets.each { printer.printPlainText(it) }
        filteredTweets.each { printer.printHtml(it) }
    }

    private void compareWithExpectedTweets(def tweets, String resource) {
        def printer = new TweetPrinter()

        def tweetsAsList = []
        tweetsAsList = tweets.collect { printer.getPlainText(it) }

        File expectedTweetsFile = getFileFromResource(resource)
        def expectedTweets = []
        expectedTweetsFile.eachLine { expectedTweets << it }

        assertArraysEqual(expectedTweets, tweetsAsList)
    }

    private void assertArraysEqual(def expected, def actual) {
        assert expected.size() == actual.size()

        for (int i = 0; i < expected.size(); i++ ) {
            assert expected[i] == actual[i]
        }

        assert expected == actual
    }
}
