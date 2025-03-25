package com.example.playlistmaker.presentation.ui.searchs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.ui.audioPlayer.AudioPlayerActivity
import com.example.playlistmaker.presentation.Creator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    // UseCases
    private val searchUseCase by lazy { Creator.provideSearchTracksUseCase(this) }
    private val historyUseCase by lazy { Creator.provideSearchHistoryUseCase(this) }

    // UI элементы
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var retryButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var historyTitle: TextView
    private lateinit var clearHistoryButton: Button

    // Адаптеры
    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    // Обработчик задержки поиска
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        setupAdapters()
        setupListeners()
        restoreState(savedInstanceState)
        toggleHistoryVisibility()
    }

    private fun initViews() {
        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearButton)
        recyclerView = findViewById(R.id.recyclerView)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        placeholderImage = findViewById(R.id.placeholderMessageError)
        placeholderText = findViewById(R.id.placeholderTextView)
        retryButton = findViewById(R.id.retryButton)
        progressBar = findViewById(R.id.progressBar)
        historyTitle = findViewById(R.id.historyHeader)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
    }

    private fun setupAdapters() {
        // Адаптер для результатов поиска
        searchAdapter = TrackAdapter(emptyList()) { track ->
            if (isClickAllowed) {
                isClickAllowed = false
                handler.postDelayed({ isClickAllowed = true }, 1000)
                historyUseCase.addTrack(track)
                openAudioPlayer(track)
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = searchAdapter

        // Адаптер для истории поиска
        historyAdapter = TrackAdapter(emptyList()) { track ->
            if (isClickAllowed) {
                isClickAllowed = false
                handler.postDelayed({ isClickAllowed = true }, 1000)

                historyUseCase.addTrack(track)
                toggleHistoryVisibility()
                openAudioPlayer(track)
            }
        }
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter
    }

    private fun setupListeners() {
        // Обработчик ввода текста
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                searchRunnable?.let { handler.removeCallbacks(it) }

                if (s.isNullOrEmpty()) {
                    toggleHistoryVisibility()
                } else {
                    searchRunnable = Runnable { performSearch(s.toString()) }
                    handler.postDelayed(searchRunnable!!, 500)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            hideKeyboard()

            searchAdapter.updateTracks(emptyList())
            recyclerView.visibility = View.GONE
            hidePlaceholder()
            toggleHistoryVisibility()
        }

        retryButton.setOnClickListener {
            performSearch(searchEditText.text.toString())
        }

        clearHistoryButton.setOnClickListener {
            historyUseCase.clearHistory()
            toggleHistoryVisibility()
        }

        findViewById<Button>(R.id.backButton).setOnClickListener {
            finish()
        }
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) return

        historyTitle.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
        historyRecyclerView.visibility = View.GONE

        progressBar.visibility = View.VISIBLE
        hidePlaceholder()

        CoroutineScope(Dispatchers.Main).launch {
            searchUseCase.execute(query).fold(
                onSuccess = { tracks ->
                    progressBar.visibility = View.GONE
                    if (tracks.isEmpty()) {
                        showPlaceholder(
                            getString(R.string.search_nothing),
                            R.drawable.placeholder_no_results,
                            showRetryButton = false
                        )
                    } else {
                        searchAdapter.updateTracks(tracks)
                        recyclerView.visibility = View.VISIBLE
                    }
                },
                onFailure = { error ->
                    progressBar.visibility = View.GONE
                    showPlaceholder(
                        getString(R.string.search_error),
                        R.drawable.placeholder_server_error,
                        showRetryButton = true
                    )
                }
            )
        }
    }

    private fun showPlaceholder(message: String, iconRes: Int, showRetryButton: Boolean) {
        placeholderImage.setImageResource(iconRes)
        placeholderText.text = message
        placeholderImage.visibility = View.VISIBLE
        placeholderText.visibility = View.VISIBLE
        retryButton.visibility = if (showRetryButton) View.VISIBLE else View.GONE
        recyclerView.visibility = View.GONE
    }

    private fun hidePlaceholder() {
        placeholderImage.visibility = View.GONE
        placeholderText.visibility = View.GONE
        retryButton.visibility = View.GONE
    }

    private fun toggleHistoryVisibility() {
        val history = historyUseCase.getHistory()
        val hasSearchText = !searchEditText.text.isNullOrEmpty()

        if (history.isEmpty()) {
            historyTitle.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
            historyRecyclerView.visibility = View.GONE
        } else if (hasSearchText) {
            historyTitle.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
            historyRecyclerView.visibility = View.GONE
        } else {
            historyTitle.visibility = View.VISIBLE
            clearHistoryButton.visibility = View.VISIBLE
            historyRecyclerView.visibility = View.VISIBLE
            historyAdapter.updateTracks(history)
        }
    }

    private fun openAudioPlayer(track: Track) {
        val intent = Intent(this, AudioPlayerActivity::class.java).apply {
            putExtra("TRACK", track)
        }
        startActivity(intent)
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        savedInstanceState?.getString("SEARCH_QUERY")?.let {
            searchEditText.setText(it)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SEARCH_QUERY", searchEditText.text.toString())
    }
}