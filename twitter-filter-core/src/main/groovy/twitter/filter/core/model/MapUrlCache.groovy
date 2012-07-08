package twitter.filter.core.model

class MapUrlCache extends UrlCache {
    private cache = [:]

    @Override
    void put(def url, def resolvedUrl) {
        cache[url] = resolvedUrl
    }

    @Override
    def get(def url) {
        cache[url]
    }

    @Override
    def getValueForUnknownElement() {
        null
    }
}
