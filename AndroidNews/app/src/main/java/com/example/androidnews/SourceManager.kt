package com.example.androidnews

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject

class SourceManager {
    private val okHttpClient: OkHttpClient

    init {
        val builder = OkHttpClient.Builder()

        // This will cause all network traffic to be logged to the console for easy debugging
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)

        okHttpClient = builder.build()
    }

    fun retrieveSources(category: String, apiKey: String): List<Source> {
        val language = "en"

        // HTML GET request for news sources from the NewsAPI
        val request: Request = Request.Builder()
            .url("https://newsapi.org/v2/top-headlines/sources?apiKey=$apiKey&language=$language&category=$category")
            .get()
            .build()

        // This executes the request and waits for a response from the server
        val response: Response = okHttpClient.newCall(request).execute()
        val responseBody: String? = response.body?.string()

        // The .isSuccessful checks to see if the status code is 200-299
        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
            val sources = mutableListOf<Source>()

            // Parse our way through the JSON hierarchy for sources
            val json: JSONObject = JSONObject(responseBody)
            val jsonSources: JSONArray = json.getJSONArray("sources")

            for (i in 0 until jsonSources.length()) {
                val curr: JSONObject = jsonSources.getJSONObject(i)

                val sourceName: String = curr.getString("name")
                val sourceDescription: String = curr.getString("description")
                val sourceCategory: String = curr.getString("category")
                val sourceUrl: String = curr.getString("url")
                val sourceId: String = curr.getString("id")

                val source = Source(
                    sourceName = sourceName,
                    sourceDescription = sourceDescription,
                    sourceCategory = sourceCategory,
                    sourceUrl = sourceUrl,
                    sourceId = sourceId,
                )

                sources.add(source)
            }

            return sources
        }

        return listOf()
    }
}