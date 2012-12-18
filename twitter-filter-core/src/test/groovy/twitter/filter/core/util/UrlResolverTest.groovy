package twitter.filter.core.util

import org.junit.Before;
import org.junit.Test

class UrlResolverTest {
    private static UrlResolver resolver

    @Before
    void initURLResolver() {
        resolver = new UrlResolver()
    }

    @Test
    void shouldResolveShortenedUrl() {
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
    void shouldHandleShortenedLinksWithNoProtocol() {
        // http://t.co/uBFuduvw -> http://j.mp/t7TdCa -> http://nosql.mypopescu.com/post/13821082555 -> /post/13821082555?3524e100
        def url = "http://t.co/uBFuduvw"

        def originalURL = resolver.getOriginalURL(url)

        assert originalURL == "http://nosql.mypopescu.com/post/13821082555"
        assert resolver.urlChain == [
            "http://t.co/uBFuduvw",
            "http://j.mp/t7TdCa",
            "http://nosql.mypopescu.com/post/13821082555"
        ]
    }

    // TODO: mockUrlResolver
    @Test
    void shouldHandleUnkownHostException() {
        def url = "http://t.co/xhhhvx7Y"

        def originalUrl = resolver.getOriginalURL(url)

        assert originalUrl == "http://www.cnews.ru/news/line/index.shtml?2011/12/23/470191"
        assert resolver.urlChain == [
            "http://t.co/xhhhvx7Y",
            "http://bit.ly/uOJk4b",
            "http://rss.feedsportal.com/c/803/f/413231/s/1b36af9a/l/0Lcnews0Bru0Cnews0Cline0Cindex0Bshtml0D20A110C120C230C470A191/story01.htm",
            "http://da.feedsportal.com/c/803/f/413231/s/1b36af9a/l/0Lcnews0Bru0Cnews0Cline0Cindex0Bshtml0D20A110C120C230C470A191/ia1.htm",
            "http://cnews.ru/news/line/index.shtml?2011/12/23/470191",
            "http://www.cnews.ru/news/line/index.shtml?2011/12/23/470191"
        ]
    }

    @Test
    void shouldHandleReadTimeouts() {
        def url = "http://t.co/5b8SR4FP"

        def originalUrl = resolver.getOriginalURL(url)

        assert originalUrl == "http://htl.li/g9lB9"
        assert resolver.urlChain == [
            "http://t.co/5b8SR4FP",
            "http://htl.li/g9lB9",
            "http://gouvernance.dsisionnel.com/2012/12/big-data-des-soucis-de-performance-avec-vos-applications-apache-hadoop/"
        ]
    }
}
