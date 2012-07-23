package twitter.filter.core.filters

import twitter.filter.core.Tweet

class BlacklistedUserStrategy implements FilterStrategy {
    private def blacklistedUsers = []

    BlacklistedUserStrategy(def blacklistedUsers) {
        this.blacklistedUsers.addAll(blacklistedUsers)
    }
    
    @Override
    def apply(Tweet tweet) {
        boolean tweetFromBlacklistedUser = tweet.from_user in blacklistedUsers
        boolean tweetContainsBlacklistedUserMention = blacklistedUsers.collect { tweet.text.contains("@${it}") }.find { it == true }
        
        return tweetFromBlacklistedUser || tweetContainsBlacklistedUserMention
    }
}
