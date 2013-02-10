package twitter.filter.core.util

import org.junit.Before
import org.junit.Test

class UrlResolverTest {
    private UrlResolver resolver

    @Before
    void initURLResolver() {
        resolver = new UrlResolver()
    }

    @Test
    void shouldResolveShortenedAndFollowRedirectsToOtherUrls() {
        def url = "http://bit.ly/w0qqxB"

        def originalURL = resolver.getOriginalURL(url)

        assert originalURL == "http://www.mapr.com/company/press-releases/mapr-announces-version-1-2-of-the-mapr-distribution-for-apache-hadoop"
        assert resolver.urlChain == [
            "http://bit.ly/w0qqxB",
            "http://www.mapr.com/news/mapr1-2",
            "http://www.mapr.com/company/press-releases/mapr-announces-version-1-2-of-the-mapr-distribution-for-apache-hadoop"
        ]
    }

    @Test
    void shouldResolveDoubleShortenedUrl() {
        // http://t.co/0w8SvsmA -> http://bit.ly/w0qqxB
        def url = "http://t.co/0w8SvsmA"

        def originalURL = resolver.getOriginalURL(url)

        assert originalURL == "http://www.mapr.com/company/press-releases/mapr-announces-version-1-2-of-the-mapr-distribution-for-apache-hadoop"
    }

    @Test
    void shouldReturnLastValidUrlForUrlForwardingToMalformedUrls() {
        def url = "http://t.co/uBFuduvw"
        def secondUrl = "http://j.mp/t7TdCa"
        def thirdUrl = "http://nosql.mypopescu.com/post/13821082555"
        def malformedUrl = "/post/13821082555?3524e100"

        def urlAndResponseChain = [
            // [responseCode, location, originalUrl
            [301, secondUrl, url],
            [301, thirdUrl, secondUrl],
            [301, malformedUrl, thirdUrl]
        ]

        // mock url resolver
        UrlResolver resolver = new UrlResolver()

        int callCount = 0
        resolver.metaClass.resolve = {
            if (callCount + 1 > urlAndResponseChain.size()) {
                new URL(malformedUrl)
            }

            urlAndResponseChain[callCount++]
        }

        def originalURL = resolver.getOriginalURL(url)

        assert originalURL == "http://nosql.mypopescu.com/post/13821082555"
    }

    @Test
    void shouldKeepOriginalUrlForMalformedUrls() {
        def url = "malformedURL"

        def originalUrl = resolver.getOriginalURL(url)

        assert originalUrl == "malformedURL"
    }

    @Test
    void shouldKeepUrlOnSocketExceptionForCheckedUrl() {
        def url = "http://timeout-testing.z"

        UrlResolver resolver = new UrlResolver()
        resolver.metaClass.resolve = { throw new SocketTimeoutException("connect timed out") }

        def originalUrl = resolver.getOriginalURL(url)

        assert originalUrl == "http://timeout-testing.z"
    }

    @Test
    void shouldKeepUrlOnUnknownHostExceptionForCheckedUrl() {
        def url = "http://unknown-host-for-testing.z"

        UrlResolver resolver = new UrlResolver()
        resolver.metaClass.resolve = { throw new UnknownHostException(url) }

        def originalUrl = resolver.getOriginalURL(url)

        assert originalUrl == "http://unknown-host-for-testing.z"
    }

    @Test
    void shouldKeepOriginalUrlOnUnknownHostExceptionForForwardedUrl() {
        def url = "http://unknown-host-at-step-2.z"
        def firstCall = true

        UrlResolver resolver = new UrlResolver()
        resolver.metaClass.resolve = {
            if (firstCall) {
                firstCall = false
                [300, "http://forwarded-uknown-host.z", url ]
            } else {
                throw new UnknownHostException(url)
            }
        }

        def originalUrl = resolver.getOriginalURL(url)

        assert originalUrl == "http://unknown-host-at-step-2.z"
    }
}
