package twitter.filter.core.util

import java.text.ParseException
import java.text.SimpleDateFormat

import org.junit.Test

class ParallelUrlResolverTest {
    @Test
    void canResolveUlrsInParallel() {
        def urls = [
            "http://bit.ly/w0qqxB",
            "http://t.co/rhTrPEJy"
        ]
        def originalUrls = [
            "http://www.mapr.com/company/press-releases/mapr-announces-general-availability-of-version-1-2-of-the-mapr-distribution-for-apache-hadoop",
            "http://blogs.splunk.com/2011/12/05/introducing-shep/"
        ]

        ParallelUrlResolver resolver = new ParallelUrlResolver(threads: 2)
        def resolvedUrls = resolver.resolveUrls(urls)

        assert resolvedUrls == [
            [
                "http://bit.ly/w0qqxB",
                "http://www.mapr.com/news/mapr1-2",
                "http://www.mapr.com/company/press-releases/mapr-announces-general-availability-of-version-1-2-of-the-mapr-distribution-for-apache-hadoop"
            ],
            [
                "http://t.co/rhTrPEJy",
                "http://blogs.splunk.com/2011/12/05/introducing-shep/"]
        ]
    }

    @Test
    void canResolveUlrsWithOneThread() {
        def urls = [
            "http://bit.ly/w0qqxB",
            "http://t.co/rhTrPEJy"
        ]
        def originalUrls = [
            "http://www.mapr.com/company/press-releases/mapr-announces-general-availability-of-version-1-2-of-the-mapr-distribution-for-apache-hadoop",
            "http://blogs.splunk.com/2011/12/05/introducing-shep/"
        ]

        ParallelUrlResolver resolver = new ParallelUrlResolver(threads: 1)
        def resolvedUrls = resolver.resolveUrls(urls)

        assert resolvedUrls == [
            [
                "http://bit.ly/w0qqxB",
                "http://www.mapr.com/news/mapr1-2",
                "http://www.mapr.com/company/press-releases/mapr-announces-general-availability-of-version-1-2-of-the-mapr-distribution-for-apache-hadoop"
            ],
            [
                "http://t.co/rhTrPEJy",
                "http://blogs.splunk.com/2011/12/05/introducing-shep/"]
        ]
    }

    @Test
    void cannn() {
        def timestamp = "Thu, 19 Jan 2012 22:56:59 +0000"
        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        Date date;
        try {
            date = format.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
