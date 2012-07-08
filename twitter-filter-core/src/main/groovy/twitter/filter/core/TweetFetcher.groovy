package twitter.filter.core

import groovy.json.JsonSlurper
import groovy.transform.TupleConstructor
import groovy.util.logging.Log4j
import twitter.filter.core.view.TweetPrinter

@TupleConstructor
@Log4j("log")
class TweetFetcher {
    def query
    def page
    def results

    private def ignoredLanguages = ["ja", "fr", "es", "it"]

    def getTweets() {
        def jsonTweets = fetchJsonTweets()

        def filteredByLanguage = jsonTweets.findAll { !(it.iso_language_code in ignoredLanguages) }
        def reverseTweets = filteredByLanguage.reverse()

        def tweets = reverseTweets.collect { TweetFactory.createFromTwitterJson(it) }

        tweets
    }

    def getJsonPayLoad() {
        def encodedQuery = query.replace("#", "%23")
        def url = "http://search.twitter.com/search.json?q=${encodedQuery}&rpp=${results}&page=${page}&include_entities=true&with_twitter_user_id=true&result_type=recent"
        log.info url

        def jsonPayload = new URL(url).text
    }

    private def fetchJsonTweets() {
        def jsonPayload = getJsonPayLoad()
        log.debug "Parsing JSon output..."

        def slurper = new JsonSlurper()

        // :-\ breaks parsing JSon payload in groovy 1.8.5 (GROOVY-5144)
        def jsonText = jsonPayload.replaceAll("[:-]+\\\\\\\\", "")
        def tweets = slurper.parseText(jsonText)

        log.debug "JSon parsed"

        tweets.results
    }

    // TODO: refactor or remove
    private def printTwitterJson(def tweets) {
        tweets.results.each { println it }

        tweets.results.each {
            println it.from_user_name
            println it.profile_image_url
            println it.created_at
            println it.text
            it.entities.urls.each { println it.expanded_url }
        }

        TweetPrinter printer = new TweetPrinter()
        tweets.results.each {
            def tweet = new Tweet(it)
            printer.printHtml(tweet)
        }
    }
}
