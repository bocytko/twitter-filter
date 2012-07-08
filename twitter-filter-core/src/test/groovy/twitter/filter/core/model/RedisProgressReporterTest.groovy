package twitter.filter.core.model

import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import redis.clients.jedis.Jedis

class RedisProgressReporterTest {
    private static String HOST = "localhost"
    private static String TEST_QUERY = "#hadoop"
    private static String OTHER_QUERY = "#hive"

    private static Jedis jedis

    private RedisProgressReporter progressReporter

    @BeforeClass
    static void initializeRedis() {
        jedis = new Jedis(HOST)

        // select test database
        jedis.select(1)
    }

    @Before
    void prepareRedisProgressReporter() {
        progressReporter = new RedisProgressReporter(jedis, TEST_QUERY)
        progressReporter.setMaxIterations(1)
        progressReporter.startIteration(4)
    }

    @AfterClass
    static void flushRedis() {
        jedis.flushDB()
    }

    @Test
    void shouldSaveProgressInRedisDBOnElementIncrementation() {
        // given

        // when
        progressReporter.incrementProcessedElements()

        // then
        def progress = jedis.get(RedisProgressReporter.PROGRESS_KEY + RedisProgressReporter.SEPARATOR + TEST_QUERY)
        assert "Page 1/1: tweets: 1/4 (total: 25.00%)" == progress
    }

    @Test
    void savedProgressShouldExpireAfterAGivenAmountOfSeconds() {
        // given
        progressReporter.setExpireTime(1)

        // when
        5.times { progressReporter.incrementProcessedElements() }

        // then
        def key = RedisProgressReporter.PROGRESS_KEY + RedisProgressReporter.SEPARATOR + TEST_QUERY
        def progress = jedis.get(key)
        assert null != progress

        Thread.sleep(2000)

        progress = jedis.get(key)
        assert null == progress
    }
}
