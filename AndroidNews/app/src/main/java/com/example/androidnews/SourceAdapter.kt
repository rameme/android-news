package com.example.androidnews

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SourceAdapter (val source: List<Source>) : RecyclerView.Adapter<SourceAdapter.ViewHolder>() {

    // ViewHolder holds the sources data.
    // Get the TextView using the id
    class ViewHolder(rootLayout: View) : RecyclerView.ViewHolder(rootLayout){
        val sourceNameText: TextView = rootLayout.findViewById(R.id.sourceName)
        val sourceDescriptionText: TextView = rootLayout.findViewById(R.id.sourceDescription)
        val sourceUrlText: TextView = rootLayout.findViewById(R.id.sourceUrl)
        val sourceIdText: TextView = rootLayout.findViewById(R.id.sourceId)
    }

    // Create a new row by reading data from the XML file for the row type
    // and use the new row to build a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val rootLayout: View = layoutInflater.inflate(R.layout.row_source, parent, false)
        return ViewHolder(rootLayout)
    }

    // Display a new row on the screen using the index
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentSource = source[position]
        holder.sourceNameText.text = currentSource.sourceName
        holder.sourceDescriptionText.text = currentSource.sourceDescription
        holder.sourceUrlText.text = currentSource.sourceUrl
        holder.sourceIdText.text = currentSource.sourceId

        val context = holder.sourceUrlText.context
        // open news item Url from the recyclerView
        holder.itemView.setOnClickListener(View.OnClickListener {
            // get search term
            val sourceIntent = (context as Activity).intent
            val searchTerm: String = sourceIntent.getStringExtra("SEARCH")!!

            val intent : Intent = Intent(context, ResultActivity::class.java)
            Log.d("SourceActivity", "Switching to ResultsActivity")

            // store searchTerm and selected source on the intent
            intent.putExtra("SEARCH",searchTerm)
            intent.putExtra("SOURCE",currentSource.sourceId)
            intent.putExtra("SOURCE_NAME",currentSource.sourceName)

            context.startActivity(intent)
        })
    }

    // Get how many rows we want to render
    override fun getItemCount(): Int {
        return source.size
    }
}