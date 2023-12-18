package com.sa.info_5126project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.sa.info_5126project.databinding.ActivityRegisterBinding

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize Firebase Auth
        auth = Firebase.auth
        binding.buttonRegister.setOnClickListener {
            when {
                TextUtils.isEmpty(binding.username.text.toString().trim {it <= ' '}) -> {
                    Toast.makeText(this,"Please enter email", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(binding.password.text.toString().trim {it <= ' '}) -> {
                    Toast.makeText(this,"Please enter email", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(binding.pseudoEditText.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(this, "Please enter pseudo", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val email:String = binding.username.text.toString().trim {it <= ' '}
                    val password:String = binding.password.text.toString().trim {it <= ' '}
                    val pseudo: String = binding.pseudoEditText.text.toString().trim { it <= ' ' }
                    // signs in current user
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "createUserWithEmail:success")
                                val user = auth.currentUser
                                // Store user information in Firebase Database (Firestore)
                                saveUserToDatabase(user?.uid, email, pseudo)
                                val intent = Intent(this,LoginActivity::class.java)
                                // gets rid of any login or register activities that are left open
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("userid",auth.currentUser?.uid)
                                intent.putExtra("email",user)
                                startActivity(intent)
                                finish()
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "createUserWithEmail:failure", task.exception)
                                Toast.makeText(
                                    baseContext,
                                    "Authentication failed.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                                println("Authentication failed.")
                            }
                        }

                }
            }
        }

    }
    private fun saveUserToDatabase(userId: String?, email: String, pseudo: String) {
        // Use Firebase Firestore to store user information
        val db = FirebaseFirestore.getInstance()
        val user = hashMapOf(
            "email" to email,
            "pseudo" to pseudo,
        )
        db.collection("users").document(userId ?: "").set(user)
            .addOnSuccessListener {
                Log.d("TAG", "User information stored in database.")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error storing user information", e)
            }
    }

    fun onReturnButtonClick(view: View){
        startActivity(Intent(this, LoginActivity::class.java))
    }
}