package twitter.filter.core.model

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass
import org.junit.AfterClass
import org.junit.Test
import redis.clients.jedis.Jedis
import twitter.filter.core.Tweet
import twitter.filter.core.TweetFactory;

class RedisRelatedTweetsStoreTest {
    private static final String HOST = "localhost"
    private static Jedis jedis

    private RedisRelatedTweetsStore relatedTweets

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
        relatedTweets = new RedisRelatedTweetsStore(jedis, "#foo")
    }

    @After
    void emptyRelatedTweets() {
        relatedTweets.clear()
    }

    @Test
    void unknownTweetShouldHaveZeroRelatedTweets() {
        Tweet tweet = TweetFactory.createFromText("missing tweet")

        def result = relatedTweets.getRelatedTweets(tweet) as List

        assert result.isEmpty()
    }

    @Test
    void canRetrieveRelatedTweetsForATweet() {
        Tweet tweet = TweetFactory.createFromText("tweet")
        Tweet first = TweetFactory.createFromText("1. related")
        Tweet second = TweetFactory.createFromText("2. related")

        relatedTweets.add(tweet, first)
        relatedTweets.add(tweet, second)

        def result = relatedTweets.getRelatedTweets(tweet) as List
        assert [first, second] == result
    }

    @Test
    void canClearRelatedTweetsStore() {
        Tweet tweet = TweetFactory.createFromText("tweet")
        Tweet first = TweetFactory.createFromText("1. related")
        relatedTweets.add(tweet, first)

        relatedTweets.clear()

        def result = relatedTweets.getRelatedTweets(tweet) as List
        assert result.isEmpty()
    }

    @Test
    void canStoreAListOfRelatedTweets() {
        Tweet tweet = TweetFactory.createFromText("tweet")
        Tweet first = TweetFactory.createFromText("1. related")
        Tweet second = TweetFactory.createFromText("2. related")

        relatedTweets.add(tweet, [first, second])

        def result = relatedTweets.getRelatedTweets(tweet) as List
        assert [first, second] == result
    }

    @Test
    void shouldAddOnlyUniqueRelatedTweets() {
        Tweet tweet = TweetFactory.createFromText("tweet")
        Tweet first = TweetFactory.createFromText("1. related")

        relatedTweets.add(tweet, [first, first])

        def result = relatedTweets.getRelatedTweets(tweet) as List
        assert [first] == result
    }
}
