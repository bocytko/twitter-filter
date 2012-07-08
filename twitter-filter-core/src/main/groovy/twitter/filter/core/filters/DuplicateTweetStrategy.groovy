package twitter.filter.core.filters

import twitter.filter.core.Tweet
import twitter.filter.core.model.ITweetStore

class DuplicateTweetStrategy implements FilterStrategy {
    private ITweetStore tweetStore

    DuplicateTweetStrategy(ITweetStore tweetStore) {
        this.tweetStore = tweetStore
    }

    @Override
    def apply(Tweet tweet) {
        def storedTweets = tweetStore.storedTweets
        storedTweets.find {
            def partialTweetLength = (it.text.length() / 2) as int
            def partialTweet = it.text.substring(0, partialTweetLength)

            tweet.text.contains(partialTweet)
        } != null
    }
}
