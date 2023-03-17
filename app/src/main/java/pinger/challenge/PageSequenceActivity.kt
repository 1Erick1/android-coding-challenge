package pinger.challenge

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.launch

class PageSequenceActivity : AppCompatActivity() {
    private lateinit var adapter: PageSequenceAdapter
    private val viewModel: PageSequenceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        setupViews()
        setupViewModel()
    }

    private fun setupViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect{
                when(it){
                    is ViewState.Loading->{
                        changeProgressBarVisibility(true)
                    }
                    is ViewState.Error -> {
                        changeProgressBarVisibility(false)
                        showErrorMessage(it.msg)
                    }
                    is ViewState.Result -> {
                        changeProgressBarVisibility(false)
                        updatePathSequenceList(it.sequences)
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sendIntentFetchSequences()
    }

    private fun sendIntentFetchSequences() {
        lifecycleScope.launch {
            viewModel.intent.send(ViewIntent.FetchSequences)
        }
    }

    private fun setupViews() {
        repeated_path_list.layoutManager = LinearLayoutManager(this)

        fab.setOnClickListener {
            sendIntentFetchSequences()
        }
    }

    private fun changeProgressBarVisibility(visible: Boolean) {
        loading_view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun updatePathSequenceList(pageSequenceData: List<Pair<String, Int>>) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            adapter = PageSequenceAdapter(pageSequenceData, this@PageSequenceActivity)
            repeated_path_list.adapter = adapter
        }
    }

    private fun showErrorMessage(message: String) {
        Snackbar.make(parent_layout, message, Snackbar.LENGTH_LONG).show()
    }
}
