package com.iacademy.smartsoilph.arduino

import android.bluetooth.*
import android.content.Context
import android.util.Log
import java.util.UUID

class BluetoothController(private val context: Context) {

    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothGatt: BluetoothGatt? = null
    private val hc10MacAddress = "88:4A:EA:98:22:E7" // Replace with your HM-10 MAC address
    private val serviceUUID = UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB") // Replace with your HM-10 service UUID
    private val charUUID = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB") // Replace with your HM-10 characteristic UUID

    fun connect(): Boolean {
        bluetoothAdapter?.let {
            val device = it.getRemoteDevice(hc10MacAddress)
            bluetoothGatt = device.connectGatt(context, false, gattCallback)
            return true
        }
        return false
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("BluetoothController", "Connected to GATT server.")
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("BluetoothController", "Disconnected from GATT server.")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BluetoothController", "Services Discovered.")
            } else {
                Log.w("BluetoothController", "onServicesDiscovered received: $status")
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BluetoothController", "Characteristic written successfully")
            } else {
                Log.d("BluetoothController", "Characteristic write failed: $status")
            }
        }
    }

    fun sendData(data: String) {
        if (data != "1" && data != "0") {
            Log.d("BluetoothController", "Invalid data. Only '1' or '0' allowed.")
            return
        }

        bluetoothGatt?.let { gatt ->
            val service = gatt.getService(serviceUUID)
            val char = service?.getCharacteristic(charUUID)
            char?.let {
                if (it.properties and BluetoothGattCharacteristic.PROPERTY_WRITE > 0) {
                    it.value = data.toByteArray()
                    val writeSuccess = gatt.writeCharacteristic(it)
                    Log.d("BluetoothController", "Attempting to write characteristic: $writeSuccess")
                } else {
                    Log.d("BluetoothController", "Characteristic not writable")
                }
            } ?: Log.d("BluetoothController", "Characteristic not found")
        }
    }

    fun disconnect() {
        bluetoothGatt?.apply {
            close()
            bluetoothGatt = null
        }
    }
}
