package twitter.filter.core.model

import org.junit.Test

import twitter.filter.core.Tweet
import twitter.filter.core.TweetFactory

class ListTweetStoreTest {
    @Test
    void canAddAndRetrieveTweets() {
        // given
        Tweet first = TweetFactory.createFromText("first")
        Tweet second = TweetFactory.createFromText("second")
        ITweetStore store = new ListTweetStore()

        // when
        store.storeTweet(first)
        store.storeTweet(first)
        store.storeTweet(second)

        // then
        assert store.storedTweets.size() == 3
        assert store.storedTweets == [first, first, second]
        assert store.numberOfStoredTweets == 3
    }

    @Test
    void canRemoveTweetsMatchingCondition() {
        // given
        Tweet first = TweetFactory.createFromText("first")
        Tweet second = TweetFactory.createFromText("second")
        ITweetStore store = new ListTweetStore()

        store.storeTweet(first)
        store.storeTweet(first)
        store.storeTweet(second)

        // when
        def tweetWithTextSecond = { Tweet t -> t.text == "second" }
        store.removeTweets(tweetWithTextSecond)

        // then
        assert store.storedTweets.size() == 2
        assert store.storedTweets == [first, first]
        assert store.numberOfStoredTweets == 2
    }

    @Test
    void canRetrieveTweetByUrl() {
        // given
        Tweet first = TweetFactory.createFromText("first")
        first.urls = ["A"]
        ITweetStore store = new ListTweetStore()

        // when
        store.storeTweet(first)

        // then
        assert first == store.getTweetForUrl("A")
    }
}
