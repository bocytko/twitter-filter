package twitter.filter

import java.util.Collection;

import redis.clients.jedis.Jedis;

class ConfigurationService {
    def grailsApplication

    private static final String CONFIG_IGNORED_USERS = "config:ignoredUsers"

    def getNumberOfThreads() {
        grailsApplication.config.filter.numThreads
    }

    def getPagesToFetch() {
        grailsApplication.config.filter.pagesToFetch
    }

    def getIgnoredUsers(Jedis jedis) {
        Set<String> ignoredUsers = jedis.smembers(CONFIG_IGNORED_USERS)

        if (!ignoredUsers || ignoredUsers.isEmpty()) {
            ignoredUsers = grailsApplication.config.filter.ignoredUsers
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
