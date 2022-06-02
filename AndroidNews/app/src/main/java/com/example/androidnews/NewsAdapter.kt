package com.example.androidnews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Picasso


class NewsAdapter (val news: List<News>) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    // ViewHolder holds the news data.
    // Get the TextView using the id
    class ViewHolder(rootLayout: View) : RecyclerView.ViewHolder(rootLayout){
        val newsTitleText: TextView = rootLayout.findViewById(R.id.newsTitle)
        val newsSourceText: TextView = rootLayout.findViewById(R.id.newsSource)
        val newsDescriptionText: TextView = rootLayout.findViewById(R.id.newsDescription)
        val newsUrlText: TextView = rootLayout.findViewById(R.id.newsUrl)
        val newsIcon: ImageView = rootLayout.findViewById(R.id.newsIcon)
        val newsIdText: TextView = rootLayout.findViewById(R.id.newsId)
    }

    // Create a new row by reading data from the XML file for the row type
    // and use the new row to build a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val rootLayout: View = layoutInflater.inflate(R.layout.row_news, parent, false)
        return NewsAdapter.ViewHolder(rootLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentSource = news[position]

        holder.newsTitleText.text = currentSource.newsTitle
        holder.newsSourceText.text = currentSource.newsSource
        holder.newsDescriptionText.text = currentSource.newsDescription
        holder.newsUrlText.text = currentSource.newsUrl
        holder.newsIdText.text = currentSource.newsId

        // if clicked, open news item url from the recyclerView
        val context = holder.newsUrlText.context
        holder.itemView.setOnClickListener(View.OnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(currentSource.newsUrl))
            Log.d("NewsAdapter", "Clicked on News link")
            context.startActivity(browserIntent)
        })

        // load image
        if(currentSource.newsThumbnail.isNotEmpty()){
            Picasso
                .get()
                .load(currentSource.newsThumbnail)
                .into(holder.newsIcon)
        }
    }

    override fun getItemCount(): Int {
        return news.size
    }
}