package twitter.filter.core

import twitter.filter.core.model.IProgressReporter

public interface ITweetConsumer {
    IProgressReporter getProgressReporter()

    def getTweets()

    def getRelatedTweets(Tweet tweet)

    int consume(def tweets)
}
