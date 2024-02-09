
package com.iacademy.smartsoilph.arduino

import android.bluetooth.*
import android.content.Context
import android.util.Log
import java.util.*

class BluetoothController(private val context: Context) {

    interface BluetoothDataListener {
        fun onDataReceived(data: String)
    }

    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothGatt: BluetoothGatt? = null
    private var dataListener: BluetoothDataListener? = null

    // Replace these with your actual Bluetooth module MAC address and UUIDs
    private val deviceMacAddress = "88:4A:EA:98:22:E7"
    private val serviceUUID = UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB")
    private val characteristicUUID = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB")

    fun setDataListener(listener: BluetoothDataListener) {
        this.dataListener = listener
    }

    fun connect(): Boolean {
        bluetoothAdapter?.let { adapter ->
            val device = adapter.getRemoteDevice(deviceMacAddress)
            bluetoothGatt = device.connectGatt(context, false, gattCallback)
            return true
        }
        return false
    }

    fun sendCommand(command: String) {
        bluetoothGatt?.let { gatt ->
            val service = gatt.getService(serviceUUID)
            val characteristic = service?.getCharacteristic(characteristicUUID)
            characteristic?.setValue(command)
            gatt.writeCharacteristic(characteristic)
        }
    }

    fun disconnect() {
        bluetoothGatt?.let {
            it.disconnect()
            it.close()
            bluetoothGatt = null
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("BluetoothController", "Connected to GATT server.")
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("BluetoothController", "Disconnected from GATT server.")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val service = gatt.getService(serviceUUID)
                val characteristic = service?.getCharacteristic(characteristicUUID)
                if (characteristic != null) {
                    enableNotifications(gatt, characteristic)
                }
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            super.onCharacteristicChanged(gatt, characteristic)
            val data = characteristic.value?.toString(Charsets.UTF_8)
            data?.let {
                dataListener?.onDataReceived(it)
            }
        }
    }

    private fun enableNotifications(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        val success = gatt.setCharacteristicNotification(characteristic, true)
        if (success) {
            val descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805F9B34FB"))
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(descriptor)
        }
    }
}
