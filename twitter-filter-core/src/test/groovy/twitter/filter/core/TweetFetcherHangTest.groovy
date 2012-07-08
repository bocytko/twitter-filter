package twitter.filter.core

import org.junit.Test

class TweetFetcherHangTest {
    private def getFileFromResource(String resource) {
        new File(ClassLoader.getSystemResource(resource).toURI())
    }

    @Test
    void canParseTweets() {
        // given
        def query = "#hadoop"
        def page = 1
        def results = 100

        TweetFetcher fetcher = new TweetFetcher(query, page, results)
        fetcher.metaClass.getJsonPayLoad = {
            def str = getFileFromResource("Groovy-5144.json").text.replaceAll("[:-]+\\\\\\\\", "")

            println str

            str
        }

        // when
        def tweets = fetcher.getTweets()

        // then
        assert 83 == tweets.size
    }
}
