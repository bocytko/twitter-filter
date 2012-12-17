package twitter.filter

import redis.clients.jedis.Jedis

class FilterController {

    ConfigurationService configurationService
    FilterService filterService
    def redisService

    def index() {
        def tweets = []
        def hashTag = params.hashTag

        if (null == hashTag) {
            hashTag = allHashTags[0]
        }

        redisService.withRedis { Jedis jedis ->
            tweets = filterService.getLastNTweets(jedis, hashTag, 100)
        }

        render(view: "index", model: [tweets: tweets, allHashTags: allHashTags, hashTag: hashTag])
    }

    def filter() {
        int numAddedTweets = 0

        redisService.withRedis { Jedis jedis ->
            numAddedTweets = filterService.filterTweets(jedis, params.hashTag)
        }

        render "Newly added tweets: ${numAddedTweets}"
    }

    def progress() {
        def progressInfo = ""

        redisService.withRedis { Jedis jedis ->
            progressInfo = filterService.getProgress(jedis, params.hashTag)
        }

        render progressInfo
    }

    def stats() {
        def datastoreStats

        redisService.withRedis { Jedis jedis ->
            datastoreStats = filterService.getDatastoreStats(jedis, allHashTags)
        }

        render(view: "stats", model: [ allHashTags: allHashTags, stats: datastoreStats])
    }

    def config() {
        def hashTags = ""
        def ignoredUsers = ""

        redisService.withRedis { Jedis jedis ->
            hashTags = allHashTags.join(",")
            ignoredUsers = configurationService.getIgnoredUsers(jedis).join(",")
        }

        render(view: "config", model: [ignoredUsers: ignoredUsers, allHashTags: allHashTags, hashTags: hashTags])
    }

    def updateConfiguration() {
        def hashTags = params.hashTags.split(",").collect { it.trim() }
        def ignoredUsers = params.ignoredUsers.split(",").collect { it.trim() }

        // remove tweets for hash tags that have been removed
        def currentHashTags = allHashTags as HashSet
        def removedHashTags = currentHashTags.minus(hashTags)

        redisService.withRedis { Jedis jedis ->
            removedHashTags.each {
                filterService.clearStoredTweetsAndUrlCache(jedis, it)
            }
        }

        // update configuration
        redisService.withRedis { Jedis jedis ->
            configurationService.setHashTags(jedis, hashTags)
            configurationService.setIgnoredUsers(jedis, ignoredUsers)

            filterService.removeTweetsFromIgnoredUsers(jedis, hashTags, ignoredUsers)
        }

        redirect(action: "config")
    }

    def clear() {
        redisService.withRedis { Jedis jedis ->
            filterService.clearStoredTweetsAndUrlCache(jedis, params.hashTag)
        }

        redirect(action: "stats")
    }

    private def getAllHashTags() {
        def hashTags = []

        redisService.withRedis { Jedis jedis ->
            hashTags = configurationService.getHashTags(jedis).sort().asList()
        }

        hashTags
    }
}
