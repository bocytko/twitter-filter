package twitter.filter.core.model

import groovy.json.JsonSlurper

import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import twitter.filter.core.TweetFactory
import redis.clients.jedis.Jedis

class RedisTweetStoreTest {
    private static String HOST = "localhost"
    private static String TEST_QUERY = "#hadoop"
    private static String OTHER_QUERY = "#hive"

    private static Jedis jedis

    private RedisTweetStore tweetStore

    @BeforeClass
    static void initializeRedis() {
        jedis = new Jedis(HOST)

        // select test database
        jedis.select(1)
    }

    @AfterClass
    static void flushRedis() {
        jedis.flushDB()
    }

    @Before
    void before() {
        tweetStore = new RedisTweetStore(jedis, TEST_QUERY)
        assert tweetStore.getStoredTweets() == []
    }

    @After
    void after() {
        tweetStore.clear()
        assert tweetStore.getStoredTweets() == []
    }

    @Test
    void shouldRetrieveZeroTweetsWhenRedisDBEmpty() {
        assert tweetStore.getStoredTweets() == []
    }

    @Test
    void shouldRetrieveTweetsSavedInRedis() {
        // given
        def twitterJsonTweets = getTweetsFromTwitterJsonText()
        def tweetText = [
            twitterJsonTweets.results[15],
            twitterJsonTweets.results[16]
        ]

        def tweets = tweetText.collect { TweetFactory.createFromTwitterJson(it) }

        // when
        tweets.each { tweetStore.storeTweet(it) }

        // then
        def storedTweets = tweetStore.getStoredTweets()

        assert tweets == storedTweets
        assert 2 == tweetStore.getNumberOfStoredTweets()
    }

    @Test
    void shouldRetrieveKnownUrlsSavedInRedis() {
        // given
        def urlChain = ["A", "B", "C"]

        // when
        def knownUrls = tweetStore.addToKnownUrls(urlChain)

        assert urlChain == knownUrls
        assert urlChain == tweetStore.knownUrls
        assert 3 == tweetStore.getNumberOfKnownUrls()
    }

    @Test
    void shouldStoreTweetsFromDifferentQueriesWithDifferentKeys() {
        // given
        def twitterJsonTweets = getTweetsFromTwitterJsonText()
        def mainQueryTweetText = [
            twitterJsonTweets.results[15],
            twitterJsonTweets.results[16]
        ]

        def otherQueryTweetText = [
            twitterJsonTweets.results[0],
            twitterJsonTweets.results[1]
        ]

        def mainQueryTweets = mainQueryTweetText.collect { TweetFactory.createFromTwitterJson(it) }
        def otherQueryTweets = otherQueryTweetText.collect { TweetFactory.createFromTwitterJson(it) }

        ITweetStore otherQueryTweetStore = new RedisTweetStore(jedis, OTHER_QUERY)

        // when
        mainQueryTweets.each { tweetStore.storeTweet(it) }
        otherQueryTweets.each { otherQueryTweetStore.storeTweet(it) }

        // then
        def storedMainQueryTweets = tweetStore.getStoredTweets()
        assert mainQueryTweets == storedMainQueryTweets

        def storedOtherQueryTweets = otherQueryTweetStore.getStoredTweets()
        assert otherQueryTweets == storedOtherQueryTweets

        otherQueryTweetStore.clear()
    }

    // TODO: duplicated method from TweetTest
    private def getTweetsFromTwitterJsonText() {
        File file = new File(ClassLoader.getSystemResource("tweets.json").toURI());
        def jsonPayload = file.text
        def slurper = new JsonSlurper()
        def tweets = slurper.parseText(jsonPayload)

        tweets
    }
}
