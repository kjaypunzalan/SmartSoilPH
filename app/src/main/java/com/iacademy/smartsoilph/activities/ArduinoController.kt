package com.iacademy.smartsoilph.activities

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.arduino.BluetoothController

class ArduinoController : AppCompatActivity() {

    private lateinit var bluetoothController: BluetoothController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arduino_control)

        bluetoothController = BluetoothController(this)

        if (bluetoothController.connect()) {
            // Connected to HM-10
        } else {
            // Failed to connect
        }

        val buttonSendOne: Button = findViewById(R.id.button_send_one)
        buttonSendOne.setOnClickListener {
            bluetoothController.sendData("1")
        }

        val buttonSendZero: Button = findViewById(R.id.button_send_zero)
        buttonSendZero.setOnClickListener {
            bluetoothController.sendData("0")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothController.disconnect()
    }
}
