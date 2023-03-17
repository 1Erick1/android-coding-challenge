package pinger.challenge

import pinger.challenge.parsing.ApacheLogParser
import pinger.challenge.utility.PageSequenceCalculator
import io.reactivex.observers.DisposableObserver
import pinger.challenge.networking.NetworkTransactions

class PageSequencePresenter(
    var view: PageSequenceContract.View?,
    private val networkTransactions: NetworkTransactions
) : PageSequenceContract.Presenter {
    private val numberOfConsecutivePages = 3
    private val pageSequenceCalculator = PageSequenceCalculator()

    init {
        view?.setupPresenter(this)
    }

    override fun getDefaultObserver(): DisposableObserver<String> {
        return object : DisposableObserver<String>() {
            val pathSequenceList = mutableListOf<String>()

            override fun onComplete() {
                val logs = parseApacheLogs(pathSequenceList)
                calculateMostPopularPathSequences(logs)
            }

            override fun onNext(path: String) {
                pathSequenceList.add(path)
            }

            override fun onError(e: Throwable) {
                view?.changeProgressBarVisibility(false)
                view?.showErrorMessage("The request failed. Please try again.")
            }
        }
    }

    override fun fetchMostPopularPathSequences() {
        view?.changeProgressBarVisibility(true)
        //networkTransactions.downloadApacheFile(getDefaultObserver())
    }

    private fun parseApacheLogs(pathSequenceData: MutableList<String>): HashMap<String, MutableList<String>> {
        return ApacheLogParser(pathSequenceData).parseLogsForEachUser()
    }

    private fun calculateMostPopularPathSequences(parsedLogs: HashMap<String, MutableList<String>>) {
        val mostCommonPageSequences = pageSequenceCalculator.getMostCommonPageSequences(parsedLogs, numberOfConsecutivePages)
        view?.updatePathSequenceList(mostCommonPageSequences)
        view?.changeProgressBarVisibility(false)
    }

    override fun cleanUp() {
        view = null
    }
}