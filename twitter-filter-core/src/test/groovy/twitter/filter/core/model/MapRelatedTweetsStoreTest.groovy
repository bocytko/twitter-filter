package twitter.filter.core.model

import org.junit.Test;

import twitter.filter.core.Tweet
import twitter.filter.core.TweetFactory;

class MapRelatedTweetsStoreTest {
    @Test
    void unknownTweetShouldHaveZeroRelatedTweets() {
        MapRelatedTweetsStore relatedTweets = new MapRelatedTweetsStore()
        Tweet tweet = TweetFactory.createFromText("missing tweet")

        def result = relatedTweets.getRelatedTweets(tweet) as List

        assert result.isEmpty()
    }

    @Test
    void canRetrieveRelatedTweetsForATweet() {
        MapRelatedTweetsStore relatedTweets = new MapRelatedTweetsStore()
        Tweet tweet = TweetFactory.createFromText("tweet")
        Tweet first = TweetFactory.createFromText("1. related")
        Tweet second = TweetFactory.createFromText("2. related")

        relatedTweets.add(tweet, first)
        relatedTweets.add(tweet, second)

        def result = relatedTweets.getRelatedTweets(tweet) as List
        assert [first, second] == result
    }

    @Test
    void canStoreAListOfRelatedTweets() {
        MapRelatedTweetsStore relatedTweets = new MapRelatedTweetsStore()
        Tweet tweet = TweetFactory.createFromText("tweet")
        Tweet first = TweetFactory.createFromText("1. related")
        Tweet second = TweetFactory.createFromText("2. related")

        relatedTweets.add(tweet, [first, second])

        def result = relatedTweets.getRelatedTweets(tweet) as List
        assert [first, second] == result
    }
}
