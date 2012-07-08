package twitter.filter.core.model

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
}
