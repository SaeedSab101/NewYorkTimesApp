package com.sa.info_5126project

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainViewModel : ViewModel() {
    // LiveData to hold the API response
    var booksLiveData = MutableLiveData<APIFormat>()

        // Method to fetch data from the API
        fun fetchBooksData(apiUrl: String) {
            CoroutineScope(Dispatchers.Main).launch {
                val request = getBookDataFromCoroutine(apiUrl)
                if (request != null) {
                    // Update LiveData with the fetched data
                    booksLiveData.value = request
                } else {
                    // Handle the case where the request is null (error case)
                    booksLiveData.value = null
                }
            }
        }

        // Coroutine to get the book data
        private suspend fun getBookDataFromCoroutine(apiUrl: String): APIFormat? {
            return CoroutineScope(Dispatchers.IO).async {
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpsURLConnection
                if (connection.responseCode == 200) {
                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val request = Gson().fromJson(inputStreamReader, APIFormat::class.java)
                    inputStreamReader.close()
                    inputSystem.close()
                    return@async request
                } else {
                    return@async null
                }
            }.await()
        }
    }