package com.example.androidnews

import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.androidnews.databinding.ActivityMapsBinding
import org.jetbrains.anko.doAsync

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    // init variables
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var cardView: CardView
    private lateinit var cardResultText: TextView
    private lateinit var preferences: SharedPreferences
    private lateinit var progressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get shared preferences
        preferences = getSharedPreferences("android-news", Context.MODE_PRIVATE)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Get Activity Title from string.xml
        val title: String = getString(R.string.mapsActivityTitle)
        setTitle(title)

        /* Card View */
        cardView = findViewById(R.id.mapsCard)
        cardView.visibility = INVISIBLE

        /* Recycler View */
        // Get the sources recyclerView
        recyclerView = findViewById(R.id.mapsRecyclerView)
        recyclerView.visibility = INVISIBLE

        /* Result View */
        cardResultText = findViewById(R.id.mapsResult)

        /* progress bar */
        progressBar = findViewById(R.id.progressMapBar)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // get stored values
        val location: String? = preferences.getString("LOCATION","")
        val lat: String? = preferences.getString("LAT","")
        val lon: String? = preferences.getString("LON","")

        // display marker if we have a scored location
        if (!location.isNullOrEmpty()){
            mMap.clear()

            val cords = LatLng(lat!!.toDouble(),lon!!.toDouble())
            // set market marker
            val marker = MarkerOptions()
                .position(cords)
                .title(location)

            mMap.addMarker(marker)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cords, 10.0f))

            // get local news for the stored location
            cardResultText.text = getString(R.string.newsResult, location)
            getLocalNews(location)
        }

        // place marker and get local news on longClick
        mMap.setOnMapLongClickListener { cords: LatLng ->
            mMap.clear()

            doAsync {

                // get location from geocoder
                val geocoder: Geocoder = Geocoder(this@MapsActivity)
                val results: List<Address> = try {
                    geocoder.getFromLocation(
                        cords.latitude,
                        cords.longitude,
                        10
                    )
                } catch (exception: Exception) {
                    Log.e("MapsActivity", "Geocoding failed!", exception)
                    listOf()
                }

                runOnUiThread {
                    if (!results.isNullOrEmpty()) {
                        val firstResult = results[0]

                        // get city, state, country
                        val addressCity = firstResult.locality
                        val addressStateProvince = firstResult.subAdminArea
                        val addressCountry = firstResult.adminArea

                        // Get the new article data and set the recyclerView
                        val currentLocation = when {
                            addressCity != null -> {
                                addressCity
                            }
                            addressStateProvince != null -> {
                                addressStateProvince
                            }
                            else -> {
                                addressCountry
                            }
                        }

                        // save the cords and location to sharedPreferences
                        preferences
                            .edit()
                            .putString("LAT", cords.latitude.toString())
                            .apply()

                        preferences
                            .edit()
                            .putString("LON", cords.longitude.toString())
                            .apply()

                        preferences
                            .edit()
                            .putString("LOCATION", currentLocation)
                            .apply()

                        // set market marker
                        val marker = MarkerOptions()
                            .position(cords)
                            .title(currentLocation)

                        mMap.addMarker(marker)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cords, 10.0f))

                        cardResultText.text = getString(R.string.newsResult, currentLocation)

                        // get local news for the current location
                        getLocalNews(currentLocation)

                    } else {
                        Toast.makeText(this@MapsActivity, "No locations found!", Toast.LENGTH_LONG).show()

                        // since no result is found, make the cardView and recyclerView invisible
                        cardView.visibility = INVISIBLE
                        recyclerView.visibility = INVISIBLE
                    }
                }
            }
        }
    }

    // get real local news articles from NewsAPI
    private fun getLocalNews(location: String) {

        // get newsManager and NewsAPI key
        val newsManager = NewsManager()
        val apiKey: String = getString(R.string.news_api_key)

        // enable progress bar
        progressBar.visibility = VISIBLE

        doAsync {

            // Use our NewsManager to get articles from the News API. If there is network
            // connection issues, the catch-block will fire and we'll show the user an error message.
            val news: List<News> = try {
                newsManager.retrieveNews(location, apiKey)
            } catch(exception: Exception) {
                // log the error
                Log.e("MapsActivity", "Networking error: retrieving articles failed", exception)
                listOf<News>()
            }

            runOnUiThread {
                if (news.isNotEmpty()) {
                    // disable progress bar
                    progressBar.visibility = INVISIBLE

                    // put the retrieved articles in a horizontal recyclerView
                    val adapter = NewsAdapter(news)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.HORIZONTAL, false)

                    // make visible since we have results
                    cardView.visibility = VISIBLE
                    recyclerView.visibility = VISIBLE
                } else {
                    // enable progress on network failure
                    progressBar.visibility = VISIBLE

                    // news articles for this location, display an error message
                    Toast.makeText(
                        this@MapsActivity,
                        "No news articles for $location!",
                        Toast.LENGTH_LONG
                    ).show()

                    // since no result is found, make the cardView and recyclerView invisible
                    cardView.visibility = INVISIBLE
                    recyclerView.visibility = INVISIBLE
                }
            }
        }
    }
}