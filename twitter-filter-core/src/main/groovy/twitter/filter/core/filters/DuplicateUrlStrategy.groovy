package twitter.filter.core.filters

import groovy.util.logging.Log4j
import twitter.filter.core.Tweet
import twitter.filter.core.model.ITweetStore
import twitter.filter.core.model.UrlCache

@Log4j("log")
class DuplicateUrlStrategy implements FilterStrategy {
    private ITweetStore tweetStore
    private def knownUrls

    private UrlCache urlCache

    DuplicateUrlStrategy(ITweetStore tweetStore, UrlCache urlCache) {
        this.tweetStore = tweetStore
        this.knownUrls = tweetStore.getKnownUrls()
        this.urlCache = urlCache
    }

    @Override
    def apply(Tweet tweet) {
        for (def url : tweet.urls) {
            log.debug "Checking $url ..."
            try {
                new URL(url)
            } catch (MalformedURLException e) {
                log.error "${url} is malformed: ${e.message}. Skipping duplicate URL check."
                continue
            }

            // resolve url to chain of redirects
            def urlChain = urlCache.get(url)

            // any url from chain already known?
            for (def urlFromChain : urlChain) {
                if (urlFromChain in knownUrls) {
                    log.debug "$urlFromChain already known!"
                    // knownUrls = tweetStore.addToKnownUrls(urlChain)
                    return true
                }
            }

            knownUrls = tweetStore.addToKnownUrls(urlChain)
        }

        false
    }
}
