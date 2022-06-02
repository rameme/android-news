package com.example.androidnews

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync

class SourceActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    // init variables
    private lateinit var recyclerView: RecyclerView
    private lateinit var skip : Button
    private lateinit var spinner : Spinner
    private lateinit var progressBar : ProgressBar

    override fun onCreate(savedInsanceState: Bundle?){
        super.onCreate(savedInsanceState)
        setContentView(R.layout.activity_source)

        // Retrieve the data from "SEARCH" from intent
        val searchTerm: String = intent.getStringExtra("SEARCH")!!

        // Get "Search for" from String.xml and set title
        val title: String = getString(R.string.sourceSearchTitle, searchTerm)
        setTitle(title)

        // Get the sources recyclerView
        recyclerView = findViewById(R.id.sourceRecyclerView)

        /* spinner */
        spinner = findViewById(R.id.category)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.category,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Apply the adapter to the spinner
            spinner.adapter = adapter
            spinner.onItemSelectedListener = this
        }

        /* progress bar */
        progressBar = findViewById(R.id.progressSourceBar)

        // skip sources selections
        skip = findViewById(R.id.skipButton)
        skip.isEnabled = false

        // skip source selection if skip button is clicked
        skip.setOnClickListener(){ view: View ->
            val intent = Intent(this, ResultActivity::class.java)
            Log.d("SourceActivity", "Switching to ResultsActivity")

            // store searchTerm
            intent.putExtra("SEARCH",searchTerm)
            intent.putExtra("SOURCE","")
            intent.putExtra("SOURCE_NAME","")

            startActivity(intent)
        }
    }

    // Spinner category is selected
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        // get the selected category
        val category = p0?.getItemAtPosition(p2).toString()

        // get the sourceManager and NewsAPI key
        val sourceManager = SourceManager()
        val apiKey: String = getString(R.string.news_api_key)

        // enable progress bar
        progressBar.visibility = View.VISIBLE
        skip.isEnabled = false

        // Networking is done on a background thread
        doAsync {
            // Use our SourceManager to get sources from the News API. If there is network
            // connection issues, the catch-block will fire and we'll show the user an error message.
            val sources: List<Source> = try {
                sourceManager.retrieveSources(category, apiKey)
            } catch(exception: Exception) {
                Log.e("SourceActivity", "Retrieving sources failed", exception)
                listOf<Source>()
            }

            runOnUiThread {
                if (sources.isNotEmpty()) {
                    // disable progress bar
                    progressBar.visibility = View.INVISIBLE
                    skip.isEnabled = true

                    // put the source in a recyclerView
                    val adapter = SourceAdapter(sources)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(this@SourceActivity)
                } else {
                    // disable progress bar on error and skip button
                    progressBar.visibility = View.VISIBLE
                    skip.isEnabled = false

                    // display a toast if no source is found
                    Toast.makeText(
                        this@SourceActivity,
                        "Failed to retrieve sources!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // no category is selected, display a toast
    override fun onNothingSelected(p0: AdapterView<*>?) {
        Toast.makeText(
            this@SourceActivity,
            "No category is selected!",
            Toast.LENGTH_LONG
        ).show()
    }
}