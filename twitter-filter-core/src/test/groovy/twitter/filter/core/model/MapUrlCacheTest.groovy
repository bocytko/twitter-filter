package twitter.filter.core.model

import groovy.mock.interceptor.MockFor;

import org.junit.Test

import twitter.filter.core.Tweet;
import twitter.filter.core.util.ParallelUrlResolver;

class MapUrlCacheTest {
    private static def urls = ["A", "B"]
    private static def resolvedUrls = [
        ["A", "AA", "AAA"],
        ["B", "BB"]
    ]

    @Test
    void canAddSingleUrlToCache() {
        // given
        UrlCache urlCache = new MapUrlCache()

        def url = "A"
        def resolvedUrl = ["A", "AA", "AAA"]

        // when
        urlCache.put(url, resolvedUrl)

        // then
        assert urlCache.get("A") == ["A", "AA", "AAA"]
    }

    @Test
    void canAddMultipleUrlsToCache() {
        // given
        UrlCache urlCache = new MapUrlCache()

        def urls = ["A", "B"]
        def resolvedUrls = [
            ["A", "AA", "AAA"],
            ["B", "BB"]
        ]

        // when
        urlCache.putAll(urls, resolvedUrls)

        // then
        assert urlCache.get("A") == ["A", "AA", "AAA"]
        assert urlCache.get("B") == ["B", "BB"]
    }


    @Test
    void shouldReturnEmptyListUnknownUrl() {
        // given
        UrlCache urlCache = new MapUrlCache()
        def unknownUrl = "unknownUrl"

        // when
        def resolvedUrls = urlCache.get(unknownUrl)

        // then
        assert resolvedUrls == null
    }

    @Test
    void shouldReturnEmptyDatasetIfAllUrlsAreCached() {
        // given
        UrlCache urlCache = new MapUrlCache()
        urlCache.putAll(urls, resolvedUrls)

        // when
        def unknownUrls = urlCache.filterKnownUrls(urls)

        // then
        assert unknownUrls == []
    }

    @Test
    void shouldReturnUnknownUrlsIfTheyAreNotNotCached() {
        // given
        UrlCache urlCache = new MapUrlCache()
        urlCache.putAll(urls, resolvedUrls)

        // when
        def unknownUrls = urlCache.filterKnownUrls(["A", "C", "D", "B", "E"])

        // then
        assert unknownUrls == ["C", "D", "E"]
    }

    @Test
    void shouldPopulateUrlCacheFromTweets() {
        // given
        UrlCache urlCache = new MapUrlCache()
        Tweet foo = new Tweet()
        Tweet bar = new Tweet()
        foo.urls = ["A"]
        bar.urls = ["B"]

        def urlResolver = new MockFor(ParallelUrlResolver.class)
        urlResolver.demand.resolveUrls() { resolvedUrls }

        // when
        urlCache.populateWithUrlsFromTweets([foo, bar], urlResolver.proxyInstance())

        // then
        assert urlCache.get("A") == ["A", "AA", "AAA"]
        assert urlCache.get("B") == ["B", "BB"]
    }
}
