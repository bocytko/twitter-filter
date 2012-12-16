package twitter.filter.core

import org.junit.Before
import org.junit.Test

import twitter.filter.core.filters.BlacklistedUserStrategy
import twitter.filter.core.filters.DuplicateTweetStrategy
import twitter.filter.core.filters.DuplicateUrlStrategy
import twitter.filter.core.filters.LevenshteinDistanceStrategy
import twitter.filter.core.model.IProgressReporter
import twitter.filter.core.model.ListTweetStore
import twitter.filter.core.model.MapUrlCache
import twitter.filter.core.model.SimpleProgressReporter

class TweetConsumerTest {
    private TweetConsumer consumer
    def tweetStore = new ListTweetStore()
    def urlCache = new MapUrlCache()
    def threads = 10

    @Before
    void before() {
        def filterStrategies = [
            new BlacklistedUserStrategy(['d8Pit', 'HBaselog', 'HatzolahNYC', 'ShomrimHatzny']),
            new DuplicateUrlStrategy(tweetStore, urlCache),
            // new DuplicateTweetStrategy(tweetStore)
            new LevenshteinDistanceStrategy(tweetStore)
        ]

        // TODO: mock
        IProgressReporter progressReporter = new SimpleProgressReporter()

        consumer = new TweetConsumer()
                .withTweetStore(tweetStore)
                .withUrlCache(urlCache)
                .withThreads(threads)
                .withFilterStrategies(filterStrategies)
                .withProgressReporter(progressReporter)
    }

    @Test
    void shouldIgnoreRetweets() {
        def tweetText = [
            "Amigos,tengo un nuevo cliente: @Cachinko ! http://t.co/9z8y2EaD, busca desarrolladores #AJAX, #Python, #Mongo, #Hadoop, #Net. Proyecto #DF",
            "RT @DonChambitas: Amigos,tengo un nuevo cliente: @Cachinko ! http://t.co/9z8y2EaD, busca desarrolladores #AJAX, #Python, #Mongo, #Hadoop, #Net. Proyecto #DF",
        ]

        def tweets = tweetText.collect { TweetFactory.createFromText(it) }

        assert consumer.consume(tweets) == 1

        assert consumer.getTweets() == [
            tweets[0]
        ]
    }

    @Test
    void shouldIgnoreDuplicatedTweets() {
        def tweetText = [
            "I have 3 map/reduce jobs in a chain. Can I lower the number of parallel map tasks for one of jobs? setNumMapTasks(1) doesn't work. #hadoop",
            "RT @DataJunkie: I have 3 map/reduce jobs in a chain. Can I lower the number of parallel map tasks for one of jobs? setNumMapTasks(1) doesn't work. #hadoop",
            "Amigos,tengo un nuevo cliente: @Cachinko ! http://t.co/9z8y2EaD, busca desarrolladores #AJAX, #Python, #Mongo, #Hadoop, #Net. Proyecto #DF",
            "RT @patrickDurusau: Hadapt is moving forward #topicmaps #hadoop #unstructured #sql #splitquery - http://t.co/cBtxNshp",
            "RT @patrickDurusau: Hadapt is moving forward #topicmaps #hadoop #unstructured #sql #splitquery - http://t.co/cBtxNshp",
            "RT @DonChambitas: Amigos,tengo un nuevo cliente: @Cachinko ! http://t.co/9z8y2EaD, busca desarrolladores #AJAX, #Python, #Mongo, #Hadoop, #Net. Proyecto #DF",
            "RT @DonChambitas: Amigos,tengo un nuevo cliente: @Cachinko ! http://t.co/9z8y2EaD, busca desarrolladores #AJAX, #Python, #Mongo, #Hadoop, #Net. Proyecto #DF",
            "I just got MR2 running on HA HDFS. Killed active NN under a running MR job and the job kept going. Beautiful thing. #hadoop",
            "You're my hero. RT: @atm: I just got MR2 running on HA HDFS. Killed active NN under a running MR job and the job kept going. Beaut...#hadoop",
            "The year in #bigdata & data science: #Hadoop, security & open data http://t.co/UjYVGRz3 via @radar #datascience",
            "RT @strataconf:  The year in #bigdata & data science: #Hadoop, security & open data  via @radar #datascience | http://t.co/LHo2GmJ1"
        ];

        def tweets = tweetText.collect { TweetFactory.createFromText(it) }

        assert consumer.consume(tweets) == 6

        assert consumer.getTweets() == [
            tweets[0],
            tweets[2],
            tweets[3],
            tweets[7],
            tweets[9],
            tweets[10]
        ]
    }

