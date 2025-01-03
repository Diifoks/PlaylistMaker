package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.widget.ProgressBar

class SearchActivity : AppCompatActivity() {
    // Переменные для сохранения текста
    private lateinit var searchEditText: EditText
    private var searchText: String = ""
    // Переменные для списка треков
    private lateinit var trackList: ArrayList<Track>
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var iTunesService: iTunesApi
    // Переменные для ошибок
    private lateinit var placeholderMessageError: ImageView
    private lateinit var placeholderTextView: TextView
    private lateinit var retryButton: Button
    private lateinit var recyclerView: RecyclerView
    // Для истории поиска
    private lateinit var searchHistory: SearchHistory
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var clearHistoryButton: Button
    private lateinit var historyHeader: TextView
    // Для отображения индикации поиска
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Инициализация элементов интерфейса
        placeholderMessageError = findViewById(R.id.placeholderMessageError)
        placeholderTextView = findViewById(R.id.placeholderTextView)
        retryButton = findViewById(R.id.retryButton)
        recyclerView = findViewById(R.id.recyclerView)
        searchEditText = findViewById(R.id.searchEditText)
        val clearButton = findViewById<ImageView>(R.id.clearButton)

        // Инициализация истории поиска
        searchHistory = SearchHistory(getSharedPreferences("PlaylistMakerPrefs", Context.MODE_PRIVATE))
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        historyHeader = findViewById(R.id.historyHeader)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)

        // Инициализация адаптера истории поиска
        historyAdapter = TrackAdapter(searchHistory.getHistory())
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter

        // Инициализация идикатора поиска
        progressBar = findViewById(R.id.progressBar)

        // Кнопка обновления поиска
        retryButton.setOnClickListener {
            searchTracks(searchText)
        }

        // Кнопка возврата на главный экран
        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        // Установка обработчика нажатия на кнопку "Очистить поисковый запрос"
        clearButton.setOnClickListener {
            searchEditText.setText("")
            clearButton.visibility = View.GONE
            // Скрытие клавиатуры
            hideKeyboard()
            // Скрыть трек лист
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            // Скрытие сообщений об ошибках
            hidePlaceholder()
            // Обновление видимости истории
            toggleHistoryVisibility()
        }

        // Обработка done на клавиатуре
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTracks(searchEditText.text.toString())
                true
            } else {
                false
            }
        }

        // Создание TextWatcher для отслеживания изменений в поисковой строке
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                // Логика для изменения поля ввода
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                // Очищаем предыдущие запланированные действия
                searchRunnable?.let { handler.removeCallbacks(it) }
                // Если поле ввода пустое, очищаем список треков и обновляем видимость истории
                if (s.isNullOrEmpty()) {
                    trackList.clear() // Очистка списка треков
                    trackAdapter.notifyDataSetChanged() // Уведомление адаптера об изменениях
                    // Обновляем видимость истории при изменении текста
                    toggleHistoryVisibility()
                    hidePlaceholder()
                }else {
                    searchRunnable = Runnable {
                        searchTracks(searchText)
                    }
                    handler.postDelayed(searchRunnable!!, 500) // Задержка 500 мс
                }
            }

            // При изменении текста
            override fun afterTextChanged(s: Editable?) {}
        }

        // Установка TextWatcher для поля ввода поисковой строки
        searchEditText.addTextChangedListener(textWatcher)

        // Установка фокуса на поле ввода
        searchEditText.requestFocus()

        // Инициализация списка треков
        trackList = arrayListOf()
        recyclerView.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter(trackList)
        recyclerView.adapter = trackAdapter

        // Инициализация Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        iTunesService = retrofit.create(iTunesApi::class.java)

        // Обработчик нажатия на трек
