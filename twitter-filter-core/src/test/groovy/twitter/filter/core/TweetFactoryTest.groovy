package twitter.filter.core

import groovy.json.JsonSlurper

import org.junit.Test

class TweetFactoryTest {
    @Test
    void canParseTwitterJsonToTweet() {
        // given
        def twitterJsonTweets = getTweetsFromTwitterJsonText()
        def tweetText = twitterJsonTweets.results[15]

        // when
        Tweet tweet = TweetFactory.createFromTwitterJson(tweetText)

        // then
        assert tweet.from_user == "steveloughran"
        assert tweet.text == "My other computer is a personal datacentre. http://t.co/xNjseoAx stickers by @therealfitz #hadoop."
        assert tweet.urls == ["http://t.co/xNjseoAx"]
    }

    @Test
    void canCreateATweetFromTextOnly() {
        // given
        def text = "sample tweet"

        // when
        Tweet tweet = TweetFactory.createFromText(text)

        // then
        assert tweet.from_user == "nouser"
        assert tweet.text == "sample tweet"
        assert tweet.urls == []
    }

    @Test
    void canCreateATweetFromTextAndUrlListOnly() {
        // given
        def text = "sample text"
        def url = ["http://google.com"]

        // when
        Tweet tweet = TweetFactory.createFromTextAndUrl([text, url])

        // then
        assert tweet.from_user == "nouser"
        assert tweet.text == "sample text"
        assert tweet.urls == ["http://google.com"]
    }

    @Test
    void canSerializeToJsonAndRecreateTweetFromTheSerializedOutput() {
        // given
        def twitterJsonTweets = getTweetsFromTwitterJsonText()
        def tweetText = twitterJsonTweets.results[15]

        // when
        Tweet originalTweet = TweetFactory.createFromTwitterJson(tweetText)

        // when
        def jsonOutput = originalTweet.toJson()
        assert jsonOutput == """{"created_at":"Tue, 06 Dec 2011 20:04:52 +0000","text":"My other computer is a personal datacentre. http://t.co/xNjseoAx stickers by @therealfitz #hadoop.","urls":["http://t.co/xNjseoAx"],"from_user":"steveloughran","profile_image_url":"http://a1.twimg.com/profile_images/1411273327/2a64548_normal.jpg"}"""

        Tweet tweetFromJson = TweetFactory.createFromJson(jsonOutput)
        assert originalTweet == tweetFromJson
    }

    private def getTweetsFromTwitterJsonText() {
        File file = new File(ClassLoader.getSystemResource("tweets.json").toURI());
        def jsonPayload = file.text
        def slurper = new JsonSlurper()
        def tweets = slurper.parseText(jsonPayload)

        tweets
    }
}