    @Test
    void shouldIgnoreTweetsLinkingToSameAddress() {
        def tweetText = [
            [
                "Amigos,tengo un nuevo cliente: @Cachinko ! http://t.co/9z8y2EaD, busca desarrolladores #AJAX, #Python, #Mongo, #Hadoop, #Net. Proyecto #DF",
                [
                    "http://t.co/9z8y2EaD"]
            ],
            [
                "My favorite link today: http://t.co/9z8y2EaD",
                [
                    "http://t.co/9z8y2EaD"]
            ],
        ];

        def tweets = tweetText.collect { TweetFactory.createFromTextAndUrl(it) }

        assert 1 == consumer.consume(tweets)

        assert consumer.getTweets() == [
            tweets[0]
        ]
    }

    @Test
    void shouldIgnoreTweetsLinkingIndirectlyToSameAddress() {
        def tweetText = [
            [
                "Pleased to see the Pig UDFs released by LinkedIn SNA team: http://t.co/ul1W8mgx #in #hadoop",
                [
                    "http://t.co/ul1W8mgx"]
            ],
            [
                "Worth keeping in mind: datafu is LinkedIn's collection of #Pig UDFs for Statistics and Data Mining http://t.co/7Jlum0R5 - #hadoop",
                ["http://t.co/7Jlum0R5"]]
        ]

        def tweets = tweetText.collect { TweetFactory.createFromTextAndUrl(it) }

        assert consumer.consume(tweets) == 1

        assert consumer.getTweets() == [
            tweets[0]
        ]
    }

    @Test
    void shouldIgnoreDuplicatedTweetsWithDifferentLinks() {
        def tweetText = [
            [
                """My weekend hack: "Analysing patterns of my Tweets using #Pig on #Hadoop" #BigData https://t.co/3bqrm34u http://t.co/0M5tZTxc""",
                [
                    "https://t.co/3bqrm34u",
                    "http://t.co/0M5tZTxc"
                ]
            ],
            [
                """RT @P7h: My weekend hack: "Analysing patterns of my Tweets using #Pig on #Hadoop" #BigData  http://t.co/2WCacgY2 | http://t.co/hrLnl7fW""",
                [
                    "http://t.co/2WCacgY2",
                    "http://t.co/hrLnl7fW"
                ]
            ],
        ]

        def tweets = tweetText.collect { TweetFactory.createFromTextAndUrl(it) }

        assert consumer.consume(tweets) == 1

        assert consumer.getTweets() == [
            tweets[0],
        ]
    }

    @Test
    void shouldIgnoreAllTweetsInSecondConsumeRunIfTheyHaveBeenConsumedOnce() {
        def tweetText = [
            [
                """My weekend hack: "Analysing patterns of my Tweets using #Pig on #Hadoop" #BigData https://t.co/3bqrm34u http://t.co/0M5tZTxc""",
                [
                    "https://t.co/3bqrm34u",
                    "http://t.co/0M5tZTxc"
                ]
            ],
            [
                """RT @P7h: My weekend hack: "Analysing patterns of my Tweets using #Pig on #Hadoop" #BigData  http://t.co/2WCacgY2 | http://t.co/hrLnl7fW""",
                [
                    "http://t.co/2WCacgY2",
                    "http://t.co/hrLnl7fW"
                ]
            ],
        ]

        def tweets = tweetText.collect { TweetFactory.createFromTextAndUrl(it) }

        assert consumer.consume(tweets) == 1

        assert consumer.getTweets() == [
            tweets[0],
        ]

        assert consumer.consume(tweets) == 0
        assert consumer.getTweets() == [
            tweets[0],
        ]
    }

    @Test
    void shouldIgnoreAllTweetsFromBlacklistedUsers() {
        Tweet tweetFromBlacklistedUser = TweetFactory.createFromText("Blacklisted user")
        tweetFromBlacklistedUser.from_user = "d8Pit"

        Tweet tweet = TweetFactory.createFromText("OK")

        def tweets = [
            tweetFromBlacklistedUser,
            tweet
        ]

        assert consumer.consume(tweets) == 1
        assert consumer.getTweets() == [
            tweet
        ]
    }

    @Test
    void shouldIgnoreTweetsContainingBlacklistedUserMentions() {
        Tweet tweetFromBlacklistedUser = TweetFactory.createFromText("Blacklisted user")
        tweetFromBlacklistedUser.from_user = "ShomrimHatzny"

        Tweet tweetToBlacklistedUser = TweetFactory.createFromText("Blacklisted user mention @ShomrimHatzny")

        Tweet tweet = TweetFactory.createFromText("OK")

        def tweets = [
            tweetFromBlacklistedUser,
            tweetToBlacklistedUser,
            tweet
        ]

        assert consumer.consume(tweets) == 1
        assert consumer.getTweets() == [
            tweet
        ]
    }
}
