package com.sa.info_5126project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.sa.info_5126project.databinding.ActivityViewSavedBooksBinding

class ViewSavedBooksActivity : AppCompatActivity() {
    private lateinit var recyclerViewManager: RecyclerView.LayoutManager
    private lateinit var binding: ActivityViewSavedBooksBinding
    private lateinit var recyclerAdapter: RecyclerAdapter
    private val db = Firebase.firestore
    private val savedBooks: MutableList<Book> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_view_saved_books)
        binding = ActivityViewSavedBooksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra("USERID_STRING")
        recyclerViewManager = LinearLayoutManager(applicationContext)
        binding.recyclerView2.layoutManager = recyclerViewManager
        binding.recyclerView2.setHasFixedSize(true)

        recyclerAdapter = RecyclerAdapter(emptyList())
        binding.recyclerView2.adapter = recyclerAdapter

        // Retrieve user's saved books from Firestore
        retrieveSavedBooksFromFirestore(userId)
    }

    private fun retrieveSavedBooksFromFirestore(userId: String?) {

        db.collection("users").document(userId ?: "")
            .collection("saved_books").get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    val title = document.getString("title") ?: ""
                    val author = document.getString("author") ?: ""
                    val description = document.getString("description") ?: ""
                    val url = document.getString("url") ?: ""
                    val category = document.getString("category") ?: ""
                    val bookImage = document.getString("book_image") ?: ""
                    val rank = document.get("rank") as? Int?:0
                    val amazonProductUrl = document.getString("amazon_product_url") ?: ""
                    savedBooks.add(Book(title, author, description, url, category, bookImage, rank, amazonProductUrl))
                }
                binding.recyclerView2.adapter = RecyclerAdapter(savedBooks)
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error retrieving user's saved books information", e)
            }
    }

    fun onButtonClick(view: View) {
        finish()
    }
}