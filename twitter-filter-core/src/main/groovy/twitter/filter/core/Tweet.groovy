package twitter.filter.core

import groovy.json.JsonBuilder
import groovy.transform.Canonical

@Canonical
class Tweet {
    def from_user
    def created_at
    def profile_image_url
    def text
    def urls

    String toJson() {
        def json = new JsonBuilder(this)

        json
    }
}
