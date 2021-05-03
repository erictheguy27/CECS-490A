package com.example.airboard

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.kids_mode.*
import java.io.IOException
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class KidsModeActivity : AppCompatActivity() {
    companion object {
        //standard UUID
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null //bluetooth socket to connect to
        lateinit var m_progress: ProgressDialog //progress bar of connection
        lateinit var m_bluetoothAdapater: BluetoothAdapter //bluetooth adapter
        var m_isConnected: Boolean = false //check if bluetooth is connected
        lateinit var m_address: String //address of the bluetooth
        private const val TAG = "MY_APP_DEBUG_TAG"
        var soundPool: SoundPool? = null //soundpool variable for sounds
        var read: Int = 0 //read flag to playback songs
        var sendBString: String = "" //initialize string that will be sent to the gloves
        var learn: Int = 0 //learn flag to learn selected song
        var noteCnt: Int = 0 //counter for element index for song playback
        var noteCntLearn: Int = 0 // counter for element index for song learning
        var strCheck: String = "" //check if the note is already being played
        var sendToBoard: CharArray =
            charArrayOf('0', '0', '0', '0', '0', '0', '0', '0', '0', '0')
        //empty array to convert abcdefghij to 0s and 1s for the gloves
        var sendBoard: String = "" //string of variable sendToBoard to send to gloves
        var waitFor: Int = 0 //waiting flag that waits for user to input the correct notes
        var stopping: Int = 0 //stopping flag that will tell the threads to close upon leaving page

        /*
        right hand and left hand variables are used to hold the current song the user selects
        we store each of the songs right hand and left hand notes into string arrays
         */
        var rightHand = arrayOf(String())
        var leftHand = arrayOf(String())
        val saintsRight = arrayOf(
            "f", "", "h", "", "i", "", "j", "",
            "", "", "", "", "", "", "", "",
            "f", "", "h", "", "i", "", "j", "",
            "", "", "", "", "", "", "", "",
            "f", "", "h", "", "i", "", "j", "",
            "", "", "h", "", "", "", "f", "",
            "", "", "h", "", "", "", "g", "",
            "", "", "", "", "", "", "", "",
            "", "", "h", "", "g", "", "f", "",
            "", "", "f", "", "", "", "h", "",
            "", "", "j", "", "", "", "j", "",
            "i", "", "", "", "", "", "", "",
            "", "", "h", "", "i", "", "j", "",
            "", "", "h", "", "", "", "g", "",
            "", "", "g", "", "", "", "f", "",
            "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", ""
        )
        val saintsLeft = arrayOf(
            "", "", "", "", "", "", "", "",
            "ace", "", "ace", "", "ace", "", "ace", "",
            "", "", "", "", "", "", "", "",
            "ace", "", "ace", "", "ace", "", "ace", "",
            "", "", "", "", "", "", "", "",
            "ace", "", "", "", "ace", "", "", "",
            "ace", "", "", "", "ace", "", "", "",
            "de", "", "de", "", "de", "", "de", "",
            "", "", "", "", "", "", "", "",
            "ace", "", "", "", "ace", "", "", "",
            "ace", "", "", "", "ace", "", "", "",
            "", "", "ad", "", "ad", "", "ad", "",
            "", "", "", "", "", "", "", "",
            "ace", "", "", "", "ace", "", "", "",
            "de", "", "", "", "de", "", "", "",
            "ace", "", "ace", "", "ace", "", "ace", "",
            "", "", "", "", "", "", "", ""
        )
        val odeRight = arrayOf(
            "h", "", "h", "", "i", "", "j", "",
            "j", "", "i", "", "h", "", "g", "",
            "f", "", "f", "", "g", "", "h", "",
            "h", "", "", "g", "g", "", "", "",
            "h", "", "h", "", "i", "", "j", "",
            "j", "", "i", "", "h", "", "g", "",
            "f", "", "f", "", "g", "", "h", "",
            "g", "", "", "f", "f", "", "", "",
            "g", "", "g", "", "h", "", "f", "",
            "g", "", "h", "i", "h", "", "f", "",
            "g", "", "h", "i", "h", "", "g", "",
            "f", "", "g", "", "", "", "", "",
            "h", "", "h", "", "i", "", "j", "",
            "j", "", "i", "", "h", "", "g", "",
            "f", "", "f", "", "g", "", "h", "",
            "g", "", "", "f", "f", "", "", ""
        )
        val odeLeft = arrayOf(
            "a", "", "", "", "", "", "", "",
            "e", "", "", "", "", "", "", "",
            "a", "", "", "", "", "", "", "",
            "e", "", "", "", "", "", "", "",
            "a", "", "", "", "", "", "", "",
            "e", "", "", "", "", "", "", "",
            "a", "", "", "", "", "", "", "",
            "e", "", "", "", "ace", "", "", "",
            "e", "", "", "", "", "", "", "",
            "e", "", "", "", "", "", "", "",
            "e", "", "", "", "", "", "", "",
            "", "", "", "", "e", "", "", "",
            "a", "", "", "", "", "", "", "",
            "e", "", "", "", "", "", "", "",
            "a", "", "", "", "", "", "", "",
            "e", "", "", "", "ace", "", "", ""
        )
        val jingleRight = arrayOf(
            "h", "","h","","h","","","h","","h","","h","","","h","","j","","f","",
            "g","","h","","","","","i","","i","","i","","i","","i","","h","","h",
            "","h","","h","","g","","g","","h","","g","","","j","","","h", "","h",
            "","h","","","h","","h","","h","","","h","","j","","f","","g","","h",
            "","","","","i","","i","","i","","i","","i","","h","","h","","h","","j",
            "","j","","i","","g","","f",""
        )

        val jingleLeft = arrayOf(
            "ae","","","","","","","ae","","","","","","","ae","",
            "","","","","","","ae","","","","","","","","","","","","","","","","","","",
            "","","","","","","","","","","","","","","","","ae","","","","","","", "ae",
            "","","","","","","ae","","","","","","","","ae","","","","","","","","","",
            "","","","","","","","","","","","de","","","","","","","","ce",""
        )

        val maryRight = arrayOf(
            "h", "", "g", "", "f", "", "g", "", "h",
            "", "h", "", "h", "", "", "", "g", "", "g", "", "g", "", "",
            "", "h", "", "j", "", "j", "", "", "", "h", "", "g", "", "f",
            "", "g", "", "h", "", "h", "", "h", "", "h", "", "g", "", "g", "", "h", "", "g", "",
            "f", ""
        )
        val maryLeft = arrayOf(
            "ce", "", "", "", "", "", "", "", "ce", "", "", "", "", "", "",
            "", "be", "", "", "", "", "", "", "", "ce", "", "", "", "", "",
            "", "", "ce", "", "", "", "", "", "", "", "ce", "", "", "", "",
            "", "", "", "be", "", "", "", "", "", "", "", "ce", "", "", "",
            "", "", "", ""
        )

        //initialize variables to play sounds.
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

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        stopping = 0

        super.onCreate(savedInstanceState)
        setContentView(R.layout.kids_mode)
        m_address = intent.getStringExtra(SettingsActivity.EXTRA_ADDRESS)!!//gets BT address
        ConnectToDeviceKids(this).execute()//connects to bluetooth device and starts threads
        soundPool = SoundPool(10, AudioManager.STREAM_MUSIC, 0)
        //intializes soundPool api and sets a maximum of 10 sounds being able to be played
        //simultaneously

        //initalize all note sounds into variables
        a4 = soundPool!!.load(this, R.raw.a4, 1)
        b4 = soundPool!!.load(this, R.raw.b4, 1)
        c4 = soundPool!!.load(this, R.raw.c4, 1)
        d4 = soundPool!!.load(this, R.raw.d4, 1)
        e4 = soundPool!!.load(this, R.raw.e4, 1)
        f4 = soundPool!!.load(this, R.raw.f4, 1)
        g4 = soundPool!!.load(this, R.raw.g4, 1)

        a3 = soundPool!!.load(this, R.raw.a3, 1)
        b3 = soundPool!!.load(this, R.raw.b3, 1)
        c3 = soundPool!!.load(this, R.raw.c3, 1)
        d3 = soundPool!!.load(this, R.raw.d3, 1)
        e3 = soundPool!!.load(this, R.raw.e3, 1)
        f3 = soundPool!!.load(this, R.raw.f3, 1)
        g3 = soundPool!!.load(this, R.raw.g3, 1)
        kidsBack.setOnClickListener{
            stopping = 1 //tell the threads to close
            disconnect() //runs disconnect function to clear all buffers and safely dc BT
        }
        /*
        setOnClickListener performs actions upon clicking the corresponding button
        if a play button is tapped, it will set the read flag to 1 and playback
        the song that the user chose

        if a learn button is clicked, the learn flag will be set to 1.
        It will wait for the user to flex the correct fingers, then move on to the next note
         */
        mary_play.setOnClickListener {

            read = 1
            noteCnt = 0
            rightHand = maryRight
            leftHand = maryLeft
        }
        mary_learn.setOnClickListener {

            learn = 1
            noteCntLearn = 0
            rightHand = maryRight
            leftHand = maryLeft
        }
        jingle_play.setOnClickListener {

            read = 1
            noteCnt = 0
            rightHand = jingleRight
            leftHand = jingleLeft
        }
        jingle_learn.setOnClickListener {
            learn = 1
            noteCntLearn = 0
            rightHand = jingleRight
            leftHand = jingleLeft


        }
        ode_play.setOnClickListener {

            read = 1
            noteCnt = 0
            rightHand = odeRight
            leftHand = odeLeft
        }
        ode_learn.setOnClickListener {

            learn = 1
            noteCntLearn = 0
            rightHand = odeRight
            leftHand = odeLeft
        }
        saints_play.setOnClickListener {

            read = 1
            noteCnt = 0
            rightHand = saintsRight
            leftHand = saintsLeft
        }
        saints_learn.setOnClickListener {

            learn = 1
            noteCntLearn = 0
            rightHand = saintsRight
            leftHand = saintsLeft
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


/*
    disconnect function closes the bluetooth socket, clears the handler message queue, and leaves
    activity
 */
    private fun disconnect() {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                Handler(Looper.getMainLooper()).removeCallbacksAndMessages(null)
                m_bluetoothSocket = null
                m_isConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        finish()
    }

    private class ConnectedThreadKids(private val mmSocket: BluetoothSocket?, c: Context) :
        Thread() {
        private val mmBuffer: CharArray = CharArray(10) // mmBuffer store for the stream
        var context: Context
        private val temp: CharArray = CharArray(10)//stores last set of notes played
        private var stopThread: AtomicBoolean = AtomicBoolean(true)
        init {
            this.context = c
        }


        override fun run() {
            var c23: Int = 0
            stopThread.set(true)

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
                    break//closes thread
                }


                if (c23 == 10) {

                    kidsPlayNote(mmBuffer)
                    /*
                    checks if the notes that user is currently playing, matches the
                    right and left hand arrays of the current index of the song
                     */
                    if (learn == 1) {
                        for (string in mmBuffer) {
                            strCheck += string

                        }
                        strCheck = convertStringtoBuffer(strCheck)
                        Log.i("data", strCheck + " " + sendBString)
                        if (strCheck == sendBString) {
                            noteCntLearn++
                            waitFor = 0
                        }
                    }
                    c23 = 0

                }
            }

        }

        /*
        kidsPlayNote function plays all the notes corresponding to the fingers the user flexed
         */
        fun kidsPlayNote(notes: CharArray) {
            var element: Int = 0
            for (note in notes) {
                if (note !in temp) {
                    when (note) {
                        'a' -> soundPool?.play(c3, 1F, 1F, 0, 0, 1F)
                        'b' -> soundPool?.play(d3, 1F, 1F, 0, 0, 1F)
                        'c' -> soundPool?.play(e3, 1F, 1F, 0, 0, 1F)
                        'd' -> soundPool?.play(f3, 1F, 1F, 0, 0, 1F)
                        'e' -> soundPool?.play(g3, 1F, 1F, 0, 0, 1F)
                        'f' -> soundPool?.play(c4, 1F, 1F, 0, 0, 1F)
                        'g' -> soundPool?.play(d4, 1F, 1F, 0, 0, 1F)
                        'h' -> soundPool?.play(e4, 1F, 1F, 0, 0, 1F)
                        'i' -> soundPool?.play(f4, 1F, 1F, 0, 0, 1F)
                        'j' -> soundPool?.play(g4, 1F, 1F, 0, 0, 1F)

                    }

                }
                temp[element] = note
                element++


            }
        }
        /*
            convertStringtoBuffer takes the notes being played represented as abcdefghij
            and converts it to 1s and 0s for the board to read and turn on the corresponding
            LEDs
         */
        fun convertStringtoBuffer(convert: String): String {
            sendToBoard = charArrayOf('0', '0', '0', '0', '0', '0', '0', '0', '0', '0')
            sendBoard = ""
            for (letter in convert) {
                when (letter) {
                    'a' -> sendToBoard[0] = '1'
                    'b' -> sendToBoard[1] = '1'
                    'c' -> sendToBoard[2] = '1'
                    'd' -> sendToBoard[3] = '1'
                    'e' -> sendToBoard[4] = '1'
                    'f' -> sendToBoard[5] = '1'
                    'g' -> sendToBoard[6] = '1'
                    'h' -> sendToBoard[7] = '1'
                    'i' -> sendToBoard[8] = '1'
                    'j' -> sendToBoard[9] = '1'
                }
            }
            for (string in sendToBoard) {
                sendBoard += string

            }
            Log.i("data", sendBoard)
            return (sendBoard)


        }


    }
    /*
        ListenThread serves as the playback thread
     */
    private class ListenThread(c: Context) :
        Thread() {
        var context: Context
        private var stopThread: AtomicBoolean = AtomicBoolean(true)

        init {
            this.context = c
        }

        override fun run() {

            sendCommand("k\r")//tell the gloves we are in kid's mode mode
            stopThread.set(true)
            // Keep listening to the InputStream until an exception occurs.
            while (stopThread.get()) {
                // Read from the InputStream.
                if (read == 0) {
                    try {
                        /*
                            if learning, only go through the elements with notes, skip rests
                         */
                        if (learn == 1 && waitFor == 0) {
                            if (noteCntLearn < rightHand.size - 1) {
                                if (rightHand[noteCntLearn - 1] == "" && (noteCntLearn != 0)) {
                                    while ((rightHand[noteCntLearn] == "") && (leftHand[noteCntLearn] == "")) {
                                        noteCntLearn++
                                    }
                                }

                                sendBString =
                                    convertStringtoBuffer(rightHand[noteCntLearn].plus(leftHand[noteCntLearn]))
                                sendCommand(sendBString.plus("\r"))
                                waitFor = 1

                            } else {
                                Log.i("Learning Song", "Done!!")
                                sendCommand("0000000000\r")
                                learn = 0
                            } //once done, clear LEDs
                        }


                    } catch (e: IOException) {
                        Log.d(TAG, "Input stream was disconnected", e)
                        break
                    }

                } else if (read == 1) {
                    noteCnt = 0
                    read = 0
                    playSong()
                    Handler(Looper.getMainLooper()).removeCallbacksAndMessages(null)
                    sendCommand("0000000000\r")

                }//if read, playback the whole song then clear message queue
                if(stopping == 1){
                    stopThread.set(false)//set while loop flag to false
                    break //breaks to close thread
                }

            }
        }

        fun convertStringtoBuffer(convert: String): String {
            sendToBoard = charArrayOf('0', '0', '0', '0', '0', '0', '0', '0', '0', '0')
            sendBoard = ""
            for (letter in convert) {
                when (letter) {
                    'a' -> sendToBoard[0] = '1'
                    'b' -> sendToBoard[1] = '1'
                    'c' -> sendToBoard[2] = '1'
                    'd' -> sendToBoard[3] = '1'
                    'e' -> sendToBoard[4] = '1'
                    'f' -> sendToBoard[5] = '1'
                    'g' -> sendToBoard[6] = '1'
                    'h' -> sendToBoard[7] = '1'
                    'i' -> sendToBoard[8] = '1'
                    'j' -> sendToBoard[9] = '1'
                }
            }
            for (string in sendToBoard) {
                sendBoard += string

            }
            Log.i("data", sendBoard)
            return (sendBoard)


        }
        /*
            sends data to the gloves
         */
        private fun sendCommand(input: String) {
            if (m_bluetoothSocket != null) {
                try {
                    Log.i("data", input.toByteArray().toString())
                    m_bluetoothSocket!!.outputStream.write(input.toByteArray())
                    m_bluetoothSocket!!.outputStream.flush()//flush outputstream buffer
                    Log.i("data", "sending..")
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.i("data", "couldn't send")
                }
                return

            }

        }
        /*
            runs a playback of a song. goes through every index in the right and left hand
            arrays every 250ms
         */
        fun playSong() {
            val myHandler = Handler(Looper.getMainLooper())
            if (noteCnt < rightHand.size - 1 && stopping == 0) {
                //send the current notes being played to board
                sendBString = convertStringtoBuffer(rightHand[noteCnt].plus(leftHand[noteCnt]))
                sendCommand(sendBString.plus("\r"))//add carriage return
                kidsPlayNote(rightHand[noteCnt])//plays the current right hand
                kidsPlayNote(leftHand[noteCnt])//plays the current left hand
                noteCnt++//increment element index
                /*
                handler.postDelayed() will recursively run every 250ms until the song is finished
                 */
                myHandler.postDelayed({
                    Log.i("delay", "worked")
                    playSong()

                }, 250)

            }
            else {
                //once the song is finished, or the threads are interrupted to stop
                //clear the handler message queue
                sendCommand("0000000000\r")
                myHandler.removeCallbacksAndMessages(null)
            }
        }

        fun kidsPlayNote(notes: String) {
            for (note in notes) {
                when (note) {
                    'a' -> soundPool?.play(c3, 1F, 1F, 0, 0, 1F)
                    'b' -> soundPool?.play(d3, 1F, 1F, 0, 0, 1F)
                    'c' -> soundPool?.play(e3, 1F, 1F, 0, 0, 1F)
                    'd' -> soundPool?.play(f3, 1F, 1F, 0, 0, 1F)
                    'e' -> soundPool?.play(g3, 1F, 1F, 0, 0, 1F)
                    'f' -> soundPool?.play(c4, 1F, 1F, 0, 0, 1F)
                    'g' -> soundPool?.play(d4, 1F, 1F, 0, 0, 1F)
                    'h' -> soundPool?.play(e4, 1F, 1F, 0, 0, 1F)
                    'i' -> soundPool?.play(f4, 1F, 1F, 0, 0, 1F)
                    'j' -> soundPool?.play(g4, 1F, 1F, 0, 0, 1F)

                }

            }
        }


    }

    private class ConnectToDeviceKids(c: Context) : AsyncTask<Void, Void, String>() {
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
                m_bluetoothSocket!!.outputStream.write("k\r".toByteArray())
                m_bluetoothSocket!!.outputStream.flush()
                var connectedthread =
                    ConnectedThreadKids(m_bluetoothSocket, context)
                connectedthread.start()
                var listening =
                    ListenThread(context)
                listening.start()
                Log.i("data", "connected")
            }
            m_progress.dismiss()


        }

    }
}