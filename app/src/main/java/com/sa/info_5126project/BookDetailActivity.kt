package com.sa.info_5126project

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.squareup.picasso.Picasso
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.sa.info_5126project.databinding.ActivityBookDetailBinding

class BookDetailActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private var userId: String? = null
    private var bookTitle: String? = null
    private lateinit var binding: ActivityBookDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve book information from Intent
        val rank = intent.getIntExtra("rank", 0)
        val author = intent.getStringExtra("author")
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val category = intent.getStringExtra("category")
        val bookImage = intent.getStringExtra("book_image")
        val amazonProductUrl = intent.getStringExtra("amazon_product_url")

        // Find views in the layout
        val rankTextView: TextView = binding.textViewRanking
        val titleTextView: TextView = binding.textViewTitleDetails
        val authorTextView: TextView = binding.textViewAuthorDetails
        val descriptionTextView: TextView = binding.textViewDescriptionDetails
        val imageView: ImageView = binding.imageViewDetails
        val amazonUrlTextView: TextView = binding.textViewAmazonUrlDetails

        // Set the values of the views
        rankTextView.text = "Rank Number: $rank"
        titleTextView.text = title
        authorTextView.text = author
        descriptionTextView.text = description
        amazonUrlTextView.text = "Buy Here: $amazonProductUrl"

        //Check if it has ranking, if does show visible (Primarily for the saved book list)
        if (rank > 0) {
            rankTextView.text = "Rank Number: $rank"
            rankTextView.visibility = View.VISIBLE
        } else {
            // If rank is 0 or less, hide the textViewRanking
            rankTextView.visibility = View.GONE
        }

        val saveToListButton: Button = binding.saveToListButton
        //val backToListButton: Button = findViewById(R.id.backToListButton)
        val deleteFromSavedBooksButton: Button = binding.deleteFromSavedBooksButton

        // Set background colors for the buttons
        saveToListButton.setBackgroundColor(Color.BLUE)
        deleteFromSavedBooksButton.setBackgroundColor(Color.RED)

        userId = intent.getStringExtra("USERID_STRING")
        bookTitle = title
        // Load the book image using Picasso
        Picasso.get().load(bookImage).into(imageView)
        //checkIfBookSaved(userId, title)
    }

    fun onBackButtonClick(view: View){
        //startActivity(Intent(this, BooksScreen::class.java))
        finish()
    }

    fun onSaveToListButtonClick(view: View) {

        // Retrieve book information from Intent
        val rank = intent.getIntExtra("rank", 0)
        val author = intent.getStringExtra("author")
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val category = intent.getStringExtra("category")
        val bookImage = intent.getStringExtra("book_image")
        val amazonProductUrl = intent.getStringExtra("amazon_product_url")

        // Create a Book object
        val book = Book(
            title = title ?: "",
            author = author ?: "",
            description = description ?: "",
            url = "",
            category = category?: "",
            book_image = bookImage ?: "",
            rank = rank,
            amazon_product_url = amazonProductUrl ?: ""
        )

        // Save the book to Firestore
        saveBookToFirestore(book)
    }

    private fun saveBookToFirestore(book: Book) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            // Check if the book with the same title already exists
            db.collection("users")
                .document(userId)
                .collection("saved_books")
                .whereEqualTo("title", book.title)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        // Book does not exist, proceed to add
                        db.collection("users")
                            .document(userId)
                            .collection("saved_books")
                            .add(book)
                            .addOnSuccessListener { documentReference ->
                                // Handle success
                                Log.d("TAG", "Book saved with ID: ${documentReference.id}")
                                Toast.makeText(this, "Book saved to your list!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                // Handle failure
                                Log.w("TAG", "Error adding book", e)
                                Toast.makeText(this, "Failed to save book. Please try again.", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        // Book already exists
                        Toast.makeText(this, "Book is already in your list.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    // Handle failure
                    Log.w("TAG", "Error checking if book exists", e)
                    Toast.makeText(this, "Error checking if book exists. Please try again.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun onDeleteFromSavedBooksClick(view: View) {
        deleteBookFromFirestore()
    }

    private fun deleteBookFromFirestore() {
        val bookTitle = intent.getStringExtra("title")
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null && bookTitle != null) {
            db.collection("users")
                .document(userId)
                .collection("saved_books")
                .whereEqualTo("title", bookTitle)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        // Delete the document (book) from the collection
                        db.collection("users")
                            .document(userId)
                            .collection("saved_books")
                            .document(document.id)
                            .delete()
                            .addOnSuccessListener {
                                // Handle successful deletion
                                Log.d("TAG", "Book deleted successfully")
                                Toast.makeText(this, "Book deleted from your list!", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener { e ->
                                // Handle failure
                                Log.w("TAG", "Error deleting book", e)
                                Toast.makeText(this, "Failed to delete book. Please try again.", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    // Handle failure
                    Log.w("TAG", "Error checking if book is saved", e)
                }
        }
    }
}
