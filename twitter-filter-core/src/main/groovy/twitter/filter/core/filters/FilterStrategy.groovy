package twitter.filter.core.filters

import twitter.filter.core.Tweet

interface FilterStrategy {
    def apply(Tweet tweet)
}
