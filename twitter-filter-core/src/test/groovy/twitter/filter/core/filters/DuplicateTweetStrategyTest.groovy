package twitter.filter.core.filters

import groovy.mock.interceptor.MockFor;

import org.junit.Test;

import twitter.filter.core.Tweet;
import twitter.filter.core.model.ITweetStore;

class DuplicateTweetStrategyTest {
    @Test
    void aTweetWithContainingASubstringOfTheOriginalTweetShouldBeMarkedAsDuplicate() {
        def tweet = new Tweet()
        tweet.from_user = "user"
        tweet.text = "tweet textlong text"

        def newTweet = new Tweet()
        newTweet.from_user = "user2"
        newTweet.text = "tweet text"

        def tweetStoreMock = new MockFor(ITweetStore.class)
        tweetStoreMock.demand.getStoredTweets(1) { [ tweet ] }

        DuplicateTweetStrategy strategy = new DuplicateTweetStrategy(tweetStoreMock.proxyInstance())

        assert tweet == strategy.apply(newTweet)
    }

    @Test
    void theSameTweetShallBeItsOwnDuplicate() {
        def tweet = new Tweet()
        tweet.from_user = "user"
        tweet.text = "tweet textlong text"

        def newTweet = new Tweet()
        newTweet.from_user = "user"
        newTweet.text = "tweet textlong text"

        def tweetStoreMock = new MockFor(ITweetStore.class)
        tweetStoreMock.demand.getStoredTweets(1) { [ tweet ] }

        DuplicateTweetStrategy strategy = new DuplicateTweetStrategy(tweetStoreMock.proxyInstance())

        assert tweet == strategy.apply(newTweet)
    }
}
