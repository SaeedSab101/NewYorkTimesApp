package com.sa.info_5126project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class Developers : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developers)
    }

    fun onButtonClick(view: View){
        // Set up the intent to start BooksScreen
        finish()
    }
}