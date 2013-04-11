package twitter.filter.core.model

import twitter.filter.core.Tweet;

interface IRelatedTweetsStore {
    def add(Tweet tweet, Tweet relatedTweet)

    def add(Tweet tweet, Collection related)

    def getRelatedTweets(Tweet tweet)
}
