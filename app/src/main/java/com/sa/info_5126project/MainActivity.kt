package com.sa.info_5126project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.sa.info_5126project.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private val savedBooks = arrayListOf<Book>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        // Retrieve user information from Firestore
        val userId = intent.getStringExtra("userid")
        retrieveUserInformation(userId)
        binding.textViewEmail.text = intent.getStringExtra("email")

        binding.logoutButton.setOnClickListener {
            // logs out of Firebase
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this,R.string.logged_out, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
    }

    fun onButtonClick(view: View){
        // Set up the intent to start BooksScreen
        startActivity(Intent(this, BooksScreen::class.java))
    }

    fun onAboutClick(view: View){
        // Set up the intent to start BooksScreen
        startActivity(Intent(this, Developers::class.java))
    }

    private fun retrieveUserInformation(userId: String?) {
        db.collection("users").document(userId ?: "").get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val pseudo = documentSnapshot.getString("pseudo")
                    binding.textViewPseudo.text = "Welcome $pseudo!"
                } else {
                    Log.d("TAG", "User document does not exist")
                }
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error retrieving user information", e)
            }

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
                    val rank = document.getLong("rank")?.toInt() ?: 0
                    val amazonProductUrl = document.getString("amazon_product_url") ?: ""

                    savedBooks.add(Book(title, author, description, url, category, bookImage, rank, amazonProductUrl))
                }
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error retrieving user information", e)
            }
    }

    fun onViewSavedButtonClick(view: View) {
        val userid = intent.getStringExtra("userid")

        val intent = Intent(this, ViewSavedBooksActivity::class.java)
        intent.putExtra("USERID_STRING", userid)
        intent.putExtra("isBookSaved", true)

        startActivity(intent)
    }


}
