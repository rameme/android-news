package com.example.androidnews

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import org.jetbrains.anko.doAsync

class ResultActivity: AppCompatActivity() {

    // init variables
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchTerm: String
    private lateinit var source: String
    private lateinit var sourceName: String
    private lateinit var progressBar : ProgressBar

    override fun onCreate(savedInsanceState: Bundle?) {
        super.onCreate(savedInsanceState)
        setContentView(R.layout.activity_result)

        // Retrieve the data from "SEARCH" and "CATEGORY" from intent
        searchTerm = intent.getStringExtra("SEARCH")!!
        source = intent.getStringExtra("SOURCE")!!
        sourceName = intent.getStringExtra("SOURCE_NAME")!!

        // set title from String.xml
        title = if(source.isNotEmpty()){
            getString(R.string.sourceResultTitle, sourceName, searchTerm)
        } else {
            getString(R.string.resultTitle, searchTerm)
        }

        /* recyclerView */
        recyclerView = findViewById(R.id.resultView)

        /* progress bar */
        progressBar = findViewById(R.id.progressResultBar)

        // get newsManager and api key
        val newsManager = NewsManager()
        val apiKey: String = getString(R.string.news_api_key)

        // enable progress bar
        progressBar.visibility = View.VISIBLE

        // Networking is done on a background thread
        doAsync {

            // Use our NewsManager to get sources from the News API. If there is network
            // connection issues, the catch-block will fire and we'll show the user an error message.
            val results: List<News> = try {
                // no category selected, so we search everything
                newsManager.retrieveResult(source,searchTerm,apiKey)
            } catch(exception: Exception) {
                Log.e("ResultActivity", "Retrieving sources failed", exception)
                listOf<News>()
            }

            runOnUiThread {
                if (results.isNotEmpty()) {
                    // disable progress bar
                    progressBar.visibility = View.INVISIBLE

                    // put the news articles in a recyclerView
                    val adapter = NewsAdapter(results)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(this@ResultActivity)
                } else {
                    // disable progress bar on network error
                    progressBar.visibility = View.VISIBLE

                    // display a toast if no articles are found
                    Toast.makeText(
                        this@ResultActivity,
                        "Failed to retrieve results for $searchTerm!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}