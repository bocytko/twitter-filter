package twitter.filter.core.filters

import twitter.filter.core.Tweet

interface DuplicateStrategy {
    Tweet apply(Tweet tweet)
}
