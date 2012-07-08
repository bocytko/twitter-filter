package twitter.filter.core.model

import redis.clients.jedis.Jedis

class RedisUrlCache extends UrlCache {
    // key for list of all cached urls
    private static String CACHED_URLS_KEY = "cached-urls"

    // key for cached url elements
    private static String URL_KEY = "url:"

    private Jedis jedis

    RedisUrlCache(Jedis jedis) {
        this.jedis = jedis
    }

    @Override
    void put(def url, def resolvedUrl) {
        def urlKey = getUrlKey(url)

        // save cached url
        jedis.rpush(CACHED_URLS_KEY, url)

        // save cached resolved urls
        resolvedUrl.each {
            jedis.rpush(urlKey, it)
        }
    }

    @Override
    def get(def url) {
        def key = getUrlKey(url)

        jedis.lrange(key, 0, -1)
    }

    @Override
    def getValueForUnknownElement() {
        []
    }

    def getCachedUrls() {
        jedis.lrange(CACHED_URLS_KEY, 0, -1)
    }

    void clear() {
        def cachedUrls = getCachedUrls()

        // remove all cached urls
        def keysToDelete = cachedUrls.collect { getUrlKey(it) }
        if (keysToDelete.size() > 0) {
            jedis.del(keysToDelete.toArray(new String[0]))
        }

        // remove list of all cached urls
        jedis.del(CACHED_URLS_KEY)
    }

    private def getUrlKey(def url) {
        URL_KEY + url
    }
}
