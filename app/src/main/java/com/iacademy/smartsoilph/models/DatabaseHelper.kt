package com.iacademy.smartsoilph.models

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.iacademy.smartsoilph.datamodels.SoilDataModel

/**
 * SQLite Database helper class for managing local database operations.
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {


        private const val DATABASE_VERSION = 1                      // Database version, used for database upgrade management
        private const val DATABASE_NAME = "SmartSoilPHDatabase"     // Database name
        private const val TABLE_SOIL = "SoilTable"                  // Table name
        private const val KEY_ID = "id"

        // Constants for the soil table columns
        private const val KEY_NITROGEN = "nitrogen"
        private const val KEY_POTASSIUM = "potassium"
        private const val KEY_PHOSPHORUS = "phosphorus"
        private const val KEY_PH_LEVEL = "phLevel"
        private const val KEY_EC_LEVEL = "ecLevel"
        private const val KEY_HUMIDITY = "humidity"
        private const val KEY_TEMPERATURE = "temperature"
        private const val KEY_FERTILIZER_RECOMMENDATION = "fertilizerRecommendation"
        private const val KEY_LIME_RECOMMENDATION = "limeRecommendation"
    }

    /**
     * Called when the database is created for the first time.
     */
    override fun onCreate(db: SQLiteDatabase) {
        val createSoilTable = ("CREATE TABLE " + TABLE_SOIL + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NITROGEN + " REAL," + KEY_POTASSIUM + " REAL," + KEY_PHOSPHORUS + " REAL,"
                + KEY_PH_LEVEL + " REAL," + KEY_EC_LEVEL + " REAL,"
                + KEY_HUMIDITY + " REAL," + KEY_TEMPERATURE + " REAL,"
                + KEY_FERTILIZER_RECOMMENDATION + " REAL,"
                + KEY_LIME_RECOMMENDATION + " REAL" + ")")
        db.execSQL(createSoilTable)
    }

    /**
     * Called when the database needs to be upgraded.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SOIL")
        onCreate(db)
    }


    /**
     * Adds a new SoilDataModel entry to the database.
     * @param soilData the SoilDataModel to be added
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    fun addSoilData(soilData: SoilDataModel): Long {
        // Get database
        val db = this.writableDatabase

        // Putting values in ContentValues
        val contentValues = ContentValues()
        contentValues.put(KEY_NITROGEN, soilData.nitrogen)
        contentValues.put(KEY_POTASSIUM, soilData.potassium)
        contentValues.put(KEY_PHOSPHORUS, soilData.phosphorus)
        contentValues.put(KEY_PH_LEVEL, soilData.phLevel)
        contentValues.put(KEY_EC_LEVEL, soilData.ecLevel)
        contentValues.put(KEY_HUMIDITY, soilData.humidity)
        contentValues.put(KEY_TEMPERATURE, soilData.temperature)
        contentValues.put(KEY_FERTILIZER_RECOMMENDATION, soilData.fertilizerRecommendation)
        contentValues.put(KEY_LIME_RECOMMENDATION, soilData.limeRecommendation)

        // Inserting Row and returning the row id
        val success = db.insert(TABLE_SOIL, null, contentValues)
        db.close()
        return success
    }

    /**
     * Retrieves all soil data records from the database.
     * @return a list of SoilDataModel containing the data from the database
     */
    fun getAllSoilData(): List<SoilDataModel> {
        val soilList: ArrayList<SoilDataModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_SOIL"
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery(selectQuery, null)

        // Map each field to its corresponding column key
        val fieldMap = mapOf(
            SoilDataModel::nitrogen to KEY_NITROGEN,
            SoilDataModel::phosphorus to KEY_PHOSPHORUS,
            SoilDataModel::potassium to KEY_POTASSIUM,
            SoilDataModel::ecLevel to KEY_EC_LEVEL,
            SoilDataModel::phLevel to KEY_PH_LEVEL,
            SoilDataModel::humidity to KEY_HUMIDITY,
            SoilDataModel::temperature to KEY_TEMPERATURE,
            SoilDataModel::fertilizerRecommendation to KEY_FERTILIZER_RECOMMENDATION,
            SoilDataModel::limeRecommendation to KEY_LIME_RECOMMENDATION
        )

        cursor?.let {
            if (cursor.moveToFirst()) {
                do {
                    val soilData = SoilDataModel()

                    // Iterate through the fieldMap and use reflection to set field values
                    fieldMap.forEach { (field, key) ->
                        cursor.getColumnIndex(key).takeIf { it != -1 }?.let { index ->
                            // Use reflection to set the field value
                            field.setter.call(soilData, cursor.getFloat(index))
                        }
                    }

                    // Add soil data to list
                    soilList.add(soilData)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        db.close()
        return soilList
    }
}
