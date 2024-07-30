package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
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

class SearchActivity : AppCompatActivity() {
    // Переменные для сохранения текста
    private lateinit var searchEditText: EditText
    private var searchText: String = ""
    // Переменные для списка треков
    private lateinit var trackList: ArrayList<Track>
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var iTunesService: iTunesApi
    //Переменные для ошибок
    private lateinit var placeholderMessageError: ImageView
    private lateinit var placeholderTextView: TextView
    private lateinit var retryButton: Button
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        placeholderMessageError = findViewById(R.id.placeholderMessageError)
        placeholderTextView = findViewById(R.id.placeholderTextView)
        retryButton = findViewById(R.id.retryButton)
        recyclerView = findViewById(R.id.recyclerView)
        searchEditText = findViewById(R.id.searchEditText)
        val clearButton = findViewById<ImageView>(R.id.clearButton)

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
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Действия до изменения текста
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                // Логика для изменения поля ввода
                if (s.isNullOrEmpty()) {
                    clearButton.visibility = View.GONE
                } else {
                    clearButton.visibility = View.VISIBLE
                }
            }

            // При изменение текста
            override fun afterTextChanged(s: Editable?) {
            }
        }

        // Установка TextWatcher для поля ввода поисковой строки
        searchEditText.addTextChangedListener(textWatcher)

        // Установка фокуса на поле ввода
        searchEditText.requestFocus()

        trackList = arrayListOf()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter(trackList)
        recyclerView.adapter = trackAdapter

        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        iTunesService = retrofit.create(iTunesApi::class.java)
    }

    private fun searchTracks(term: String) {
        iTunesService.searchTracks(term).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    trackList.clear()
                    trackList.addAll(response.body()!!.results)
                    if (trackList.isEmpty()) {
                        showPlaceholder( getString(R.string.search_nothing), R.drawable.placeholder_no_results, false)
                    } else {
                        hidePlaceholder()
                        trackAdapter.notifyDataSetChanged()
                    }
                } else {
                    showPlaceholder(getString(R.string.search_error), R.drawable.placeholder_server_error, true)

                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                showPlaceholder(getString(R.string.search_error), R.drawable.placeholder_server_error, true)
            }
        })
    }

    private fun showPlaceholder(message: String, imageResId: Int, showRetryButton: Boolean) {
        recyclerView.visibility = View.GONE
        placeholderMessageError.visibility = View.VISIBLE
        placeholderTextView.visibility = View.VISIBLE
        placeholderTextView.text = message
        placeholderMessageError.setImageResource(imageResId)

        if (showRetryButton) {
            retryButton.visibility = View.VISIBLE
        } else {
            retryButton.visibility = View.GONE
        }
    }

    private fun hidePlaceholder() {
        recyclerView.visibility = View.VISIBLE
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
    }
}
