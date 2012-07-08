package twitter.filter.core.model

import org.junit.Test

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
}
