package twitter.filter.core.model

class SimpleProgressReporter implements IProgressReporter {
    private int iteration = 0
    private int maxIterations = 1
    private int processedElementsInIteration = 0
    private int totalElementsInIteration = 0

    @Override
    void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations
    }

    @Override
    void startIteration(int elemCount) {
        totalElementsInIteration = elemCount
        iteration++
        processedElementsInIteration = 0
    }

    @Override
    def getTotalElementsInIteration() {
        totalElementsInIteration
    }

    @Override
    def getProcessedElementsInIteration() {
        processedElementsInIteration
    }

    @Override
    def getIteration() {
        iteration
    }

    @Override
    def getProgressPercentage() {
        ((iteration - 1) + (processedElementsInIteration / totalElementsInIteration)) * (100/maxIterations)
    }

    @Override
    def getProgressString() {
        def percentage = String.format("%2.2f", getProgressPercentage())
        "Page ${iteration}/${maxIterations}: tweets: ${processedElementsInIteration}/${totalElementsInIteration} (total: ${percentage}%)"
    }

    @Override
    void incrementProcessedElements() {
        processedElementsInIteration < totalElementsInIteration ? processedElementsInIteration++ : 0
    }
}
