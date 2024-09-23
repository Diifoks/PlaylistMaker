package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val artworkImageView: ImageView = itemView.findViewById(R.id.artworkImageView)
    private val trackNameTextView: TextView = itemView.findViewById(R.id.trackNameTextView)
    private val artistNameTextView: TextView = itemView.findViewById(R.id.artistNameTextView)
    private val trackTimeTextView: TextView = itemView.findViewById(R.id.trackTimeTextView)

    fun bind(track: Track) {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName

        val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        trackTimeTextView.text = formattedTime

        // Загрузка изображения обложки с использованием Glide
        Glide.with(itemView.context)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder) // Заглушка
            .error(R.drawable.placeholder) // Заглушка на случай, если изображение не загрузилось
            .centerInside()
            .transform(RoundedCorners(10))
            .into(artworkImageView)
    }
}