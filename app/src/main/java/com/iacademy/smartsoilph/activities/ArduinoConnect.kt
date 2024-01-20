package com.iacademy.smartsoilph.activities

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.*

private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

class BluetoothSensorService(private val context: Context, private val uuid: UUID) {
    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket: BluetoothSocket? = null
    private var deviceAddress: String? = null
    private val dbHelper: SensorDataDbHelper = SensorDataDbHelper(context)

    fun connectToDevice(address: String) {
        val device: BluetoothDevice? = bluetoothAdapter?.getRemoteDevice(address)
        try {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            bluetoothSocket = device?.createRfcommSocketToServiceRecord(uuid)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            bluetoothSocket?.connect()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun readSensorData() {
        val buffer = ByteArray(1024)
        var bytes: Int

        try {
            val inputStream = bluetoothSocket?.inputStream
            if (inputStream != null) {
                bytes = inputStream.read(buffer)
                val data = String(buffer, 0, bytes)
                processSensorData(data)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun processSensorData(data: String) {
        val parts = data.split(",")
        if (parts.size >= 7) {
            val moisture = parts[0]
            val temperature = parts[1]

            if (moisture.isInt() && temperature.isFloat()) {
                dbHelper.insertSensorData(moisture.toInt(), temperature.toFloat())
            }
        }
    }

    companion object {
        private val MY_UUID: UUID = uuid
    }
}

private fun String.isInt(): Boolean {
    return try {
        this.toInt()
        true
    } catch (e: NumberFormatException) {
        false
    }
}

private fun String.isFloat(): Boolean {
    return try {
        this.toFloat()
        true
    } catch (e: NumberFormatException) {
        false
    }
}