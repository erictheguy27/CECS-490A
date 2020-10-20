package com.example.airboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.media.MediaPlayer
import android.view.View
class KidsModeActivity: AppCompatActivity() {
    var mMediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kids_mode)

    }

    // 1. Plays the water sound
    fun playOde(view: View) = if (mMediaPlayer == null) {
        mMediaPlayer = MediaPlayer.create(this, R.raw.ode)
        mMediaPlayer!!.start()
    } else if (mMediaPlayer != null) {
        mMediaPlayer!!.stop()
        mMediaPlayer!!.release()
        mMediaPlayer = null
    }else mMediaPlayer!!.start()

    fun playMary(view: View) = if (mMediaPlayer == null) {
        mMediaPlayer = MediaPlayer.create(this, R.raw.mary)
        mMediaPlayer!!.start()
    } else if (mMediaPlayer != null) {
        mMediaPlayer!!.stop()
        mMediaPlayer!!.release()
        mMediaPlayer = null
    }else mMediaPlayer!!.start()

    // 2. Pause playback
    fun pauseSound(view: View) {
        if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) mMediaPlayer!!.pause()
    }

    // 3. {optional} Stops playback
    fun stopSound(view: View) {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    // 4. Closes the MediaPlayer when the app is closed
    override fun onStop() {
        super.onStop()
        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }
}