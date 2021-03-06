package com.example.airboard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        kidsmode.setOnClickListener {
            startActivity(Intent(this, KidsModeActivity::class.java))
        }

        keyboard.setOnClickListener {
            startActivity(Intent(this,DisplayActivity::class.java))
        }

    }
}
