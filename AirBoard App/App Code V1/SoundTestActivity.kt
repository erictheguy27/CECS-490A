package com.example.airboard

import com.example.airboard.R



import android.media.AudioManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.media.SoundPool
import android.view.View
class SoundTestActivity: AppCompatActivity() {
    private var soundPool: SoundPool? = null
    private var note1 = 0
    private var note2 = 0
    private var note3 = 0
    private var note4 = 0
    private var note5 = 0
    private var note6 = 0
    private var note7 = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.soundtest)
        soundPool = SoundPool(10, AudioManager.STREAM_MUSIC, 0)
        note1 = soundPool!!.load(baseContext, R.raw.a4, 1)
        note2 = soundPool!!.load(baseContext, R.raw.b4, 1)
        note3 = soundPool!!.load(baseContext, R.raw.c4, 1)
        note4 = soundPool!!.load(baseContext, R.raw.d4, 1)
        note5 = soundPool!!.load(baseContext, R.raw.e4, 1)
        note6 = soundPool!!.load(baseContext, R.raw.f4, 1)
        note7 = soundPool!!.load(baseContext, R.raw.g4, 1)


    }
    fun playSound(view: View){
        when(view.id){
            R.id.a -> soundPool?.play(note1, 1F,1F,0,0,1F)
            R.id.b -> soundPool?.play(note2, 1F,1F,0,0,1F)
            R.id.c -> soundPool?.play(note3, 1F,1F,0,0,1F)
            R.id.d -> soundPool?.play(note4, 1F,1F,0,0,1F)
            R.id.e -> soundPool?.play(note5, 1F,1F,0,0,1F)
            R.id.f -> soundPool?.play(note6, 1F,1F,0,0,1F)
            R.id.g -> soundPool?.play(note7, 1F,1F,0,0,1F)
        }
    }

}