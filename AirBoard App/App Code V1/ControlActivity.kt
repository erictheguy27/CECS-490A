package com.example.airboard

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.control_layout.*
import org.jetbrains.anko.toast
import java.io.IOException
import java.util.*

class ControlActivity: AppCompatActivity() {

    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapater: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var  m_address: String
        private const val TAG = "MY_APP_DEBUG_TAG"
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_layout)
        m_address = intent.getStringExtra(SettingsActivity.EXTRA_ADDRESS)!!
        ConnectToDevice(this).execute()

        control_led_on.setOnClickListener { sendCommand("1") }
        control_led_off.setOnClickListener { sendCommand("0") }
        control_led_disconnect.setOnClickListener { disconnect() }
    }
/* Function: sendCommand
   Input: Takes in a string, converts it into a byte array
   Sends a byte array to the TM4C to evaluate
*/
   private fun sendCommand(input: String){
        if (m_bluetoothSocket != null){
            try {
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
                Log.i("data", "sending..")
            } catch (e: IOException) {
                e.printStackTrace()
                Log.i("data", "couldn't send")
                }
                return

            }

    }
    private fun disconnect(){
        if (m_bluetoothSocket != null){
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

    private class ConnectedThread(private val mmSocket: BluetoothSocket?, c : Context) : Thread() {
        private val mmBuffer: ByteArray = ByteArray(5) // mmBuffer store for the stream
        private var note1 = 0
        private var note2 = 0
        private var note3 = 0
        private var note4 = 0
        private var note5 = 0
        private var soundPool: SoundPool? = null
        private var temp : ByteArray = ByteArray(5)
        var context: Context
        init{
            this.context = c
        }

        override fun run() {
            var numBytes: Int = 0// bytes returned from read()
            soundPool = SoundPool(10, AudioManager.STREAM_MUSIC, 0)
            note1 = soundPool!!.load(context,R.raw.c4, 1)
            note2 = soundPool!!.load(context,R.raw.d4, 1)
            note3 = soundPool!!.load(context,R.raw.e4, 1)
            note4 = soundPool!!.load(context,R.raw.f4, 1)
            note5 = soundPool!!.load(context,R.raw.g4, 1)
            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                // Read from the InputStream.
                try {
                    numBytes =
                        mmSocket!!.inputStream.read(mmBuffer, numBytes, mmBuffer.size - numBytes)
                    /*for(i in begin..numBytes){
                        if(mmBuffer[i] == "\n".toByte()){
                            handler.obtainMessage(1,begin,i,mmBuffer).sendToTarget()
                            begin = i + 1
                        }
                        if(i == numBytes-1){
                            numBytes = 0
                            begin = 0
                        }
                    }*/

                } catch (e: IOException) {
                    Log.d(TAG, "Input stream was disconnected", e)
                    break
                }

                playNote(mmBuffer)


                //val readMsg = handler.obtainMessage(0,numBytes,-1,mmBuffer )
                // Send the obtained bytes to the UI activity.

            }
        }
        fun playNote( buf: ByteArray) {
            var count : Int = 0

            for( i in buf) {
                if(i == '1'.toByte()) {
                    when (count) {
                       // 0 -> soundPool?.play(note1, 1F, 1F, 0, 0, 1F)
                        1 -> soundPool?.play(note2, 1F, 1F, 0, 0, 1F)


                        2 -> soundPool?.play(note3, 1F, 1F, 0, 0, 1F)

                        3 -> soundPool?.play(note4, 1F, 1F, 0, 0, 1F)

                        4 -> soundPool?.play(note5, 1F, 1F, 0, 0, 1F)


                    }
                }
                count++
            }
            temp = buf



        }

    }




    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>(){
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = c
        }
        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context, "Connecting...", "please wait")
        }
        override fun doInBackground(vararg p0: Void?) : String? {
            try {
                if (m_bluetoothSocket == null || !m_isConnected){
                    m_bluetoothAdapater = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapater.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()

                }
            } catch (e: IOException){
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(!connectSuccess){
                Log.i("data", "couldn't connect")
            } else {
                m_isConnected = true
                var connectedthread= ConnectedThread(m_bluetoothSocket, context)
                connectedthread.start()
                Log.i("data", "connected")
            }
            m_progress.dismiss()


        }

    }
}