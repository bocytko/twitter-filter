package twitter.filter.core

import groovy.util.logging.Log4j
import twitter.filter.core.view.TweetPrinter

@Log4j("log")
class TweetFilter {
    private ITweetConsumer consumer

    def withTweetConsumer(ITweetConsumer consumer) {
        this.consumer = consumer

        this
    }

    // TODO: build()

    def doFilter(def query, def pages) {
        int addedTweets = 0
        def results = 100

        consumer.getProgressReporter()?.setMaxIterations(pages)

        for (; pages > 0; pages--) {
            TweetFetcher fetcher = new TweetFetcher(query, pages, results)
            def tweets = fetcher.getTweets()

            def numberOfConsumedTweets = consumer.consume(tweets)
            addedTweets += numberOfConsumedTweets
        }

        addedTweets
    }

    def printLastNTweets(def numberOfConsumedTweets) {
        if (numberOfConsumedTweets == 0) {
            log.info "No new tweets to print!"
            return
        }

        log.info "Printing ${numberOfConsumedTweets} tweets:"
        def filteredTweets = consumer.getTweets()
        def lastNTweets = filteredTweets[filteredTweets.size() - numberOfConsumedTweets .. filteredTweets.size() - 1]

        TweetPrinter printer = new TweetPrinter()
        lastNTweets.each { printer.printHtml(it, consumer.getRelatedTweets(it)) }
    }
}
