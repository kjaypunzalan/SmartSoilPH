// Overwrite the content of this file with the following code
package com.iacademy.smartsoilph.arduino

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.iacademy.smartsoilph.R

class ArduinoController : AppCompatActivity() {

    private lateinit var bluetoothController: BluetoothController
    private lateinit var receivedDataTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arduino_control)

        receivedDataTextView = findViewById(R.id.received_data_text_view)

        bluetoothController = BluetoothController(this).apply {
            setDataListener(object : BluetoothController.BluetoothDataListener {
                override fun onDataReceived(data: String) {
                    runOnUiThread {
                        appendReceivedData(data)
                    }
                }
            })
        }

        if (!bluetoothController.connect()) {
            // Show a message if failed to connect
            receivedDataTextView.text = "Failed to connect to the device"
        }

        val buttonSendOne: Button = findViewById(R.id.button_send_one)
        buttonSendOne.setOnClickListener {
            // Send command "1" to Arduino
            bluetoothController.sendCommand("1")
        }
    }

    private fun appendReceivedData(data: String) {
        receivedDataTextView.append("\n$data")
        // Scroll to the bottom of the TextView
        receivedDataTextView.post {
            receivedDataTextView.scrollTo(0, receivedDataTextView.top)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothController.disconnect()
    }
}

