
    package com.iacademy.smartsoilph.arduino

    import android.bluetooth.*
    import android.content.Context
    import android.content.Intent
    import android.util.Log
    import androidx.localbroadcastmanager.content.LocalBroadcastManager
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

            private var buffer = StringBuilder()

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                super.onCharacteristicChanged(gatt, characteristic)
                val data = characteristic.value?.toString(Charsets.UTF_8)
                data?.let {
                    buffer.append(it)
                    // Check if the buffer contains the end-of-message delimiter (e.g., newline)
                    if (buffer.contains("\n")) {
                        // Extract the complete message up to the delimiter
                        val completeData = buffer.substring(0, buffer.indexOf("\n") + 1)
                        // Remove the processed data from the buffer
                        buffer.delete(0, buffer.indexOf("\n") + 1)

                        // Process the complete message
                        processCompleteData(completeData.trim())
                    }
                }
            }

            private fun processCompleteData(data: String) {
                // Split the data by commas
                val values = data.split(",").map { it.trim() }

                // Assign each value to a variable, ensuring there are enough values to avoid IndexOutOfBoundsException
                val val1 = values.getOrNull(0) ?: "N/A"
                val val2 = values.getOrNull(1) ?: "N/A"
                val val3 = values.getOrNull(2) ?: "N/A"
                val val4 = values.getOrNull(3) ?: "N/A"
                val val5 = values.getOrNull(4) ?: "N/A"
                val val6 = values.getOrNull(5) ?: "N/A"
                val val7 = values.getOrNull(6) ?: "N/A"


                // Log or use the values as needed
                Log.d("BluetoothController", "Values - val1: $val1, val2: $val2, val3: $val3, val4: $val4, val5: $val5, val6: $val6, val7: $val7")

                val dataMap = mapOf(
                    "val1" to val1,
                    "val2" to val2,
                    "val3" to val3,
                    "val4" to val4,
                    "val5" to val5,
                    "val6" to val6,
                    "val7" to val7,
                )
                // Broadcast the data
                broadcastUpdate("com.iacademy.smartsoilph.arduino.ACTION_UPDATE_DATA", dataMap)
                dataListener?.onDataReceived(data)
            }

            private fun broadcastUpdate(action: String, dataMap: Map<String, String>) {
                Intent(action).also { intent ->
                    dataMap.forEach { (key, value) ->
                        intent.putExtra(key, value)
                    }
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
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
