package com.example.airboard

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.settings.*
import org.jetbrains.anko.toast

class SettingsActivity : AppCompatActivity() {

    private var m_bluetoothAdapter: BluetoothAdapter? = null //bluetooth adapter
    private lateinit var m_pairedDevices: Set<BluetoothDevice> //get the paired devices
    private val REQUEST_ENEABLE_BLUETOOTH = 1 // requesting to eneable bluetooth

    companion object {
        val EXTRA_ADDRESS: String = "Device_Address" //the device address
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter() //gets the adapter
        if(m_bluetoothAdapter == null){
            toast("This device does not support bluetooth")
            return
        }
        if(!m_bluetoothAdapter!!.isEnabled){
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENEABLE_BLUETOOTH)
        }//if the adapter is not enable, ask to enable it
        //press button to refresh the bluetooth list
        select_device_refresh.setOnClickListener { pairedDeviceList() }

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
    private fun pairedDeviceList() {
        m_pairedDevices = m_bluetoothAdapter!!.bondedDevices
        val list: ArrayList<String> = ArrayList() //list of bluetooth devices paired
        //val list: ArrayList<BluetoothDevice> = ArrayList()
        if (!m_pairedDevices.isEmpty()) {
            for (device: BluetoothDevice in m_pairedDevices) {
                list.add("Name: " + device.name + "\n" + "MAC Address "+ device.address)
                Log.i("device", "" + device.name + device.address)
            } //displays the name of the device and address

        } else {
            toast("no paired bluetooth devices found")
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        select_device_list.adapter = adapter
        select_device_list.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val device: String = list[position]
                var address: String = ""
                val name: String = ""
                var checkadd = device.length - 17
                while(checkadd != device.length){
                    address+= device[checkadd]
                    checkadd++
                }//gets address portion of device
                Log.i("device", "" + address)
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(SettingsActivity.EXTRA_ADDRESS, address)
                startActivity(intent)
            }//after selecting device, send address to main activity
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENEABLE_BLUETOOTH){
            if(resultCode == Activity.RESULT_OK) {
                if (m_bluetoothAdapter!!.isEnabled) {
                    toast("Bluetooth has been enabled")
                } else {
                    toast("Bluetooth has not been enabled")
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                toast("Bluetooth enabling has ben canceled")
            }
        }
    }
}