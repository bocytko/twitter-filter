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
    void canAddAndRetrieveKnownUrls() {
        // given
        def urlChain = ["A", "B", "C"]
        ITweetStore store = new ListTweetStore()

        // when
        store.addToKnownUrls(urlChain)

        assert store.knownUrls == urlChain
        assert store.numberOfKnownUrls == 3
    }
}
