package twitter.filter.core.model

import groovy.util.logging.Log4j
import twitter.filter.core.Tweet
import twitter.filter.core.TweetFactory
import redis.clients.jedis.Jedis

@Log4j("log")
class RedisTweetStore implements ITweetStore {
    private static final String TWEETS_KEY = "tweets"
    private static final String TWEET_TO_URL_KEY = "tweet-url"
    private static final String SEPARATOR = ":"
    private static final String WILDCARD = "*"

    private static final int MAX_STORED_TWEETS = 200

    private String tweetsKey
    private String tweetToUrlKey

    private Jedis jedis
    private UrlCache urlCache

    RedisTweetStore(Jedis jedis, def query, UrlCache urlCache) {
        this.tweetsKey = TWEETS_KEY + SEPARATOR + query
        this.tweetToUrlKey = TWEET_TO_URL_KEY + SEPARATOR + query + SEPARATOR
        this.jedis = jedis
        this.urlCache = urlCache

        log.info "tweetsKey: ${this.tweetsKey}"
    }

    @Override
    void storeTweet(Tweet tweet) {
        def tweetAsJson = tweet.toJson()
        storeUrlsForTweet(tweet)

        jedis.rpush(tweetsKey, tweetAsJson)
        jedis.ltrim(tweetsKey, -MAX_STORED_TWEETS, -1)
    }

    private void storeUrlsForTweet(Tweet tweet) {
        def tweetAsJson = tweet.toJson()
        tweet.urls.each {
            // each url from chain shall reference the tweet
            urlCache.get(it).each { jedis.rpush(tweetToUrlKey + it, tweetAsJson) }
        }
    }

    @Override
    def getStoredTweets() {
        def storedTweets = jedis.lrange(tweetsKey, 0, -1)

        storedTweets.collect { TweetFactory.createFromJson(it) }
    }

    @Override
    Tweet getTweetForUrl(def url) {
        def tweetAsJson = jedis.lrange(tweetToUrlKey + url, 0, 1)

        tweetAsJson.isEmpty() ? null : TweetFactory.createFromJson(tweetAsJson[0])
    }

    @Override
    def removeTweets(Closure condition) {
        def storedTweets = getStoredTweets()
        storedTweets.removeAll(condition)

        clear()
        storedTweets.each { storeTweet(it) }
    }

    @Override
    def getNumberOfStoredTweets() {
        jedis.llen(tweetsKey)
    }

    @Override
    void clear() {
        jedis.del(tweetsKey)

        Set<String> urls = jedis.keys(tweetToUrlKey + WILDCARD)
        urls.each { jedis.del(it) }
    }
}
