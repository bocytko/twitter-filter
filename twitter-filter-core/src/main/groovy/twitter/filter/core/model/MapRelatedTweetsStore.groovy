package twitter.filter.core.model

import java.util.List
import java.util.Map

import twitter.filter.core.Tweet

class MapRelatedTweetsStore implements IRelatedTweetsStore {
    private Map<Tweet, List<Tweet> > relatedTweets = [:]

    @Override
    def add(Tweet tweet, Collection related) {
        createEmptyListForNewTweet(tweet)

        relatedTweets[tweet].addAll(related)
    }

    @Override
    def add(Tweet tweet, Tweet relatedTweet) {
        createEmptyListForNewTweet(tweet)

        relatedTweets[tweet] << relatedTweet
    }

    private void createEmptyListForNewTweet(Tweet tweet) {
        if (!relatedTweets.containsKey(tweet)) {
            relatedTweets[tweet] = []
        }
    }

    @Override
    def getRelatedTweets(Tweet tweet) {
        relatedTweets.containsKey(tweet) ? relatedTweets[tweet] : []
    }
}
