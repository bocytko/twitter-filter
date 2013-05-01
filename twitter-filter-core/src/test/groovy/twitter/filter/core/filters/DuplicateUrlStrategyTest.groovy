package twitter.filter.core.filters

import groovy.mock.interceptor.MockFor

import org.junit.Test

import twitter.filter.core.TweetFactory
import twitter.filter.core.model.ITweetStore
import twitter.filter.core.model.UrlCache

class DuplicateUrlStrategyTest {
    @Test
    void shouldIgnoreMalformedUrls() {
        def tweetText = [
            "Malformed URL: t.co/ul1W8mgx",
            ["t.co/ul1W8mgx"]
        ]
        def tweet = TweetFactory.createFromTextAndUrl(tweetText)

        def urlCacheMock = new MockFor(UrlCache.class)
        def urlCache = urlCacheMock.proxyInstance()

        def tweetStoreMock = new MockFor(ITweetStore.class)
        def tweetStore = tweetStoreMock.proxyInstance()

        DuplicateUrlStrategy strategy = new DuplicateUrlStrategy(tweetStore, urlCache)

        assert null == strategy.apply(tweet)
    }

    @Test
    void aTweetLinkingDirectlyToSameAddressShouldBeDuplicateOfOriginalTweet() {
        def tweetText = [
            "Amigos,tengo un nuevo cliente: @Cachinko ! http://t.co/9z8y2EaD, busca desarrolladores #AJAX, #Python, #Mongo, #Hadoop, #Net. Proyecto #DF",
            ["http://t.co/9z8y2EaD"]
        ]
        def tweet = TweetFactory.createFromTextAndUrl(tweetText)

        def urlCacheMock = new MockFor(UrlCache.class)
        urlCacheMock.demand.get(1) { ["http://linkedin.com"] }

        def tweetStoreMock = new MockFor(ITweetStore.class)
        tweetStoreMock.demand.getTweetForUrl(1) { it == "http://linkedin.com" ? tweet : null }

        def urlCache = urlCacheMock.proxyInstance()
        def tweetStore = tweetStoreMock.proxyInstance()

        DuplicateUrlStrategy strategy = new DuplicateUrlStrategy(tweetStore, urlCache)

        def otherTweetText = [
            "My favorite link today: http://t.co/9z8y2EaD",
            ["http://t.co/9z8y2EaD"]
        ]

        def otherTweet = TweetFactory.createFromTextAndUrl(otherTweetText)

        assert tweet == strategy.apply(otherTweet)

    }

    @Test
    void aTweetLinkingIndirectlyToSameAddressShouldBeDuplicateOfOriginalTweet() {
        def tweetText = [
            "Pleased to see the Pig UDFs released by LinkedIn SNA team: http://t.co/ul1W8mgx #in #hadoop",
            ["http://t.co/ul1W8mgx"]
        ]
        def tweet = TweetFactory.createFromTextAndUrl(tweetText)

        def urlCacheMock = new MockFor(UrlCache.class)
        urlCacheMock.demand.get(2) { [it, "http://linkedin.com"] }

        def tweetStoreMock = new MockFor(ITweetStore.class)
        tweetStoreMock.demand.getTweetForUrl(2) { it == "http://linkedin.com" ? tweet : null }

        def urlCache = urlCacheMock.proxyInstance()
        def tweetStore = tweetStoreMock.proxyInstance()

        DuplicateUrlStrategy strategy = new DuplicateUrlStrategy(tweetStore, urlCache)

        def otherTweetText = [
            "Worth keeping in mind: datafu is LinkedIn's collection of #Pig UDFs for Statistics and Data Mining http://t.co/7Jlum0R5 - #hadoop",
            ["http://t.co/7Jlum0R5"]
        ]

        def otherTweet = TweetFactory.createFromTextAndUrl(otherTweetText)

        assert tweet == strategy.apply(otherTweet)
    }
}
