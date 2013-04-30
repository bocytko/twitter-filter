package twitter.filter.core.model

import twitter.filter.core.util.ParallelUrlResolver;
import twitter.filter.core.util.UrlResolver;
import groovy.util.logging.Log4j

@Log4j("log")
abstract class UrlCache {
    abstract void put(def url, def resolvedUrl)
    abstract def get(def url)
    abstract def getValueForUnknownElement()

    void putAll(def urls, def resolvedUrls) {
        for (int i = 0; i < urls.size(); i++) {
            put(urls[i], resolvedUrls[i])
        }
    }

    def filterKnownUrls(def urls) {
        urls.findAll { get(it) == getValueForUnknownElement() }
    }

    /**
     * Retrieves all URLs from the given tweets
     * and resolves its currently unknown urls using
     * {@link ParallelUrlResolver} urlResolver.
     *
     * The resolved urls (url chain) are saved
     * as (url: url chain) into the urlCache.
     *
     * @param tweets
     * @param urlResolver
     */
    void populateWithUrlsFromTweets(def tweets, ParallelUrlResolver urlResolver) {
        def before = System.currentTimeMillis()
        log.info "Getting all URLs..."
        def urls = []
        tweets.each { urls = urls + it.urls }
        log.info "Got a total of ${urls.size()} URLs..."

        log.info "Filtering already known URLs..."
        urls = filterKnownUrls(urls)

        log.info "Got ${urls.size()} URLs. Removing duplicate urls..."
        urls = urls.sort().unique()

        log.info "Resolving ${urls.size()} unknown URLs..."
        def resolvedUrls = urlResolver.resolveUrls(urls)

        putAll(urls, resolvedUrls)

        def after = System.currentTimeMillis()
        log.info "Took ${after-before} ms"
    }
}
