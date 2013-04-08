package twitter.filter.core

import groovy.mock.interceptor.MockFor;

import org.junit.Before;
import org.junit.Test;

import twitter.filter.core.filters.DuplicateStrategy;
import twitter.filter.core.filters.FilterStrategy;
import twitter.filter.core.model.ITweetStore;
import twitter.filter.core.model.ListTweetStore

class RelatedTweetConsumerTest {
    private RelatedTweetConsumer consumer

    @Before
    void before() {
        consumer = new RelatedTweetConsumer().withTweetStore(new ListTweetStore())
    }

    @Test
    void aSingleTweetShouldHaveZeroRelatedTweets() {
        def tweetText = [
            "first",
        ]

        def tweets = tweetText.collect { TweetFactory.createFromText(it) }

        int result = consumer.consume(tweets)

        assert 1 == result
        assert [] == consumer.getRelatedTweets(consumer.tweets[0])
    }

    @Test
    void shouldFilterOutTweetsMatchingSingleFilteringStrategy() {
        def mockStrategy = new MockFor(FilterStrategy)
        mockStrategy.demand.apply(3) { it.text.contains("X") }

        def filterStrategies = [mockStrategy.proxyInstance()]
        consumer.withFilterStrategies(filterStrategies)

        def tweetText = [
            "first",
            "second X",
            "third"
        ]
        def tweets = tweetText.collect { TweetFactory.createFromText(it) }

        int result = consumer.consume(tweets)

        assert 2 == result
        assert [tweets[0], tweets[2]]== consumer.tweets
    }

    @Test
    void shouldFilterOutTweetsMatchingMultipleFilteringStrategy() {
        def mockStrategyX = new MockFor(FilterStrategy)
        def mockStrategyY = new MockFor(FilterStrategy)
        mockStrategyX.demand.apply(3) { it.text.contains("X") }
        mockStrategyY.demand.apply(2) { it.text.contains("Y") }

        def filterStrategies = [mockStrategyX.proxyInstance(), mockStrategyY.proxyInstance()]
        consumer.withFilterStrategies(filterStrategies)

        def tweetText = [
            "first",
            "second X",
            "third Y"
        ]
        def tweets = tweetText.collect { TweetFactory.createFromText(it) }

        int result = consumer.consume(tweets)

        assert 1 == result
        assert [tweets[0]]== consumer.tweets
    }

    @Test
    void shouldDetectDuplicatesBasedOnDuplicatesStrategyAndMarkThemAsRelatedTweetOfTheOriginal() {
        def tweetText = [
            "first", "first2"
        ]
        def tweets = tweetText.collect { TweetFactory.createFromText(it) }

        def mockDuplicateStrategy = new MockFor(DuplicateStrategy)
        mockDuplicateStrategy.demand.apply(2) { it.text == "first2" ? tweets[0] : null }

        def duplicateStrategies = [mockDuplicateStrategy.proxyInstance()]
        consumer.withDuplicateStrategies(duplicateStrategies)

        int result = consumer.consume(tweets)

        assert 1 == result
        assert [tweets[0]] == consumer.tweets
        assert [tweets[1]] == consumer.getRelatedTweets(consumer.tweets[0])
    }
}
