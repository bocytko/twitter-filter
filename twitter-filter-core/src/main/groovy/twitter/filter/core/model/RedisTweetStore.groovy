package twitter.filter.core.model

import groovy.util.logging.Log4j
import twitter.filter.core.Tweet
import twitter.filter.core.TweetFactory
import redis.clients.jedis.Jedis

@Log4j("log")
class RedisTweetStore implements ITweetStore {
    private static String TWEETS_KEY = "tweets"
    private static String KNOWN_URLS_KEY = "tweets-known-urls"
    private static String SEPARATOR = ":"

    private static int MAX_STORED_TWEETS = 200

    private String tweetsKey
    private String knownUrlsKey

    private Jedis jedis

    RedisTweetStore(Jedis jedis, def query) {
        this.tweetsKey = TWEETS_KEY + SEPARATOR + query
        this.knownUrlsKey = KNOWN_URLS_KEY + SEPARATOR + query
        this.jedis = jedis

        log.info "tweetsKey: ${this.tweetsKey}"
        log.info "knownUrlsKey: ${knownUrlsKey}"
    }

    @Override
    void storeTweet(Tweet tweet) {
        def tweetAsJson = tweet.toJson()
        jedis.rpush(tweetsKey, tweetAsJson)

        jedis.ltrim(tweetsKey, -MAX_STORED_TWEETS, -1)
    }

    @Override
    def getStoredTweets() {
        def storedTweets = jedis.lrange(tweetsKey, 0, -1)

        storedTweets.collect { TweetFactory.createFromJson(it) }
    }

    @Override
    def addToKnownUrls(def urlChain) {
        urlChain.each {
            jedis.rpush(knownUrlsKey, it)
        }

        getKnownUrls()
    }

    @Override
    def getKnownUrls() {
        jedis.lrange(knownUrlsKey, 0, -1)
    }

    @Override
    def getNumberOfStoredTweets() {
        jedis.llen(tweetsKey)
    }

    @Override
    def getNumberOfKnownUrls() {
        jedis.llen(knownUrlsKey)
    }

    @Override
    void clear() {
        jedis.del(tweetsKey)
        jedis.del(knownUrlsKey)
    }
}
