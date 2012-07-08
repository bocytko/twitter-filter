package twitter.filter.core.util

import org.junit.Test

class LevenshteinDistanceTest {
    @Test
    void canComputeDistance() {
        def tweets = [
            "RT @DataJunkie: I have 3 map/reduce jobs in a chain. Can I lower the number of parallel map tasks for one of jobs? setNumMapTasks(1) doesn't work. #hadoop",
            "I have 3 map/reduce jobs in a chain. Can I lower the number of parallel map tasks for one of jobs? setNumMapTasks(1) doesn't work. #hadoop"
        ]

        assert LevenshteinDistance.computeDistance("aa", "bb") == 2
        assert LevenshteinDistance.computeDistance("aa", "ab") == 1
        assert LevenshteinDistance.computeDistance("aa", "aab") == 1
        assert LevenshteinDistance.computeDistance("kitten", "sitting") == 3
        assert LevenshteinDistance.computeDistance("Sunday", "Saturday") == 3
        assert LevenshteinDistance.computeDistance(tweets[0], tweets[1]) == 16
    }
}
