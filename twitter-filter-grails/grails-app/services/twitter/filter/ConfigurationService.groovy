package twitter.filter

import java.util.Collection;

import redis.clients.jedis.Jedis;

class ConfigurationService {
    def grailsApplication

    private static final String CONFIG_HASHTAGS = "config:hashtags"
    private static final String CONFIG_IGNORED_USERS = "config:ignoredUsers"

    def getNumberOfThreads() {
        grailsApplication.config.filter.numThreads
    }

    def getPagesToFetch() {
        grailsApplication.config.filter.pagesToFetch
    }

    def getDefaultIgnoredUsers() {
        grailsApplication.config.filter.ignoredUsers
    }

    def getDefaultHashTags() {
        grailsApplication.config.filter.queries
    }

    def getHashTags(Jedis jedis) {
        Set<String> hashTags = jedis.smembers(CONFIG_HASHTAGS)

        if (!hashTags || hashTags.isEmpty()) {
            hashTags = getDefaultHashTags()
        }

        hashTags
    }

    void setHashTags(Jedis jedis, Collection<String> hashTags) {
        removeAllValuesFromSet(jedis, CONFIG_HASHTAGS)

        hashTags.each {
            jedis.sadd(CONFIG_HASHTAGS, it)
        }
    }

    def getIgnoredUsers(Jedis jedis) {
        Set<String> ignoredUsers = jedis.smembers(CONFIG_IGNORED_USERS)

        if (!ignoredUsers || ignoredUsers.isEmpty()) {
            ignoredUsers = getDefaultIgnoredUsers()
        }

        ignoredUsers
    }

    void setIgnoredUsers(Jedis jedis, Collection<String> ignoredUsers) {
        removeAllValuesFromSet(jedis, CONFIG_IGNORED_USERS)

        ignoredUsers.each {
            jedis.sadd(CONFIG_IGNORED_USERS, it)
        }
    }

    private void removeAllValuesFromSet(Jedis jedis, String key) {
        def setValues = jedis.smembers(key)

        setValues.each {
            jedis.srem(key, it)
        }
    }
}
