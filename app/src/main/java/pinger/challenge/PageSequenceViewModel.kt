package pinger.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pinger.challenge.networking.NetworkTransactions
import pinger.challenge.parsing.ApacheLogParser
import pinger.challenge.utility.PageSequenceCalculator

class PageSequenceViewModel(
): ViewModel() {
    private val networkTransactions: NetworkTransactions = NetworkTransactions()
    private val numberOfConsecutivePages = 3
    private val pageSequenceCalculator = PageSequenceCalculator()

    private val _state = MutableStateFlow<ViewState>(ViewState.Idle)
    val state : StateFlow<ViewState> = _state

    val intent = Channel<ViewIntent>(Channel.UNLIMITED)

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            intent.consumeAsFlow()
                .collect{
                    when(it){
                        is ViewIntent.FetchSequences -> {
                            fetchMostPopularPathSequences()
                        }
                    }
                }
        }
    }

    private fun fetchMostPopularPathSequences(){
        viewModelScope.launch {
            _state.value = ViewState.Loading
            val pathSequenceList = mutableListOf<String>()
            networkTransactions.downloadApacheFile()
                .onEach {
                    pathSequenceList.add(it)
                }
                .onCompletion {
                    val logs = parseApacheLogs(pathSequenceList)
                    calculateMostPopularPathSequences(logs)
                }.launchIn(this)
        }
    }

    private fun parseApacheLogs(pathSequenceData: MutableList<String>): HashMap<String, MutableList<String>>{
        return ApacheLogParser(pathSequenceData).parseLogsForEachUser()
    }

    private fun calculateMostPopularPathSequences(parsedLogs: HashMap<String, MutableList<String>>) {
        val mostCommonPageSequences = pageSequenceCalculator.getMostCommonPageSequences(parsedLogs, numberOfConsecutivePages)
        _state.value = ViewState.Result(mostCommonPageSequences)
    }

}