package twitter.filter.core.model

import groovy.util.logging.Log4j

import redis.clients.jedis.Jedis
import twitter.filter.core.Tweet
import twitter.filter.core.TweetFactory

@Log4j("log")
public class RedisRelatedTweetsStore implements IRelatedTweetsStore {
    private static final String TWEETS_KEY = "related-tweets"
    private static final String SEPARATOR = ":"

    private String tweetsKeyPrefix

    private Jedis jedis

    RedisRelatedTweetsStore(Jedis jedis, def query) {
        this.tweetsKeyPrefix = TWEETS_KEY + SEPARATOR + query
        this.jedis = jedis

        log.info "tweetsKeyPrefix: ${this.tweetsKeyPrefix}"
    }

    @Override
    def add(Tweet tweet, Tweet relatedTweet) {
        jedis.rpush(getKey(tweet), relatedTweet.toJson())
    }

    @Override
    def add(Tweet tweet, Collection related) {
        related.each { add(tweet, it) }
    }

    @Override
    def getRelatedTweets(Tweet tweet) {
        def relatedTweets = jedis.lrange(getKey(tweet), 0, -1)

        relatedTweets.collect { TweetFactory.createFromJson(it) }
    }

    @Override
    void clear() {
        Set<String> keys = jedis.keys(tweetsKeyPrefix + "*")
        keys.each { jedis.del(it) }
    }

    private def getKey(Tweet tweet) {
        tweetsKeyPrefix + tweet.toJson()
    }
}
