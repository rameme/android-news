package com.example.androidnews

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.text.TextWatcher

class MainActivity : AppCompatActivity() {

    // init variables
    private lateinit var searchTerm: EditText
    private lateinit var searchButton: Button
    private lateinit var mapsButton: Button
    private lateinit var headlineButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // use the activity main layout and log it
        setContentView(R.layout.activity_main)
        Log.d("MainActivity", "onCreate called!")

        // get preferences
        val preferences: SharedPreferences = getSharedPreferences("android-news", Context.MODE_PRIVATE)

        // get searchTerm and searchButton (disable it)
        searchTerm = findViewById(R.id.search)
        searchButton = findViewById(R.id.searchButton)
        searchButton.isEnabled = false

        /* SourceActivity */
        // when search is clicked start the SourceActivity
        searchButton.setOnClickListener{ view: View ->
            val inputtedSearch: String = searchTerm.text.toString()

            // Save the search to SharedPreferences
            preferences
                .edit()
                .putString("SEARCH",inputtedSearch)
                .apply()

            // create a new intent
            val intent: Intent = Intent(this, SourceActivity::class.java)

            // store searchTerm on the intent
            intent.putExtra("SEARCH",inputtedSearch)

            // log and execute the intent
            Log.d("MainActivity", "Switching to SourceActivity")
            startActivity(intent)
        }

        /* MapsActivity */
        // when maps button is clicked go to the maps page
        mapsButton = findViewById(R.id.mapsButton)
        mapsButton.setOnClickListener { view: View ->
            val intent = Intent(this, MapsActivity::class.java)
            Log.d("MainActivity", "Switching to MapsActivity")
            startActivity(intent)
        }

        /* HeadlineActivity */
        // when headline button is clicked go to the headline page
        headlineButton = findViewById(R.id.headlineButton)
        headlineButton.setOnClickListener{ view: View ->
            val intent = Intent(this, HeadlineActivity::class.java)
            Log.d("MainActivity", "Switching to HeadlineActivity")
            startActivity(intent)
        }

        // use TextWatcher for EditText
        searchTerm.addTextChangedListener(textWatcher)

        // set stored searchTerm
        val search: String? = preferences.getString("SEARCH","")
        searchTerm.setText(search)
    }

    // call textWatcher everytime the user types in the search bar
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        // when the user enters characters in the search bear, enable the search button
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // enable the search button if the search term is not empty
            val inputtedSearchTerm: String = searchTerm.text.toString()
            searchButton.isEnabled = inputtedSearchTerm.isNotBlank()
        }

        override fun afterTextChanged(p0: Editable?) {}
    }
}