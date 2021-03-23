package com.example.airboard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        kids.setOnClickListener {
            startActivity(Intent(this, KidsModeActivity::class.java))
        }

        fullscale.setOnClickListener {
            startActivity(Intent(this,SettingsActivity::class.java))
        }
        settings.setOnClickListener {
            startActivity(Intent(this,SettingsActivity::class.java))
        }
        creds.setOnClickListener{
            startActivity(Intent(this,SoundTestActivity::class.java))
        }

    }

}

