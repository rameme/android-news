package com.example.androidnews

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync

class HeadlineActivity: AppCompatActivity(), AdapterView.OnItemSelectedListener {

    // init variables
    private lateinit var recyclerView : RecyclerView
    private lateinit var preferences: SharedPreferences
    private lateinit var spinner : Spinner
    private lateinit var nextButton : Button
    private lateinit var previousButton : Button
    private lateinit var pages : TextView
    private lateinit var progressBar : ProgressBar
    private var currentPage : Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_headline)

        // set title
        title = getString(R.string.headlineTitle)

        /* progress bar */
        progressBar = findViewById(R.id.progressHeadlineBar)

        /* headline recyclerView */
        recyclerView = findViewById(R.id.headlineRecyclerView)

        /* spinner */
        spinner = findViewById(R.id.categoryHeadline)

        // sharedPrefs
        preferences = getSharedPreferences("android-news", Context.MODE_PRIVATE)
        val category: String? = preferences.getString("CATEGORY-HEADLINE","")

        // log the stored category
        Log.e("HeadlineActivity", "Stored Category : $category")

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

            // if the stored category is not null, set it as the spinnerValue
            if(!category.isNullOrEmpty()){
                val spinnerValue = adapter.getPosition(category);
                spinner.setSelection(spinnerValue)
            }

            spinner.onItemSelectedListener = this
        }

        /* next button */
        nextButton = findViewById(R.id.nextButton)
        nextButton.isEnabled = true

        // move the next page and update page count
        nextButton.setOnClickListener{
            if(currentPage + 1 != 6) {
                currentPage += 1
                pages.text = getString(R.string.pageText,currentPage.toString())

                // get headline for the current page
                getHeadlines(category!!)
            }
        }

        /* previous button */
        previousButton = findViewById(R.id.previousButton)
        previousButton.isEnabled = false

        // move to the previous page and update page count
        previousButton.setOnClickListener{
            if(currentPage - 1 != 0) {
                currentPage -= 1
                pages.text = getString(R.string.pageText,currentPage.toString())

                // get headline for the current page
                getHeadlines(category!!)
            }
        }

        // use TextWatcher to enable and disable next and previous page button
        pages = findViewById(R.id.pages)
        pages.text = getString(R.string.pageText,currentPage.toString())
        pages.addTextChangedListener(textWatcher)
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        // get the selected category
        val category = p0?.getItemAtPosition(p2).toString()
        Log.e("HeadlineActivity", "Category : $category")

        // save the selected category
        preferences
            .edit()
            .putString("CATEGORY-HEADLINE", category)
            .apply()

        // update page
        currentPage = 1
        pages.text = getString(R.string.pageText,currentPage.toString())

        // get headline for the current page
        getHeadlines(category)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        // Error, no category is selected, display a Toast
        Toast.makeText(
            this@HeadlineActivity,
            "No category selected!",
            Toast.LENGTH_LONG
        ).show()

        // log it
        Log.d("HeadlineActivity", "No category selected")
    }

    // Retrieve headline for the selected category
    private fun getHeadlines(category : String){

        // get newsManager and apikey
        val newsManager = NewsManager()
        val apiKey: String = getString(R.string.news_api_key)

        // enable progress bar on networking and disable buttons
        progressBar.visibility = VISIBLE

        // Networking on a background thread
        doAsync {

            // Use our NewsManager to get headlines from the News API. If there is network
            // connection issues, the catch-block will fire and we'll show the user an error message.
            val headline: List<News> = try {
                newsManager.retrieveHeadline(category, currentPage, apiKey)
            } catch(exception: Exception) {
                Log.e("HeadlineActivity", "Networking issue: failed to headlines", exception)
                listOf<News>()
            }

            runOnUiThread {
                if (headline.isNotEmpty()) {
                    // disable progress bar
                    progressBar.visibility = INVISIBLE

                    // display the retrieved headlines in a cardView
                    val adapter = NewsAdapter(headline)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(this@HeadlineActivity)
                } else {
                    // enable progress bar on network failure
                    progressBar.visibility = VISIBLE

                    // disable buttons
                    nextButton.isEnabled = false
                    previousButton.isEnabled = false

                    // display a toast
                    Toast.makeText(
                        this@HeadlineActivity,
                        "No headlines for $category!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // call textWatcher everytime the pagesText changes
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        // enable/disable the page navigation button based on page count
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            nextButton.isEnabled = currentPage + 1 != 6
            previousButton.isEnabled = currentPage - 1 != 0
        }

        override fun afterTextChanged(p0: Editable?) {}
    }
}