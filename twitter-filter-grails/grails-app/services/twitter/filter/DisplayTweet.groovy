package twitter.filter

import twitter.filter.core.Tweet
import groovy.transform.TupleConstructor

@TupleConstructor
class DisplayTweet {
    Tweet tweet
    def relatedTweets

    def getUser() {
        tweet.from_user
    }

    def getProfileImageUrl() {
        tweet.profile_image_url
    }

    def getText() {
        tweet.text
    }

    def getCreatedAt() {
        tweet.created_at
    }

    def getRelatedTweets() {
        relatedTweets.size()
    }
}
