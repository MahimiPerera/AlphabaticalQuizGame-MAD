package com.example.alphabaticalquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView

class Logo : AppCompatActivity() {


    lateinit var  imgtitle:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo)
        imgtitle= findViewById(R.id.quiztitle)
    }

    fun imgClick(view: View){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
