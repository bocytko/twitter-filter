package twitter.filter.core.filters

import groovy.util.logging.Log4j
import twitter.filter.core.Tweet
import twitter.filter.core.model.ITweetStore
import twitter.filter.core.model.UrlCache

@Log4j("log")
class DuplicateUrlStrategy implements DuplicateStrategy {
    private ITweetStore tweetStore
    private UrlCache urlCache

    DuplicateUrlStrategy(ITweetStore tweetStore, UrlCache urlCache) {
        this.tweetStore = tweetStore
        this.urlCache = urlCache
    }

    @Override
    Tweet apply(Tweet tweet) {
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

            // any url from chain already referenced by another tweet?
            for (def urlFromChain : urlChain) {
                Tweet referencedTweet = tweetStore.getTweetForUrl(urlFromChain)
                if (referencedTweet != null) {
                    log.debug "$urlFromChain already known!"
                    return referencedTweet
                }
            }
        }

        null
    }
}
