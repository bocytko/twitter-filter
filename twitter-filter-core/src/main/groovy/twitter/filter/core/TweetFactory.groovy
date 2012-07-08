package twitter.filter.core

import groovy.json.JsonSlurper

class TweetFactory {
    private static final String NOUSER = "nouser"

    static Tweet createFromText(def text) {
        new Tweet(NOUSER, null, null, text, [])
    }

    static Tweet createFromTextAndUrl(def textAndUrl) {
        new Tweet(NOUSER, null, null, textAndUrl[0], textAndUrl[1])
    }

    static Tweet createFromTwitterJson(def map) {
        new Tweet(map.from_user, map.created_at, map.profile_image_url, map.text, map.entities.urls.collect { it.url })
    }

    static Tweet createFromJson(def json) {
        def jsonSlurper = new JsonSlurper()
        def obj = jsonSlurper.parseText(json)

        new Tweet(obj.from_user, obj.created_at, obj.profile_image_url, obj.text, obj.urls)
    }
}
