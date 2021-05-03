package com.example.airboard

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.control_layout.*
import kotlinx.android.synthetic.main.control_layout.control_led_disconnect
import kotlinx.android.synthetic.main.keyboard_display.*
import java.io.IOException
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class DisplayActivity : AppCompatActivity() {
    companion object {
        //standard UUID
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null//bluetooth socket to connect to
        lateinit var m_progress: ProgressDialog//progress bar of connection
        lateinit var m_bluetoothAdapater: BluetoothAdapter//bluetooth adapter
        var m_isConnected: Boolean = false//check if bluetooth is connected
        lateinit var m_address: String//address of the bluetooth
        private const val TAG = "MY_APP_DEBUG_TAG"
        //variables that hold images of what notes are being played
        lateinit var note1: ImageView
        lateinit var note2: ImageView
        lateinit var note3: ImageView
        lateinit var note4: ImageView

        lateinit var note5: ImageView
        lateinit var note6: ImageView
        lateinit var note7: ImageView
        lateinit var note8: ImageView
        lateinit var note9: ImageView
        lateinit var note10: ImageView

        lateinit var note1_temp: ImageView
        lateinit var note2_temp: ImageView
        lateinit var note3_temp: ImageView
        lateinit var note4_temp: ImageView
        lateinit var note5_temp: ImageView
        lateinit var note6_temp: ImageView
        lateinit var note7_temp: ImageView
        lateinit var note8_temp: ImageView
        lateinit var note9_temp: ImageView
        lateinit var note10_temp: ImageView

    }

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.keyboard_display)
        m_address = intent.getStringExtra(SettingsActivity.EXTRA_ADDRESS)!!
        //initalize all images as a no note (nothing is being played so don't display)
        note1 = findViewById<ImageView>(R.id.noNote)
        note2 = findViewById<ImageView>(R.id.noNote)
        note3 = findViewById<ImageView>(R.id.noNote)
        note4 = findViewById<ImageView>(R.id.noNote)
        note5 = findViewById<ImageView>(R.id.noNote)
        note6 = findViewById<ImageView>(R.id.noNote)
        note7 = findViewById<ImageView>(R.id.noNote)
        note8 = findViewById<ImageView>(R.id.noNote)
        note9 = findViewById<ImageView>(R.id.noNote)
        note10 = findViewById<ImageView>(R.id.noNote)
        ConnectToDevice(this).execute()//starts connectedthread thread, connects to BT

        back.setOnClickListener { disconnect() }

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


    /*
        disconnect function closes the bluetooth socket, clears the handler message queue, and leaves
        activity
     */
    private fun disconnect() {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        finish()
    }

    /*
        listens to bluetooth input stream and play corresponding note sounds
     */
    private class ConnectedThread(private val mmSocket: BluetoothSocket?, c: Context) : Thread() {
        private val mmBuffer: CharArray = CharArray(20) // mmBuffer store for the stream

        //initialize all note variables
        private var a1 = 0
        private var b1 = 0
        private var c1 = 0
        private var d1 = 0
        private var e1 = 0
        private var f1 = 0
        private var g1 = 0

        private var a2 = 0
        private var b2 = 0
        private var c2 = 0
        private var d2 = 0
        private var e2 = 0
        private var f2 = 0
        private var g2 = 0

        private var a5 = 0
        private var b5 = 0
        private var c5 = 0
        private var d5 = 0
        private var e5 = 0
        private var f5 = 0
        private var g5 = 0

        private var a6 = 0
        private var b6 = 0
        private var c6 = 0
        private var d6 = 0
        private var e6 = 0
        private var f6 = 0
        private var g6 = 0

        private var a3 = 0
        private var b3 = 0
        private var c3 = 0
        private var d3 = 0
        private var e3 = 0
        private var f3 = 0
        private var g3 = 0

        private var a4 = 0
        private var b4 = 0
        private var c4 = 0
        private var d4 = 0
        private var e4 = 0
        private var f4 = 0
        private var g4 = 0


        private var soundPool: SoundPool? = null //initalize soundpool api
        //intialize soundstreams to stop sustained notes
        private var soundStream1: Int? = 0
        private var soundStream2: Int? = 0
        private var soundStream3: Int? = 0
        private var soundStream4: Int? = 0
        private var soundStream5: Int? = 0
        private var soundStream6: Int? = 0
        private var soundStream7: Int? = 0
        private var soundStream8: Int? = 0
        private var soundStream9: Int? = 0
        private var soundStream10: Int? = 0
        private var temp: CharArray = CharArray(20)
        private var stopThread: AtomicBoolean = AtomicBoolean(true)
        var read: Int = 0
        var context: Context

        init {
            this.context = c
        }

        override fun run() {
            var c23: Int = 0
            stopThread.set(true)
            sendCommand("f\r")//tell the gloves we are in full scale mode

            /*
                initialize soundpool and all note sounds
             */
            soundPool = SoundPool(10, AudioManager.STREAM_MUSIC, 0)

            a1 = soundPool!!.load(context, R.raw.a1, 1)
            b1 = soundPool!!.load(context, R.raw.b1, 1)
            c1 = soundPool!!.load(context, R.raw.c1, 1)
            d1 = soundPool!!.load(context, R.raw.d1, 1)
            e1 = soundPool!!.load(context, R.raw.e1, 1)
            f1 = soundPool!!.load(context, R.raw.f1, 1)
            g1 = soundPool!!.load(context, R.raw.g1, 1)

            a2 = soundPool!!.load(context, R.raw.a2, 1)
            b2 = soundPool!!.load(context, R.raw.b2, 1)
            c2 = soundPool!!.load(context, R.raw.c2, 1)
            d2 = soundPool!!.load(context, R.raw.d2, 1)
            e2 = soundPool!!.load(context, R.raw.e2, 1)
            f2 = soundPool!!.load(context, R.raw.f2, 1)
            g2 = soundPool!!.load(context, R.raw.g2, 1)

            a5 = soundPool!!.load(context, R.raw.a5, 1)
            b5 = soundPool!!.load(context, R.raw.b5, 1)
            c5 = soundPool!!.load(context, R.raw.c5, 1)
            d5 = soundPool!!.load(context, R.raw.d5, 1)
            e5 = soundPool!!.load(context, R.raw.e5, 1)
            f5 = soundPool!!.load(context, R.raw.f5, 1)
            g5 = soundPool!!.load(context, R.raw.g5, 1)

            a6 = soundPool!!.load(context, R.raw.a6, 1)
            b6 = soundPool!!.load(context, R.raw.b6, 1)
            c6 = soundPool!!.load(context, R.raw.c6, 1)
            d6 = soundPool!!.load(context, R.raw.d6, 1)
            e6 = soundPool!!.load(context, R.raw.e6, 1)
            f6 = soundPool!!.load(context, R.raw.f6, 1)
            g6 = soundPool!!.load(context, R.raw.g6, 1)

            a4 = soundPool!!.load(context, R.raw.a4, 1)
            b4 = soundPool!!.load(context, R.raw.b4, 1)
            c4 = soundPool!!.load(context, R.raw.c4, 1)
            d4 = soundPool!!.load(context, R.raw.d4, 1)
            e4 = soundPool!!.load(context, R.raw.e4, 1)
            f4 = soundPool!!.load(context, R.raw.f4, 1)
            g4 = soundPool!!.load(context, R.raw.g4, 1)

            a3 = soundPool!!.load(context, R.raw.a3, 1)
            b3 = soundPool!!.load(context, R.raw.b3, 1)
            c3 = soundPool!!.load(context, R.raw.c3, 1)
            d3 = soundPool!!.load(context, R.raw.d3, 1)
            e3 = soundPool!!.load(context, R.raw.e3, 1)
            f3 = soundPool!!.load(context, R.raw.f3, 1)
            g3 = soundPool!!.load(context, R.raw.g3, 1)
            // Keep listening to the InputStream until an exception occurs.
            while (stopThread.get()) {
                // Read from the InputStream.


                try {


                    val ch = mmSocket!!.inputStream.read().toChar()
                    mmBuffer[c23] = ch
                    c23++


                } catch (e: IOException) {
                    Log.d(TAG, "Input stream was disconnected", e)
                    stopThread.set(false)
                    break
                }


                if (c23 == 20) {
                    //sets note visuals on the keyboard display
                    note1_temp = note1
                    note2_temp = note2
                    note3_temp = note3
                    note4_temp = note4
                    note5_temp = note5
                    note6_temp = note6
                    note7_temp = note7
                    note8_temp = note8
                    note9_temp = note9
                    note10_temp = note10

                    playNote(mmBuffer)
                    c23 = 0
                    Handler(Looper.getMainLooper()).post(Runnable {
                        //sets the previous notes to invisible and makes the new notes visible
                        note1_temp.visibility = View.INVISIBLE
                        note2_temp.visibility = View.INVISIBLE
                        note3_temp.visibility = View.INVISIBLE
                        note4_temp.visibility = View.INVISIBLE
                        note5_temp.visibility = View.INVISIBLE
                        note6_temp.visibility = View.INVISIBLE
                        note7_temp.visibility = View.INVISIBLE
                        note8_temp.visibility = View.INVISIBLE
                        note9_temp.visibility = View.INVISIBLE
                        note10_temp.visibility = View.INVISIBLE

                        note1.visibility = View.VISIBLE
                        note2.visibility = View.VISIBLE
                        note3.visibility = View.VISIBLE
                        note4.visibility = View.VISIBLE
                        note5.visibility = View.VISIBLE
                        note6.visibility = View.VISIBLE
                        note7.visibility = View.VISIBLE
                        note8.visibility = View.VISIBLE
                        note9.visibility = View.VISIBLE
                        note10.visibility = View.VISIBLE
                    })
                }


            }
        }


        @SuppressLint("CutPasteId")
        fun playNote(buf: CharArray) {
            var count: Int = 0

            Log.i("data", "---------------------------------------------")
            for (i in buf) {
                Log.i("data", count.toString() + " " + i.toString())
                /*
                    sets the new note visibility and plays all the notes in the buffer
                 */
                when (count) {
                    0 -> {
                        if (buf[count] == temp[count] && buf[count + 1] == temp[count + 1]) {


                        } else if (buf[count] == '0') {
                            soundPool!!.stop(soundStream1!!)
                            note1 = (context as Activity).findViewById(R.id.noNote)
                        } else if (buf[count] != temp[count] && buf[count + 1] != temp[count + 1]) {
                            soundPool!!.stop(soundStream1!!)

                            soundStream1 = noteCheck(buf[count], buf[count + 1], 1)


                        } else {
                            soundStream1 = noteCheck(buf[count], buf[count + 1], 1)

                        }

                    }
                    2 -> {
                        if (buf[count] == temp[count] && buf[count + 1] == temp[count + 1]) {


                        } else if (buf[count] == '0') {
                            soundPool!!.stop(soundStream2!!)
                            note2 = (context as Activity).findViewById(R.id.noNote)
                        } else if (buf[count] != temp[count] && buf[count + 1] != temp[count + 1]) {

                            soundPool!!.stop(soundStream2!!)
                            soundStream2 = noteCheck(buf[count], buf[count + 1], 2)


                        } else {
                            soundStream2 = noteCheck(buf[count], buf[count + 1], 2)

                        }

                    }
                    4 -> {
                        if (buf[count] == temp[count] && buf[count + 1] == temp[count + 1]) {


                        } else if (buf[count] == '0') {
                            soundPool!!.stop(soundStream3!!)
                            note3 = (context as Activity).findViewById(R.id.noNote)
                        } else if (buf[count] != temp[count] && buf[count + 1] != temp[count + 1]) {

                            soundPool!!.stop(soundStream3!!)
                            soundStream3 = noteCheck(buf[count], buf[count + 1], 3)


                        } else {
                            soundStream3 = noteCheck(buf[count], buf[count + 1], 3)

                        }

                    }
                    6 -> {
                        if (buf[count] == temp[count] && buf[count + 1] == temp[count + 1]) {

                        } else if (buf[count] == '0') {
                            soundPool!!.stop(soundStream4!!)
                            note4 = (context as Activity).findViewById(R.id.noNote)
                        } else if (buf[count] != temp[count] && buf[count + 1] != temp[count + 1]) {

                            soundPool!!.stop(soundStream4!!)
                            soundStream4 = noteCheck(buf[count], buf[count + 1], 4)


                        } else {
                            soundStream4 = noteCheck(buf[count], buf[count + 1], 4)

                        }

                    }
                    8 -> {
                        if (buf[count] == temp[count] && buf[count + 1] == temp[count + 1]) {

                        } else if (buf[count] == '0') {
                            soundPool!!.stop(soundStream5!!)
                            note5 = (context as Activity).findViewById(R.id.noNote)
                        } else if (buf[count] != temp[count] && buf[count + 1] != temp[count + 1]) {

                            soundPool!!.stop(soundStream5!!)
                            soundStream5 = noteCheck(buf[count], buf[count + 1], 5)


                        } else {
                            soundStream5 = noteCheck(buf[count], buf[count + 1], 5)

                        }

                    }
                    10 -> {
                        if (buf[count] == temp[count] && buf[count + 1] == temp[count + 1]) {

                        } else if (buf[count] == '0') {
                            soundPool!!.stop(soundStream6!!)
                            note6 = (context as Activity).findViewById(R.id.noNote)
                        } else if (buf[count] != temp[count] && buf[count + 1] != temp[count + 1]) {

                            soundPool!!.stop(soundStream6!!)
                            soundStream6 = noteCheck(buf[count], buf[count + 1], 6)


                        } else {
                            soundStream6 = noteCheck(buf[count], buf[count + 1], 6)

                        }

                    }
                    12 -> {
                        if (buf[count] == temp[count] && buf[count + 1] == temp[count + 1]) {

                        } else if (buf[count] == '0') {
                            soundPool!!.stop(soundStream7!!)
                            note7 = (context as Activity).findViewById(R.id.noNote)
                        } else if (buf[count] != temp[count] && buf[count + 1] != temp[count + 1]) {

                            soundPool!!.stop(soundStream7!!)
                            soundStream7 = noteCheck(buf[count], buf[count + 1], 7)


                        } else {
                            soundStream7 = noteCheck(buf[count], buf[count + 1], 7)

                        }

                    }
                    14 -> {
                        if (buf[count] == temp[count] && buf[count + 1] == temp[count + 1]) {

                        } else if (buf[count] == '0') {
                            soundPool!!.stop(soundStream8!!)
                            note8 = (context as Activity).findViewById(R.id.noNote)
                        } else if (buf[count] != temp[count] && buf[count + 1] != temp[count + 1]) {

                            soundPool!!.stop(soundStream8!!)
                            soundStream8 = noteCheck(buf[count], buf[count + 1], 8)


                        } else {
                            soundStream8 = noteCheck(buf[count], buf[count + 1], 8)

                        }

                    }
                    16 -> {
                        if (buf[count] == temp[count] && buf[count + 1] == temp[count + 1]) {


                        } else if (buf[count] == '0') {
                            soundPool!!.stop(soundStream9!!)
                            note9 = (context as Activity).findViewById(R.id.noNote)
                        } else if (buf[count] != temp[count] && buf[count + 1] != temp[count + 1]) {

                            soundPool!!.stop(soundStream9!!)
                            soundStream9 = noteCheck(buf[count], buf[count + 1], 9)


                        } else {
                            soundStream9 = noteCheck(buf[count], buf[count + 1], 9)

                        }

                    }
                    18 -> {
                        if (buf[count] == temp[count] && buf[count + 1] == temp[count + 1]) {

                        } else if (buf[count] == '0') {
                            soundPool!!.stop(soundStream10!!)
                            note10 = (context as Activity).findViewById(R.id.noNote)
                        } else if (buf[count] != temp[count] && buf[count + 1] != temp[count + 1]) {
                            soundPool!!.stop(soundStream10!!)

                            soundStream10 = noteCheck(buf[count], buf[count + 1], 10)


                        } else {
                            soundStream10 = noteCheck(buf[count], buf[count + 1], 10)

                        }

                    }
                }
                temp[count] = i

                count++
            }
            read = 0


            //   }


        }
        /*
            sends data to the gloves
         */
        private fun sendCommand(input: String) {
            if (m_bluetoothSocket != null) {
                try {
                    Log.i("data", input.toByteArray().toString())
                    m_bluetoothSocket!!.outputStream.write(input.toByteArray())
                    m_bluetoothSocket!!.outputStream.flush()
                    Log.i("data", "sending..")
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.i("data", "couldn't send")
                }
                return

            }

        }
        /*
            checks the note letter and note number to determine what note to play
         */
        @SuppressLint("CutPasteId")
        fun noteCheck(check1: Char, check2: Char, image: Int): Int? {
            var sndStream: Int? = 0
            if (check1 == 'a') {

                when (check2) {
                    '1' -> {
                        sndStream = soundPool?.play(a1, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.a1Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.a1Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.a1Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.a1Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.a1Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.a1Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.a1Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.a1Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.a1Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.a1Play)
                        }
                    }
                    '2' -> {
                        sndStream = soundPool?.play(a2, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.a2Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.a2Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.a2Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.a2Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.a2Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.a2Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.a2Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.a2Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.a2Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.a2Play)
                        }
                    }
                    '3' -> {
                        sndStream = soundPool?.play(a3, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.a3Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.a3Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.a3Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.a3Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.a3Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.a3Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.a3Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.a3Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.a3Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.a3Play)
                        }
                    }
                    '4' -> {
                        sndStream = soundPool?.play(a4, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.a4Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.a4Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.a4Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.a4Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.a4Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.a4Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.a4Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.a4Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.a4Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.a4Play)
                        }
                    }
                    '5' -> {
                        sndStream = soundPool?.play(a5, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.a5Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.a5Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.a5Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.a5Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.a5Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.a5Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.a5Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.a5Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.a5Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.a5Play)
                        }
                    }
                    '6' -> {
                        sndStream = soundPool?.play(a6, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.a6Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.a6Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.a6Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.a6Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.a6Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.a6Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.a6Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.a6Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.a6Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.a6Play)
                        }
                    }

                }
            } else if (check1 == 'b') {

                when (check2) {
                    '1' -> {
                        sndStream = soundPool?.play(b1, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.b1Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.b1Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.b1Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.b1Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.b1Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.b1Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.b1Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.b1Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.b1Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.b1Play)
                        }
                    }
                    '2' -> {
                        sndStream = soundPool?.play(b2, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.b2Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.b2Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.b2Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.b2Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.b2Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.b2Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.b2Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.b2Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.b2Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.b2Play)
                        }
                    }
                    '3' -> {
                        sndStream = soundPool?.play(b3, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.b3Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.b3Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.b3Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.b3Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.b3Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.b3Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.b3Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.b3Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.b3Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.b3Play)
                        }
                    }
                    '4' -> {
                        sndStream = soundPool?.play(b4, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.b4Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.b4Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.b4Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.b4Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.b4Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.b4Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.b4Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.b4Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.b4Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.b4Play)
                        }
                    }
                    '5' -> {
                        sndStream = soundPool?.play(b5, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.b5Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.b5Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.b5Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.b5Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.b5Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.b5Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.b5Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.b5Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.b5Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.b5Play)
                        }
                    }
                    '6' -> {
                        sndStream = soundPool?.play(b6, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.b6Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.b6Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.b6Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.b6Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.b6Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.b6Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.b6Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.b6Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.b6Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.b6Play)
                        }
                    }

                }
            } else if (check1 == 'c') {

                when (check2) {
                    '1' -> {
                        sndStream = soundPool?.play(c1, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.c1Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.c1Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.c1Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.c1Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.c1Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.c1Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.c1Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.c1Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.c1Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.c1Play)
                        }
                    }
                    '2' -> {
                        sndStream = soundPool?.play(c2, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.c2Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.c2Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.c2Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.c2Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.c2Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.c2Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.c2Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.c2Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.c2Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.c2Play)
                        }
                    }
                    '3' -> {
                        sndStream = soundPool?.play(c3, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.c3Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.c3Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.c3Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.c3Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.c3Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.c3Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.c3Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.c3Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.c3Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.c3Play)
                        }
                    }
                    '4' -> {
                        sndStream = soundPool?.play(c4, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.c4Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.c4Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.c4Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.c4Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.c4Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.c4Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.c4Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.c4Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.c4Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.c4Play)
                        }
                    }
                    '5' -> {
                        sndStream = soundPool?.play(c5, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.c5Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.c5Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.c5Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.c5Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.c5Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.c5Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.c5Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.c5Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.c5Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.c5Play)
                        }
                    }
                    '6' -> {
                        sndStream = soundPool?.play(c6, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.c6Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.c6Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.c6Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.c6Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.c6Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.c6Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.c6Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.c6Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.c6Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.c6Play)
                        }
                    }

                }
            } else if (check1 == 'd') {

                when (check2) {
                    '1' -> {
                        sndStream = soundPool?.play(d1, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.d1Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.d1Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.d1Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.d1Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.d1Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.d1Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.d1Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.d1Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.d1Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.d1Play)
                        }
                    }
                    '2' -> {
                        sndStream = soundPool?.play(d2, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.d2Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.d2Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.d2Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.d2Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.d2Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.d2Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.d2Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.d2Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.d2Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.d2Play)
                        }
                    }
                    '3' -> {
                        sndStream = soundPool?.play(d3, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.d3Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.d3Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.d3Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.d3Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.d3Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.d3Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.d3Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.d3Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.d3Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.d3Play)
                        }
                    }
                    '4' -> {
                        sndStream = soundPool?.play(d4, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.d4Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.d4Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.d4Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.d4Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.d4Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.d4Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.d4Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.d4Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.d4Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.d4Play)
                        }
                    }
                    '5' -> {
                        sndStream = soundPool?.play(d5, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.d5Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.d5Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.d5Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.d5Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.d5Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.d5Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.d5Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.d5Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.d5Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.d5Play)
                        }
                    }
                    '6' -> {
                        sndStream = soundPool?.play(d6, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.d6Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.d6Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.d6Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.d6Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.d6Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.d6Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.d6Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.d6Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.d6Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.d6Play)
                        }
                    }

                }
            } else if (check1 == 'e') {

                when (check2) {
                    '1' -> {
                        sndStream = soundPool?.play(e1, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.e1Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.e1Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.e1Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.e1Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.e1Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.e1Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.e1Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.e1Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.e1Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.e1Play)
                        }
                    }
                    '2' -> {
                        sndStream = soundPool?.play(e2, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.e2Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.e2Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.e2Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.e2Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.e2Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.e2Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.e2Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.e2Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.e2Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.e2Play)
                        }
                    }
                    '3' -> {
                        sndStream = soundPool?.play(e3, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.e3Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.e3Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.e3Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.e3Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.e3Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.e3Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.e3Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.e3Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.e3Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.e3Play)
                        }
                    }
                    '4' -> {
                        sndStream = soundPool?.play(e4, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.e4Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.e4Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.e4Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.e4Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.e4Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.e4Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.e4Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.e4Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.e4Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.e4Play)
                        }
                    }
                    '5' -> {
                        sndStream = soundPool?.play(e5, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.e5Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.e5Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.e5Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.e5Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.e5Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.e5Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.e5Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.e5Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.e5Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.e5Play)
                        }
                    }
                    '6' -> {
                        sndStream = soundPool?.play(e6, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.e6Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.e6Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.e6Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.e6Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.e6Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.e6Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.e6Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.e6Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.e6Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.e6Play)
                        }
                    }

                }
            } else if (check1 == 'f') {

                when (check2) {
                    '1' -> {
                        sndStream = soundPool?.play(f1, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.f1Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.f1Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.f1Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.f1Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.f1Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.f1Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.f1Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.f1Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.f1Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.f1Play)
                        }
                    }
                    '2' -> {
                        sndStream = soundPool?.play(f2, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.f2Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.f2Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.f2Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.f2Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.f2Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.f2Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.f2Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.f2Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.f2Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.f2Play)
                        }
                    }
                    '3' -> {
                        sndStream = soundPool?.play(f3, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.f3Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.f3Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.f3Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.f3Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.f3Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.f3Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.f3Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.f3Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.f3Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.f3Play)
                        }
                    }
                    '4' -> {
                        sndStream = soundPool?.play(f4, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.f4Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.f4Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.f4Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.f4Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.f4Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.f4Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.f4Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.f4Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.f4Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.f4Play)
                        }
                    }
                    '5' -> {
                        sndStream = soundPool?.play(f5, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.f5Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.f5Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.f5Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.f5Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.f5Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.f5Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.f5Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.f5Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.f5Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.f5Play)
                        }
                    }
                    '6' -> {
                        sndStream = soundPool?.play(f6, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.f6Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.f6Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.f6Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.f6Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.f6Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.f6Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.f6Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.f6Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.f6Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.f6Play)
                        }
                    }

                }
            } else if (check1 == 'g') {

                when (check2) {
                    '1' -> {
                        sndStream =
                            soundPool?.play(g1, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.g1Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.g1Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.g1Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.g1Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.g1Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.g1Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.g1Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.g1Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.g1Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.g1Play)
                        }
                    }
                    '2' -> {
                        sndStream =
                            soundPool?.play(g2, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.g2Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.g2Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.g2Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.g2Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.g2Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.g2Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.g2Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.g2Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.g2Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.g2Play)
                        }
                    }
                    '3' -> {
                        sndStream =
                            soundPool?.play(g3, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.g3Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.g3Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.g3Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.g3Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.g3Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.g3Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.g3Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.g3Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.g3Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.g3Play)
                        }
                    }
                    '4' -> {
                        sndStream =
                            soundPool?.play(g4, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.g4Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.g4Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.g4Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.g4Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.g4Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.g4Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.g4Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.g4Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.g4Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.g4Play)
                        }
                    }
                    '5' -> {
                        sndStream =
                            soundPool?.play(g5, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.g5Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.g5Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.g5Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.g5Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.g5Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.g5Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.g5Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.g5Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.g5Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.g5Play)
                        }
                    }
                    '6' -> {
                        sndStream =
                            soundPool?.play(g6, 1F, 1F, 0, 0, 1F)
                        when (image) {
                            1 -> note1 = (context as Activity).findViewById(R.id.g6Play)
                            2 -> note2 = (context as Activity).findViewById(R.id.g6Play)
                            3 -> note3 = (context as Activity).findViewById(R.id.g6Play)
                            4 -> note4 = (context as Activity).findViewById(R.id.g6Play)
                            5 -> note5 = (context as Activity).findViewById(R.id.g6Play)
                            6 -> note6 = (context as Activity).findViewById(R.id.g6Play)
                            7 -> note7 = (context as Activity).findViewById(R.id.g6Play)
                            8 -> note8 = (context as Activity).findViewById(R.id.g6Play)
                            9 -> note9 = (context as Activity).findViewById(R.id.g6Play)
                            10 -> note10 = (context as Activity).findViewById(R.id.g6Play)
                        }
                    }

                }
            }

            return sndStream
        }

    }


    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context, "Connecting...", "please wait")
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if (m_bluetoothSocket == null || !m_isConnected) {
                    m_bluetoothAdapater = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapater.getRemoteDevice(
                        m_address
                    )
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(
                        m_myUUID
                    )
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()

                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                Log.i("data", "couldn't connect")
            } else {
                m_isConnected = true
                m_bluetoothSocket!!.outputStream.write("f\r".toByteArray())
                m_bluetoothSocket!!.outputStream.flush()
                var connectedthread = ConnectedThread(m_bluetoothSocket, context)
                connectedthread.start()
                Log.i("data", "connected")
            }
            m_progress.dismiss()


        }

    }

}

