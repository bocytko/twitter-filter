package twitter.filter.core.view

import org.junit.Test

import twitter.filter.core.Tweet
import twitter.filter.core.TweetFactory

class TweetPrinterTest {
    @Test
    void shouldConvertUrlToHtmlLinksInTweetText() {
        // given
        def text = "sample text http://google.com"
        def url = ["http://google.com"]
        Tweet tweet = TweetFactory.createFromTextAndUrl([text, url])

        // when
        def html = new TweetPrinter().getHtml(tweet)

        // then
        assert html.contains("""sample text <a href="http://google.com">http://google.com</a>""")
    }

    @Test
    void shouldExcludeSpecialCharsFromCreatedLinks() {
        // given
        def text = "sample (http://text.com) http://google.com\" http://grails.org, http://grails.org."
        def url = [
            "http://text.com",
            "http://google.com",
            "http://grails.org",
            "http://grails.org",
        ]
        Tweet tweet = TweetFactory.createFromTextAndUrl([text, url])

        // when
        def html = new TweetPrinter().getHtml(tweet)

        // then
        assert html.contains("""sample (<a href="http://text.com">http://text.com</a>) <a href="http://google.com">http://google.com</a>" <a href="http://grails.org">http://grails.org</a>, <a href="http://grails.org">http://grails.org</a>.""")
    }

    @Test
    void shouldConvertHttpsUrlsToHtmlLinksInTweetText() {
        // given
        def text = "rounding out the list there is #scrunch https://t.co/EgufP7Th which supports #hbase"
        def url = ["https://t.co/EgufP7Th"]
        Tweet tweet = TweetFactory.createFromTextAndUrl([text, url])

        // when
        def html = new TweetPrinter().getHtml(tweet)

        // then
        assert html.contains("""scrunch <a href="https://t.co/EgufP7Th">https://t.co/EgufP7Th</a> which""")
    }
}
