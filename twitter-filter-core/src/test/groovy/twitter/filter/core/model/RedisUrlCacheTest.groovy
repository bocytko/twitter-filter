package twitter.filter.core.model

import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import redis.clients.jedis.Jedis

class RedisUrlCacheTest {
    private static String HOST = "localhost"

    private static String TEST_KEY_PREFIX = "test:"

    private static def urls = ["url1", "url2"]
    private static def resolvedUrls = [["A", "B"], ["C"]]

    private static Jedis jedis
    private RedisUrlCache urlCache

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
        urlCache = new RedisUrlCache(jedis)
    }

    @After
    void after() {
        urlCache.clear()
    }

    @Test
    void canStoreSingleUrlInCache() {
        // given
        def url = urls[0]
        def resolvedUrls = resolvedUrls[0]

        // when
        urlCache.put(url, resolvedUrls)

        // then
        def resolvedUrlsFromCache = urlCache.get(url)
        assert resolvedUrls == resolvedUrlsFromCache
    }

    @Test
    void shouldReturnEmptyListUnknownUrl() {
        // given
        def unknownUrl = "unknownUrl"

        // when
        def resolvedUrls = urlCache.get(unknownUrl)

        // then
        assert resolvedUrls == []
    }

    @Test
    void canStoreMultipleUrlsInCache() {
        // given

        // when
        urlCache.putAll(urls, resolvedUrls)

        // then
        def resolvedUrlsFromCache = urlCache.get(urls[0])
        assert resolvedUrls[0] == resolvedUrlsFromCache

        resolvedUrlsFromCache = urlCache.get(urls[1])
        assert resolvedUrls[1] == resolvedUrlsFromCache
    }

    @Test
    void shouldKeepAListOfAllCachedUrls() {
        // given

        // when
        urlCache.putAll(urls, resolvedUrls)

        // then
        def cachedUrls = urlCache.getCachedUrls()
        assert urls == cachedUrls
    }

    @Test
    void shouldReturnEmptyDatasetIfAllUrlsAreCached() {
        // given
        urlCache.putAll(urls, resolvedUrls)

        // when
        def unknownUrls = urlCache.filterKnownUrls(urls)

        // then
        assert unknownUrls == []
    }

    @Test
    void shouldReturnUnknownUrlsIfTheyAreNotNotCached() {
        // given
        urlCache.putAll(urls, resolvedUrls)

        // when
        def unknownUrls = urlCache.filterKnownUrls([
            "url1",
            "C",
            "D",
            "url2",
            "E"
        ])

        // then
        assert unknownUrls == ["C", "D", "E"]
    }
}
