package com.sa.info_5126project

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.sa.info_5126project.databinding.ActivityBooksScreenBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class BooksScreen : AppCompatActivity() {
    private lateinit var recyclerViewManager: RecyclerView.LayoutManager
    private lateinit var binding: ActivityBooksScreenBinding
    private lateinit var recyclerAdapter: RecyclerAdapter
    private var viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Initialize ViewBinding
        binding = ActivityBooksScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setup recyclerView
        recyclerViewManager = LinearLayoutManager(applicationContext)
        binding.recyclerView.layoutManager = recyclerViewManager
        binding.recyclerView.setHasFixedSize(true)

        recyclerAdapter = RecyclerAdapter(emptyList())
        binding.recyclerView.adapter = recyclerAdapter

        // Instantiate viewModel
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.booksLiveData.observe(this) { apiFormat ->
            if (apiFormat != null) {
                binding.textViewCopyright.text = "${apiFormat.copyright}"
                recyclerAdapter.updateData(apiFormat.results.books)
            } else {
                binding.textViewCopyright.text = getString(R.string.nobooks)
            }
        }

        // Initialize UI elements
        val spinnerOptions: Spinner = binding.spinnerOptions
        val editTextSearch: EditText = binding.editTextSearch
        val buttonSearch: Button = binding.buttonSearch
        // Set up the spinner with options
        val spinnerData = arrayOf("Book Type", "Date Published")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerData)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerOptions.adapter = spinnerAdapter

        // Set up the first spinner item selected listener
        binding.spinnerOptions.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                val selectedOption = spinnerOptions.selectedItem.toString()

                // Show the second spinner when "Book Type" is selected
                if (selectedOption == "Book Type") {
                    val spinnerListName: Spinner = binding.spinnerListName
                    spinnerListName.visibility = View.VISIBLE
                    editTextSearch.visibility = View.GONE
                }
                // Show the EditText for the date if it is selected
                else {
                    val spinnerListName: Spinner = binding.spinnerListName
                    spinnerListName.visibility = View.GONE
                    editTextSearch.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Do nothing
            }
        })

        // Set up the second spinner with options
        val spinnerListName: Spinner = binding.spinnerListName
        val listNameData = arrayOf("Combined Print and E-Book Fiction", "Combined Print and E-Book Nonfiction", "Hardcover Fiction", "Hardcover Nonfiction",
            "Trade Fiction Paperback", "Mass Market Paperback", "Paperback Nonfiction", "E-Book Fiction", "E-Book Nonfiction", "Hardcover Advice",
            "Paperback Advice", "Advice How-To and Miscellaneous", "Hardcover Graphic Books", "Paperback Graphic Books",  "Manga", "Combined Print Fiction",
            "Combined Print Nonfiction", "Chapter Books", "Childrens Middle Grade", "Childrens Middle Grade E-Book","Childrens Middle Grade Hardcover",
            "Childrens Middle Grade Paperback", "Paperback Books", "Picture Books", "Series Books", "Young Adult", "Young Adult E-Book", "Young Adult Hardcover",
            "Young Adult Paperback", "Animals", "Audio Fiction", "Audio Nonfiction", "Business Books", "Celebrities", "Crime and Punishment", "Culture", "Education",
            "Espionage", "Expeditions Disasters and Adventures", "Fashion Manners and Customs", "Food and Fitness", "Games and Activities",  "Graphic Books and Manga",
            "Hardcover Business Books", "Health", "Humor", "Indigenous Americans", "Relationships", "Mass Market Monthly", "Middle Grade Paperback Monthly", "Paperback Business Books",
            "Family", "Race and Civil Rights", "Religion Spirituality and Faith", "Science", "Sports", "Travel", "Young Adult Paperback Monthly")
        val listNameAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listNameData)
        listNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerListName.adapter = listNameAdapter

        // Set up button click listener
        binding.buttonSearch.setOnClickListener {
            onSearchButtonClick(binding.spinnerOptions, binding.spinnerListName, binding.editTextSearch)
        }

    }

    private fun onSearchButtonClick(spinnerOptions: Spinner, spinnerListName: Spinner,editTextSearch: EditText) {
        val selectedOption = spinnerOptions.selectedItem.toString()

        // To retrieve the selected category from the spinner
        val bookName = if (selectedOption == "Book Type") {
            spinnerListName.selectedItem.toString()
        } else {
            editTextSearch.text.toString()
        }

        // Dynamically generate the URL based on the selected spinner item
        val apiUrl = when (selectedOption) {
            "Cover Type and Type of Fiction" -> "https://api.nytimes.com/svc/books/v3/lists/current/$bookName.json?api-key=T2EAcub58TIt7xmggIdm3XwbmsPbHMWl"
            "Date Published" -> "https://api.nytimes.com/svc/books/v3/lists/$bookName/hardcover-fiction.json?api-key=T2EAcub58TIt7xmggIdm3XwbmsPbHMWl"
            else -> "https://api.nytimes.com/svc/books/v3/lists/current/$bookName.json?api-key=T2EAcub58TIt7xmggIdm3XwbmsPbHMWl"
        }

        // Fetch data using ViewModel
        viewModel.fetchBooksData(apiUrl)
    }

    fun onArrowClick(view: View){
        finish()
    }
}
