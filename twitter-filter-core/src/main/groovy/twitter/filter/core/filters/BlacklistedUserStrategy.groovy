package twitter.filter.core.filters

import twitter.filter.core.Tweet

class BlacklistedUserStrategy implements FilterStrategy {
    private def blacklistedUsers = []

    BlacklistedUserStrategy(def blacklistedUsers) {
        this.blacklistedUsers.addAll(blacklistedUsers)
    }
    
    @Override
    def apply(Tweet tweet) {
        tweet.from_user in blacklistedUsers
    }
}
