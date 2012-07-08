package twitter.filter.core.util

import groovy.transform.TupleConstructor
import groovy.util.logging.Log4j
import groovyx.gpars.GParsPool

@Log4j("log")
@TupleConstructor
class ParallelUrlResolver {
    int threads

    def resolveUrls(def urls) {
        if (threads < 2) {
            urls.collect resolveUrl
        } else {
            GParsPool.withPool(threads) { urls.collectParallel resolveUrl }
        }
    }

    private def resolveUrl = {
        UrlResolver resolver = new UrlResolver()
        try {
            resolver.getOriginalURL(it)
            resolver.urlChain
        } catch(MalformedURLException e) {
            log.error "${it} is malformed"
            return []
        } catch(UnknownHostException e) {
            log.error "${it} is unknown"
            return []
        }
    }
}
