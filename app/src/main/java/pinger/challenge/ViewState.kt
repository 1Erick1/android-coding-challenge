package pinger.challenge

sealed class ViewState{
    object Loading: ViewState()
    class Result(val sequences: List<Pair<String, Int>>): ViewState()
    class Error(val msg: String): ViewState()
    object Idle: ViewState()
}