package twitter.filter.core.filters

import org.junit.Test;
import twitter.filter.core.Tweet
import twitter.filter.core.TweetFactory;

class BlacklistedUserStrategyTest {
    BlacklistedUserStrategy strategy = new BlacklistedUserStrategy(['d8Pit', 'ShomrimHatzny'])

    @Test
    void shouldAcceptTweetsFromNonBlacklistedUsers() {
        Tweet tweet = TweetFactory.createFromText("OK")

        assert false == strategy.apply(tweet)
    }

    @Test
    void shouldMatchTweetFromBlacklistedUser() {
        Tweet tweetFromBlacklistedUser = TweetFactory.createFromText("Blacklisted user")
        tweetFromBlacklistedUser.from_user = "d8Pit"

        assert true == strategy.apply(tweetFromBlacklistedUser)
    }

    @Test
    void shouldMatchTweetsContainingBlacklistedUserMentions() {
        Tweet tweetToBlacklistedUser = TweetFactory.createFromText("Blacklisted user mention @ShomrimHatzny")

        assert true == strategy.apply(tweetToBlacklistedUser)
    }
}
