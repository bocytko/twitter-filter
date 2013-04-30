package twitter.filter.core.filters

import twitter.filter.core.Tweet
import twitter.filter.core.model.ITweetStore

class DuplicateTweetStrategy implements DuplicateStrategy {
    private ITweetStore tweetStore

    DuplicateTweetStrategy(ITweetStore tweetStore) {
        this.tweetStore = tweetStore
    }

    @Override
    Tweet apply(Tweet tweet) {
        def storedTweets = tweetStore.storedTweets
        storedTweets.find {
            def partialTweetLength = (it.text.length() / 2) as int
            def partialTweet = it.text.substring(0, partialTweetLength)

            tweet.text.contains(partialTweet)
        }
    }
}
