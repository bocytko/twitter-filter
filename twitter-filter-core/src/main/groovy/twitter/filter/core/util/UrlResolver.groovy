package twitter.filter.core.util

import groovy.util.logging.Log4j

@Log4j("log")
class UrlResolver {
    def urlChain = []

    def getOriginalURL(def str) {
        def result = resolve(str)

        int i = 0
        while (isNonFinalResponseCode(result[0]) && i < 10) {
            i++
            try {
                result = resolve(result[1])
            } catch(MalformedURLException e) {
                break
            } catch(UnknownHostException e) {
                break
            } catch(Exception e) {
                log.error e.message
                break
            }
        }

        result[2]
    }

    /**
     * @param str url to resolve
     * @return [response code, location, str]
     */
    private def resolve(def str) {
        log.debug "resolving $str..."
        URL url = new URL(str)

        // add only valid urls to chain
        urlChain << str

        URLConnection conn = url.openConnection()
        HttpURLConnection httpconn = conn as HttpURLConnection
        httpconn.setInstanceFollowRedirects(false)
        httpconn.setConnectTimeout(5000)
        httpconn.setReadTimeout(5000)

        def responseCode = httpconn.getResponseCode()
        def location = conn.getHeaderField("Location")

        httpconn.disconnect()

        [responseCode, location, str]
    }

    private def isNonFinalResponseCode(int httpResponseCode) {
        httpResponseCode != 200 && httpResponseCode != 404
    }
}
