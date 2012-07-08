package twitter.filter.core.model

import org.junit.Test

class SimpleProgressReporterTest {
    @Test
    void startingNewIterationShouldSetTheTotalNumberOfElementsAndIncrementTheIterationCount() {
        // given
        SimpleProgressReporter reporter = new SimpleProgressReporter()
        assert 0 == reporter.getIteration()

        // when
        reporter.startIteration(10)

        // then
        assert 1 == reporter.getIteration()
        assert 10 == reporter.getTotalElementsInIteration()
    }

    @Test
    void canTrackTheNumberOfProcessedElements() {
        // given
        SimpleProgressReporter reporter = new SimpleProgressReporter()
        reporter.startIteration(10)

        // when
        reporter.incrementProcessedElements()

        // then
        assert 1 == reporter.getIteration()
        assert 1 == reporter.getProcessedElementsInIteration()
        assert 10 == reporter.getTotalElementsInIteration()
    }

    @Test
    void theNumberOfElementsProcessedPerIterationShouldBeCappedByTheTotalElementsLimit() {
        // given
        SimpleProgressReporter reporter = new SimpleProgressReporter()
        reporter.startIteration(10)

        // when
        11.times {
            reporter.incrementProcessedElements()
        }

        // then
        assert 10 == reporter.getProcessedElementsInIteration()
        assert 10 == reporter.getTotalElementsInIteration()
    }

    @Test
    void startingNewIterationShouldResetTheElementProgressCounter() {
        // given
        SimpleProgressReporter reporter = new SimpleProgressReporter()
        reporter.startIteration(5)
        5.times { reporter.incrementProcessedElements() }

        // when
        reporter.startIteration(10)

        // then
        assert 2 == reporter.getIteration()
        assert 0 == reporter.getProcessedElementsInIteration()
        assert 10 == reporter.getTotalElementsInIteration()
    }

    @Test
    void canComputePercentageProgressPerIteration() {
        // given
        SimpleProgressReporter reporter = new SimpleProgressReporter()
        reporter.setMaxIterations(2)
        reporter.startIteration(10)

        // when
        5.times { reporter.incrementProcessedElements() }

        // then
        assert 5 == reporter.getProcessedElementsInIteration()
        assert 10 == reporter.getTotalElementsInIteration()
        assert 25 == reporter.getProgressPercentage()
    }

    @Test
    void canComputeProgressAsStringWhenOnFirstIteration() {
        // given
        SimpleProgressReporter reporter = new SimpleProgressReporter()
        reporter.setMaxIterations(2)

        // when
        reporter.startIteration(6)
        3.times { reporter.incrementProcessedElements() }

        // then
        assert 3 == reporter.getProcessedElementsInIteration()
        assert 6 == reporter.getTotalElementsInIteration()
        assert 25 == reporter.getProgressPercentage()
        assert "Page 1/2: tweets: 3/6 (total: 25.00%)" == reporter.getProgressString()
    }

    @Test
    void canComputeProgressAsStringWhenOnSecondIteration() {
        // given
        SimpleProgressReporter reporter = new SimpleProgressReporter()
        reporter.setMaxIterations(2)

        // when
        reporter.startIteration(10)
        10.times { reporter.incrementProcessedElements() }

        reporter.startIteration(5)
        1.times { reporter.incrementProcessedElements() }

        // then
        assert 1 == reporter.getProcessedElementsInIteration()
        assert 5 == reporter.getTotalElementsInIteration()
        assert 60 == reporter.getProgressPercentage()
        assert "Page 2/2: tweets: 1/5 (total: 60.00%)" == reporter.getProgressString()
    }
}
