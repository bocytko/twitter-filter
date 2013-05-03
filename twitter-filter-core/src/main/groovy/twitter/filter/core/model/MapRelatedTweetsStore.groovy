package twitter.filter.core.model

import twitter.filter.core.Tweet

class MapRelatedTweetsStore implements IRelatedTweetsStore {
    private Map<Tweet, List<Tweet> > relatedTweets = [:]

    @Override
    def add(Tweet tweet, Collection related) {
        addEmptyListForNewTweet(tweet)

        relatedTweets[tweet].addAll(related)
    }

    @Override
    def add(Tweet tweet, Tweet relatedTweet) {
        addEmptyListForNewTweet(tweet)

        relatedTweets[tweet] << relatedTweet
    }

    private void addEmptyListForNewTweet(Tweet tweet) {
        if (!relatedTweets.containsKey(tweet)) {
            relatedTweets[tweet] = []
        }
    }

    @Override
    def getRelatedTweets(Tweet tweet) {
        relatedTweets.containsKey(tweet) ? relatedTweets[tweet] : []
    }

    @Override
    void clear() {
        relatedTweets.clear()
    }
}
