package twitter.filter.core.filters

import twitter.filter.core.Tweet
import twitter.filter.core.model.ITweetStore
import twitter.filter.core.util.LevenshteinDistance

class LevenshteinDistanceStrategy implements FilterStrategy {
    public static int LEVENSHTEIN_SIMILARITY = 28

    private ITweetStore tweetStore

    LevenshteinDistanceStrategy(ITweetStore tweetStore) {
        this.tweetStore = tweetStore
    }

    /**
     * @param tweet
     * @return true in case the consumer already contains
     *         a similar tweet with a LevenshteinDistance < LEVENSHTEIN_SIMILARITY% of the tweets length.
     */
    @Override
    def apply(Tweet tweet) {
        def storedTweets = tweetStore.storedTweets
        def storedTweetsSize = storedTweets.size()

        int length = tweet.text.length()
        for (int i = storedTweetsSize - 1; i >= 0; i--) {
            def distance = LevenshteinDistance.computeDistance(storedTweets[i].text, tweet.text)

            if (distance * 100.0 < LEVENSHTEIN_SIMILARITY * length) {
                return true
            }
        }

        false
    }
}
