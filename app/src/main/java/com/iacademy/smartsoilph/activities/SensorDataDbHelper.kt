package com.iacademy.smartsoilph.activities

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SensorDataDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_MOISTURE INTEGER," +
                "$COLUMN_TEMPERATURE REAL," +
                ")"
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertSensorData(moisture: Int, temperature: Float, /* ... other parameters ... */) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_MOISTURE, moisture)
            put(COLUMN_TEMPERATURE, temperature)
            // ... other values ...
        }
        db.insert(TABLE_NAME, null, contentValues)
        db.close()
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "SensorData.db"
        private const val TABLE_NAME = "sensor_data"
        private const val COLUMN_ID = "id"
        private const val COLUMN_MOISTURE = "moisture"
        private const val COLUMN_TEMPERATURE = "temperature"
    }
}
