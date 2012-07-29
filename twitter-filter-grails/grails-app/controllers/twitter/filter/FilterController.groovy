package twitter.filter

import redis.clients.jedis.Jedis

class FilterController {

    FilterService filterService
    def redisService

    def index() {
        def tweets = []

        if (null == params.query) {
            params.query = grailsApplication.config.filter.queries[0]
        }

        redisService.withRedis { Jedis jedis ->
            tweets = filterService.getLastNTweets(jedis, params.query, 100)
        }

        render(view: "index", model: [tweets: tweets])
    }

    def filter() {
        int numAddedTweets = 0

        redisService.withRedis { Jedis jedis ->
            numAddedTweets = filterService.filterTweets(jedis, params.query)
        }

        render "Newly added tweets: ${numAddedTweets}"
    }

    def progress() {
        def progressInfo = ""

        redisService.withRedis { Jedis jedis ->
            progressInfo = filterService.getProgress(jedis, params.query)
        }

        render progressInfo
    }

    def stats() {
        def datastoreStats
        def queries = grailsApplication.config.filter.queries

        redisService.withRedis { Jedis jedis ->
            datastoreStats = filterService.getDatastoreStats(jedis, queries)
        }

        render(view: "stats", model: [queries: queries, stats: datastoreStats])
    }

    def configure() {
        def ignoredUsers = ""

        redisService.withRedis { Jedis jedis ->
            ignoredUsers = filterService.getIgnoredUsers(jedis).join(",")
        }

        render(view: "configure", model: [ignoredUsers: ignoredUsers])
    }

    def updateConfiguration() {
        def ignoredUsers = params.ignoredUsers.split(",").collect { it.trim() }

        redisService.withRedis { Jedis jedis ->
            filterService.setIgnoredUsers(jedis, ignoredUsers)
        }

        redirect(action: "configure")
    }

    def clear() {
        redisService.withRedis { Jedis jedis ->
            filterService.clearStoredTweetsAndUrlCache(jedis, params.query)
        }

        redirect(action: "stats")
    }
}
