package twitter.filter.core.filters

import groovy.mock.interceptor.MockFor;

import org.junit.Test;

import twitter.filter.core.Tweet;
import twitter.filter.core.TweetFactory;
import twitter.filter.core.model.ITweetStore;

class DuplicateTweetStrategyTest {
    @Test
    void twoTweetsWithDifferentTextShallNotBeDuplicatesOfEachOther() {
        def tweet = TweetFactory.createFromText("foo")
        def newTweet = TweetFactory.createFromText("bar")

        def tweetStoreMock = new MockFor(ITweetStore.class)
        tweetStoreMock.demand.getStoredTweets(1) { [ tweet ] }

        DuplicateTweetStrategy strategy = new DuplicateTweetStrategy(tweetStoreMock.proxyInstance())

        assert null == strategy.apply(newTweet)
    }

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

    @Test
    void reTweetsShouldBeDuplicatesOfTheOriginalTweet() {
        def tweetText = [
            """My weekend hack: "Analysing patterns of my Tweets using #Pig on #Hadoop" #BigData https://t.co/3bqrm34u http://t.co/0M5tZTxc""",
            [
                "https://t.co/3bqrm34u",
                "http://t.co/0M5tZTxc"
            ]
        ]

        def retweetText = [
            """RT @P7h: My weekend hack: "Analysing patterns of my Tweets using #Pig on #Hadoop" #BigData  http://t.co/2WCacgY2 | http://t.co/hrLnl7fW""",
            [
                "http://t.co/2WCacgY2",
                "http://t.co/hrLnl7fW"
            ]
        ]

        def tweet = TweetFactory.createFromTextAndUrl(tweetText)
        def retweet = TweetFactory.createFromTextAndUrl(retweetText)

        def tweetStoreMock = new MockFor(ITweetStore.class)
        tweetStoreMock.demand.getStoredTweets(1) { [ tweet ] }

        DuplicateTweetStrategy strategy = new DuplicateTweetStrategy(tweetStoreMock.proxyInstance())

        assert tweet == strategy.apply(retweet)
    }

    @Test
    void shouldIgnoreDuplicatedTweetsWithDifferentLinks() {
        def tweetText = [
            """My weekend hack: "Analysing patterns of my Tweets using #Pig on #Hadoop" #BigData https://t.co/3bqrm34u http://t.co/0M5tZTxc""",
            [
                "https://t.co/3bqrm34u",
                "http://t.co/0M5tZTxc"
            ]
        ]

        def retweetText = [
            """RT @P7h: My weekend hack: "Analysing patterns of my Tweets using #Pig on #Hadoop" #BigData  http://t.co/2WCacgY2 | http://t.co/hrLnl7fW""",
            [
                "http://t.co/2WCacgY2",
                "http://t.co/hrLnl7fW"
            ]
        ]

        def tweet = TweetFactory.createFromTextAndUrl(tweetText)
        def retweet = TweetFactory.createFromTextAndUrl(retweetText)

        def tweetStoreMock = new MockFor(ITweetStore.class)
        tweetStoreMock.demand.getStoredTweets(1) { [ tweet ] }

        DuplicateTweetStrategy strategy = new DuplicateTweetStrategy(tweetStoreMock.proxyInstance())

        assert tweet == strategy.apply(retweet)
    }
}
