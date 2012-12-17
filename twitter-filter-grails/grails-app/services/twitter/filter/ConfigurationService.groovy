package twitter.filter

import java.util.Collection;

import redis.clients.jedis.Jedis;

class ConfigurationService {
    private static final String CONFIG_HASHTAGS = "config:hashtags"
    private static final String CONFIG_IGNORED_USERS = "config:ignoredUsers"

    def grailsApplication

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
        getConfigValueWithDefault(jedis, CONFIG_HASHTAGS, getDefaultHashTags())
    }

    def getIgnoredUsers(Jedis jedis) {
        getConfigValueWithDefault(jedis, CONFIG_IGNORED_USERS, getDefaultIgnoredUsers())
    }

    private def getConfigValueWithDefault(Jedis jedis, String key, def defaultValue) {
        Set<String> configValue = jedis.smembers(key)

        if (!configValue || configValue.isEmpty()) {
            configValue = defaultValue
        }

        configValue
    }

    void setHashTags(Jedis jedis, Collection<String> hashTags) {
        setConfigValuesFor(jedis, CONFIG_HASHTAGS, hashTags)
    }

    void setIgnoredUsers(Jedis jedis, Collection<String> ignoredUsers) {
        setConfigValuesFor(jedis, CONFIG_IGNORED_USERS, ignoredUsers)
    }

    private void setConfigValuesFor(Jedis jedis, String key, Collection<String> values) {
        removeAllValuesFromSet(jedis, key)

        print "Setting $key to $values"

        values.each {
            jedis.sadd(key, it)
        }
    }

    private void removeAllValuesFromSet(Jedis jedis, String key) {
        def setValues = jedis.smembers(key)

        setValues.each {
            jedis.srem(key, it)
        }
    }
}
