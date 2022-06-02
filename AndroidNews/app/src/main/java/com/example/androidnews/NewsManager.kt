package com.example.androidnews

import android.widget.Toast
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject

class NewsManager {
    private val okHttpClient: OkHttpClient

    init {
        val builder = OkHttpClient.Builder()

        // This will cause all network traffic to be logged to the console for easy debugging
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)

        okHttpClient = builder.build()
    }

    // retrieve news for a given location
    fun retrieveNews(location: String, apiKey: String): List<News> {
        val language = "en"

        // HTML GET request for news sources from the NewsAPI
        val request: Request = Request.Builder()
            .url("https://newsapi.org/v2/everything?qInTitle=$location&apiKey=$apiKey&language=$language")
            .get()
            .build()

        // This executes the request and waits for a response from the server
        val response: Response = okHttpClient.newCall(request).execute()
        val responseBody: String? = response.body?.string()

        // The .isSuccessful checks to see if the status code is 200-299
        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
            // parseJSON body and return a list of news articles
            return parseJSON(responseBody)
        }

        return listOf()
    }

    // retrieve headline for given category and page number
    fun retrieveHeadline(category: String, page: Int, apiKey: String): List<News> {
        val language = "en"

        // HTML GET request for news sources from the NewsAPI
        val request: Request = Request.Builder()
            .url("https://newsapi.org/v2/top-headlines?category=$category&apiKey=$apiKey&language=$language&page=$page")
            .get()
            .build()

        // This executes the request and waits for a response from the server
        val response: Response = okHttpClient.newCall(request).execute()
        val responseBody: String? = response.body?.string()

        // The .isSuccessful checks to see if the status code is 200-299
        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
            // parseJSON body and return a list of news articles
            return parseJSON(responseBody)
        }

        return listOf()
    }

    // retrieve news for given search term and search term (if provided)
    fun retrieveResult(source: String, searchTerm: String, apiKey: String): List<News> {
        val language = "en"

        // HTML GET request for news sources from the NewsAPI
        val request: Request= if(source.isNullOrEmpty()) {
            // no source, search all source
            Request.Builder()
                .url("https://newsapi.org/v2/everything?q=$searchTerm&apiKey=$apiKey&language=$language")
                .get()
                .build()
        } else {
            // search in the given source
            Request.Builder()
                .url("https://newsapi.org/v2/everything?q=$searchTerm&apiKey=$apiKey&language=$language&sources=$source")
                .get()
                .build()
        }

        // This executes the request and waits for a response from the server
        val response: Response = okHttpClient.newCall(request).execute()
        val responseBody: String? = response.body?.string()

        // The .isSuccessful checks to see if the status code is 200-299
        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
            return parseJSON(responseBody)
        }

        return listOf()
    }

    // Parse JSON response from the API call and return a LIST of NEWS articles
    private fun parseJSON(responseBody: String?): List<News> {

        val news = mutableListOf<News>()

        // Parse our way through the JSON hierarchy for sources
        val json: JSONObject = JSONObject(responseBody)
        val jsonArticles: JSONArray = json.getJSONArray("articles")

        for (i in 0 until jsonArticles.length()) {
            val curr: JSONObject = jsonArticles.getJSONObject(i)

            // get source
            val newsSourceObject: JSONObject = curr.getJSONObject("source")
            val newsSource: String = newsSourceObject.getString("name")
            val newsId: String = newsSourceObject.getString("id")

            // get other information
            val newsTitle: String = curr.getString("title")
            val newsDescription: String = curr.getString("description")
            val newsThumbnail: String = curr.getString("urlToImage")
            val newsUrl: String = curr.getString("url")

            // put information in the news object
            val article = News(
                newsSource = newsSource,
                newsTitle = newsTitle,
                newsDescription = newsDescription,
                newsThumbnail = newsThumbnail,
                newsUrl = newsUrl,
                newsId = newsId
            )

            news.add(article)
        }

        return news
    }
}