//        trackAdapter.setOnTrackClickListener { track ->
//            searchHistory.addTrack(track)
//            historyAdapter.notifyDataSetChanged() // Обновление адаптера истории
//            Toast.makeText(this, "Трек добавлен в историю", Toast.LENGTH_SHORT).show()
//            toggleHistoryVisibility()
//
//            val intent = Intent(this, AudioPlayerActivity::class.java)
//            intent.putExtra("TRACK", track) // передаем выбранный трек
//            startActivity(intent)
//        }
        // Обработчик нажатия на трек с debounce
        trackAdapter.setOnTrackClickListener { track ->
            // Используем debounce
            var isClickable = true
            if (isClickable) {
                isClickable = false
                handler.postDelayed({ isClickable = true }, 1000)

                searchHistory.addTrack(track)
                historyAdapter.notifyDataSetChanged() // Обновление адаптера истории
                toggleHistoryVisibility()

                val intent = Intent(this, AudioPlayerActivity::class.java)
                intent.putExtra("TRACK", track) // передаем выбранный трек
                startActivity(intent)
            }
        }



        // Обработчик нажатия на кнопку очистки истории
        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            historyAdapter.notifyDataSetChanged() // Обновление адаптера после очистки
            toggleHistoryVisibility()
        }

        // Обновление видимости истории поиска при создании активности
        toggleHistoryVisibility()
    }

    private fun searchTracks(term: String) {
        // Скрываем историю поиска и плейсхолдеры
        hideHistory()
        // Показываем ProgressBar и скрываем остальные элементы
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        hidePlaceholder()

        // Скрываем старые результаты
        trackList.clear()
        trackAdapter.notifyDataSetChanged()

        // Выполняем запрос
        iTunesService.searchTracks(term).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                progressBar.visibility = View.GONE // Скрываем ProgressBar после завершения поиска

                if (response.isSuccessful && response.body() != null) {
                    trackList.clear()
                    trackList.addAll(response.body()!!.results)
                    if (trackList.isEmpty()) {
                        showPlaceholder(getString(R.string.search_nothing), R.drawable.placeholder_no_results, false)
                    } else {
                        hidePlaceholder()
                        recyclerView.visibility = View.VISIBLE // Показываем список треков
                        trackAdapter.notifyDataSetChanged()
                    }
                } else {
                    showPlaceholder(getString(R.string.search_error), R.drawable.placeholder_server_error, true)
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                progressBar.visibility = View.GONE // Скрываем ProgressBar в случае ошибки
                showPlaceholder(getString(R.string.search_error), R.drawable.placeholder_server_error, true)
            }
        })
    }

    private fun hideHistory() {
        historyRecyclerView.visibility = View.GONE
        historyHeader.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
    }

    private fun toggleHistoryVisibility() {
        val history = searchHistory.getHistory()
        if (history.isNotEmpty() && searchEditText.text.isEmpty()) {
            historyHeader.visibility = View.VISIBLE
            clearHistoryButton.visibility = View.VISIBLE
            historyRecyclerView.visibility = View.VISIBLE
            historyAdapter = TrackAdapter(history)
            historyRecyclerView.layoutManager = LinearLayoutManager(this)
            historyRecyclerView.adapter = historyAdapter

            historyAdapter.setOnTrackClickListener { track ->
                // Перемещаем трек на вершину истории
                searchHistory.addTrack(track)
                // Обновление видимости истории
                toggleHistoryVisibility()

                // Открытие меню трека (AudioPlayerActivity)
                val intent = Intent(this, AudioPlayerActivity::class.java)
                intent.putExtra("TRACK", track) // передаем выбранный трек
                startActivity(intent)
            }
        } else {
            historyHeader.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
            historyRecyclerView.visibility = View.GONE
        }
    }

    private fun showPlaceholder(message: String, imageResId: Int, showRetryButton: Boolean) {
        recyclerView.visibility = View.GONE
        placeholderMessageError.visibility = View.VISIBLE
        placeholderTextView.visibility = View.VISIBLE
        placeholderTextView.text = message
        placeholderMessageError.setImageResource(imageResId)

        // Условие показа кнопки повтора
        retryButton.visibility = if (showRetryButton) View.VISIBLE else View.GONE
    }

    private fun hidePlaceholder() {
        recyclerView.visibility = View.GONE
        placeholderMessageError.visibility = View.GONE
        placeholderTextView.visibility = View.GONE
        retryButton.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("searchText", searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString("searchText", "")
        searchEditText.setText(searchText)
        toggleHistoryVisibility() // Переключаем видимость истории при восстановлении состояния
    }
}

