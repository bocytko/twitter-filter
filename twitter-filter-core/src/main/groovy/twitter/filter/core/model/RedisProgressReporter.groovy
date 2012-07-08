package twitter.filter.core.model

import redis.clients.jedis.Jedis

class RedisProgressReporter extends SimpleProgressReporter {
    private static String PROGRESS_KEY = "progress"
    private static String SEPARATOR = ":"
    private static int DEFAULT_EXPIRE_TIME = 20

    int expireTime = DEFAULT_EXPIRE_TIME

    private String progressKey
    private Jedis jedis

    RedisProgressReporter(Jedis jedis, def query) {
        super()

        this.progressKey = PROGRESS_KEY + SEPARATOR + query
        this.jedis = jedis
    }

    @Override
    public void incrementProcessedElements() {
        super.incrementProcessedElements()

        jedis.set(progressKey, getProgressString())
        jedis.expire(progressKey, expireTime)
    }
}
