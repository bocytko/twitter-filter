package twitter.filter.core.model

import org.junit.Before;
import org.junit.Test;

import twitter.filter.core.Tweet
import twitter.filter.core.TweetFactory;

class MapRelatedTweetsStoreTest {
    MapRelatedTweetsStore relatedTweets

    @Before
    void before() {
        relatedTweets = new MapRelatedTweetsStore()
    }

    @Test
    void unknownTweetShouldHaveZeroRelatedTweets() {
        Tweet tweet = TweetFactory.createFromText("missing tweet")

        def result = relatedTweets.getRelatedTweets(tweet) as List

        assert result.isEmpty()
    }

    @Test
    void canRetrieveRelatedTweetsForATweet() {
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
        Tweet tweet = TweetFactory.createFromText("tweet")
        Tweet first = TweetFactory.createFromText("1. related")
        Tweet second = TweetFactory.createFromText("2. related")

        relatedTweets.add(tweet, [first, second])

        def result = relatedTweets.getRelatedTweets(tweet) as List
        assert [first, second] == result
    }

    @Test
    void canStoreAndRemoveRelatedTweets() {
        Tweet tweet = TweetFactory.createFromText("tweet")
        Tweet first = TweetFactory.createFromText("1. related")

        relatedTweets.add(tweet, first)
        relatedTweets.clear()

        def result = relatedTweets.getRelatedTweets(tweet) as List
        assert [] == result
    }
}
