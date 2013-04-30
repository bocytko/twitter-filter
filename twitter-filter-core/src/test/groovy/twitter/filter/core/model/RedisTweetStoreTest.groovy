package twitter.filter.core.model

import groovy.json.JsonSlurper
import groovy.mock.interceptor.MockFor

import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import twitter.filter.core.Tweet
import twitter.filter.core.TweetFactory
import redis.clients.jedis.Jedis

class RedisTweetStoreTest {
    private static final String HOST = "localhost"
    private static final String TEST_QUERY = "#hadoop"
    private static final String OTHER_QUERY = "#hive"

    private static Jedis jedis

    private UrlCache urlCache
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
        urlCache = new MapUrlCache()
        tweetStore = new RedisTweetStore(jedis, TEST_QUERY, urlCache)
        assert tweetStore.getStoredTweets() == []
    }

    @After
    void after() {
        tweetStore.clear()
        assert tweetStore.getStoredTweets() == []

        jedis.flushDB()
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
    void allUrlsShallReferenceSavedTweet() {
        // given
        def foo = new Tweet()
        def bar = new Tweet()
        foo.text = "A"
        bar.text = "B"
        foo.urls = ["first"]
        bar.urls = ["second"]
        def tweets = [ foo, bar ]

        def urlCacheMock = new MockFor(UrlCache)
        urlCacheMock.demand.get(2) { it == "first" ? ["first", "final url"] : ["second", "final url"] }

        tweetStore = new RedisTweetStore(jedis, TEST_QUERY, urlCacheMock.proxyInstance())

        // when
        tweets.each { tweetStore.storeTweet(it) }

        // then
        def storedTweets = tweetStore.getStoredTweets()

        assert tweets == storedTweets
        assert 2 == tweetStore.getNumberOfStoredTweets()
        assert 3 == jedis.keys("tweet-url*").size()
    }

    @Test
    void shouldRemoveTweetsMatchingCondition() {
        // given
        def twitterJsonTweets = getTweetsFromTwitterJsonText()
        def tweetText = [
            twitterJsonTweets.results[15],
            twitterJsonTweets.results[16]
        ]

        def tweets = tweetText.collect { TweetFactory.createFromTwitterJson(it) }
        tweets.each { tweetStore.storeTweet(it) }

        // when
        def selector = { Tweet t -> t.from_user == "Edgar_Villegas" }
        tweetStore.removeTweets(selector)

        // then
        def storedTweets = tweetStore.getStoredTweets()

        assert [tweets[0]] == storedTweets
        assert 1 == tweetStore.getNumberOfStoredTweets()
    }

    @Test
    void canRetrieveTweetForUrl() {
        // given
        urlCache.put("A", ["A"])
        urlCache.put("B", ["B"])

        Tweet foo = new Tweet()
        foo.urls = ["A", "B"]
        foo.text = "tweet"

        // when
        tweetStore.storeTweet(foo)

        // then
        assert foo == tweetStore.getTweetForUrl("A")
        assert foo == tweetStore.getTweetForUrl("B")
    }

    @Test
    void shouldReturnNullForUnknownUrl() {
        assert null == tweetStore.getTweetForUrl("B")
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

        ITweetStore otherQueryTweetStore = new RedisTweetStore(jedis, OTHER_QUERY, urlCache)

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
