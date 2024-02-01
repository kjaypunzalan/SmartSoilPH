package com.iacademy.smartsoilph.models

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.iacademy.smartsoilph.datamodels.SoilDataModel

/**
 * SQLite Database helper class for managing local database operations.
 */
class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
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
        private const val KEY_DATE_OF_RECOMMENDATION = "dateOfRecommendation"
        private const val KEY_INITIAL_STORAGE_TYPE = "initialStorageType"
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
                + KEY_FERTILIZER_RECOMMENDATION + " REAL," + KEY_LIME_RECOMMENDATION + " REAL,"
                + KEY_DATE_OF_RECOMMENDATION + " TEXT,"
                + KEY_INITIAL_STORAGE_TYPE + " TEXT" + ")")
        db.execSQL(createSoilTable)
    }

    /**
     * Called when the database needs to be upgraded.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SOIL")
        onCreate(db)
    }

    fun deleteDatabase() {
        close() // Close the database if it's open
        context.deleteDatabase(DATABASE_NAME)
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
        contentValues.put(KEY_DATE_OF_RECOMMENDATION, soilData.dateOfRecommendation)
        contentValues.put(KEY_INITIAL_STORAGE_TYPE, soilData.initialStorageType)

        // Inserting Row and returning the row id
        val success = db.insert(TABLE_SOIL, null, contentValues)
        if (success == -1L) {
            Log.e("DatabaseHelper", "Failed to insert soil data")
        }

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
            SoilDataModel::limeRecommendation to KEY_LIME_RECOMMENDATION,
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

                    // Handle String fields separately
                    val dateOfRecommendationIndex = it.getColumnIndex(KEY_DATE_OF_RECOMMENDATION)
                    if (dateOfRecommendationIndex != -1) {
                        soilData.dateOfRecommendation = it.getString(dateOfRecommendationIndex)
                    }

                    val initialStorageTypeIndex = it.getColumnIndex(KEY_INITIAL_STORAGE_TYPE)
                    if (initialStorageTypeIndex != -1) {
                        soilData.initialStorageType = it.getString(initialStorageTypeIndex)
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

    /**
     * Retrieves the recently saved soil data.
     * @return a list of SoilDataModel containing the data from the database
     */
    fun getLatestSoilData(): SoilDataModel? {
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_SOIL ORDER BY $KEY_ID DESC LIMIT 1"
        val cursor: Cursor? = db.rawQuery(selectQuery, null)

        var soilData: SoilDataModel? = null
        cursor?.use {
            if (it.moveToFirst()) {
                val nitrogenIndex = it.getColumnIndex(KEY_NITROGEN)
                val phosphorusIndex = it.getColumnIndex(KEY_PHOSPHORUS)
                val potassiumIndex = it.getColumnIndex(KEY_POTASSIUM)
                val phLevelIndex = it.getColumnIndex(KEY_PH_LEVEL)
                val ecLevelIndex = it.getColumnIndex(KEY_EC_LEVEL)
                val humidityIndex = it.getColumnIndex(KEY_HUMIDITY)
                val temperatureIndex = it.getColumnIndex(KEY_TEMPERATURE)
                val fertilizerRecommendationIndex = it.getColumnIndex(KEY_FERTILIZER_RECOMMENDATION)
                val limeRecommendationIndex = it.getColumnIndex(KEY_LIME_RECOMMENDATION)
                val dateOfRecommendationIndex = it.getColumnIndex(KEY_DATE_OF_RECOMMENDATION)
                val initialStorageTypeIndex = it.getColumnIndex(KEY_INITIAL_STORAGE_TYPE)


                // Check if indices are valid
                if (nitrogenIndex != -1 && phosphorusIndex != -1 /* ... and so on for other indices */) {
                    soilData = SoilDataModel().apply {
                        nitrogen = it.getFloat(nitrogenIndex)
                        phosphorus = it.getFloat(phosphorusIndex)
                        potassium = it.getFloat(potassiumIndex)
                        phLevel = it.getFloat(phLevelIndex)
                        ecLevel = it.getFloat(ecLevelIndex)
                        humidity = it.getFloat(humidityIndex)
                        temperature = it.getFloat(temperatureIndex)
                        fertilizerRecommendation = it.getFloat(fertilizerRecommendationIndex)
                        limeRecommendation = it.getFloat(limeRecommendationIndex)
                        dateOfRecommendation = it.getString(dateOfRecommendationIndex)
                        initialStorageType = it.getString(initialStorageTypeIndex)
                    }
                }
            }
        }
        db.close()
        return soilData
    }

    /**
     * Syncs local SQLite data with FirebaseDatabase.
     * @return a list of SoilDataModel containing the data from the database
     */
    fun syncDataWithFirebase(auth: FirebaseAuth, context: Context) {
        val dbHelper = DatabaseHelper(context)
        val localDataList = dbHelper.getAllSoilData()

        // Get FirebaseDatabase Reference
        val firebaseDB = Firebase.database.reference
        val referenceUser = firebaseDB.child("SmartSoilPH").child("Users").child(auth.currentUser!!.uid)
        val referenceDetails = referenceUser.child("RecommendationHistory")

        localDataList.forEach { soilData ->
            referenceDetails.push().setValue(soilData)
        }
    }

}
