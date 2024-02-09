package com.iacademy.smartsoilph.activities

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.arduino.BluetoothController


class ArduinoController : AppCompatActivity() {

    private lateinit var bluetoothController: BluetoothController
    private var lastReceivedData: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arduino_control)

        bluetoothController = BluetoothController(this).apply {
            setDataListener(object : BluetoothController.BluetoothDataListener {
                override fun onDataReceived(data: String) {
                    // Update the last received data
                    lastReceivedData = data
                }
            })
        }

        if (!bluetoothController.connect()) {
            Toast.makeText(this, "Failed to connect to the device", Toast.LENGTH_SHORT).show()
        }

        val buttonSendOne: Button = findViewById(R.id.button_send_one)
        buttonSendOne.setOnClickListener {
            // Display the last received data as a toast message
            lastReceivedData?.let { data ->
                Toast.makeText(this@ArduinoController, data, Toast.LENGTH_LONG).show()
            } ?: Toast.makeText(this@ArduinoController, "No data received yet", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothController.disconnect()
    }
}