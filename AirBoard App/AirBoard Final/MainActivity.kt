package com.example.airboard

import android.content.Intent
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var mainAddress = intent.getStringExtra(SettingsActivity.EXTRA_ADDRESS)!!

        kids.setOnClickListener {
            val intentKids = Intent(this, KidsModeActivity::class.java)
            intentKids.putExtra(SettingsActivity.EXTRA_ADDRESS, mainAddress)

            startActivity(intentKids)
        }

        fullscale.setOnClickListener {
            val intentFull = Intent(this, DisplayActivity::class.java)
            intentFull.putExtra(SettingsActivity.EXTRA_ADDRESS, mainAddress)
            startActivity(intentFull)
        }
        creds.setOnClickListener {

            startActivity(Intent(this, AboutUsActivity::class.java))


        }

    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

}

