package twitter.filter.core.model

interface IProgressReporter {
    void setMaxIterations(int maxIterations)

    void startIteration(int elemCount)

    def getTotalElementsInIteration()

    def getProcessedElementsInIteration()

    def getIteration()

    def getProgressPercentage()

    def getProgressString()

    void incrementProcessedElements()
}
