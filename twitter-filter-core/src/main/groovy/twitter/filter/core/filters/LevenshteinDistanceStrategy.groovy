package twitter.filter.core.filters

import twitter.filter.core.Tweet
import twitter.filter.core.model.ITweetStore
import twitter.filter.core.util.LevenshteinDistance

class LevenshteinDistanceStrategy implements DuplicateStrategy {
    public static int LEVENSHTEIN_SIMILARITY = 28

    private ITweetStore tweetStore

    LevenshteinDistanceStrategy(ITweetStore tweetStore) {
        this.tweetStore = tweetStore
    }

    /**
     * @param tweet
     * @return the original tweet, to which the checked tweet
     *         has a LevenshteinDistance < LEVENSHTEIN_SIMILARITY% of the tweet's length.
     */
    @Override
    Tweet apply(Tweet tweet) {
        def storedTweets = tweetStore.storedTweets
        def storedTweetsSize = storedTweets.size()

        int length = tweet.text.length()
        for (int i = storedTweetsSize - 1; i >= 0; i--) {
            def distance = LevenshteinDistance.computeDistance(storedTweets[i].text, tweet.text)

            if (distance * 100.0 < LEVENSHTEIN_SIMILARITY * length) {
                return storedTweets[i]
            }
        }

        null
    }
}
