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
import com.iacademy.smartsoilph.datamodels.SoilData
import com.iacademy.smartsoilph.datamodels.RecommendationData

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
    fun addSoilData(recommendationData: RecommendationData): Long {
        // Get database
        val db = this.writableDatabase

        // Extracting soil data from the recommendationData
        val soilData = recommendationData.soilData

        // Putting values in ContentValues
        val contentValues = ContentValues()
        contentValues.put(KEY_NITROGEN, soilData.nitrogen)
        contentValues.put(KEY_POTASSIUM, soilData.potassium)
        contentValues.put(KEY_PHOSPHORUS, soilData.phosphorus)
        contentValues.put(KEY_PH_LEVEL, soilData.phLevel)
        contentValues.put(KEY_EC_LEVEL, soilData.ecLevel)
        contentValues.put(KEY_HUMIDITY, soilData.humidity)
        contentValues.put(KEY_TEMPERATURE, soilData.temperature)
        contentValues.put(KEY_FERTILIZER_RECOMMENDATION, recommendationData.fertilizerRecommendation)
        contentValues.put(KEY_LIME_RECOMMENDATION, recommendationData.limeRecommendation)
        contentValues.put(KEY_DATE_OF_RECOMMENDATION, recommendationData.dateOfRecommendation)
        contentValues.put(KEY_INITIAL_STORAGE_TYPE, recommendationData.initialStorageType)

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
    fun getAllSoilData(): List<RecommendationData> {

        // Create ArrayList to list all data
        val recommendationList: ArrayList<RecommendationData> = ArrayList()

        // Get database
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_SOIL"
        // Execute the query and get a cursor to iterate over the results
        val cursor: Cursor? = db.rawQuery(selectQuery, null)
        // Use the cursor in a safe block to ensure it's closed after use
        cursor?.use {
            while (it.moveToNext()) {
                // Get the index of the columns.
                // This is separated from data extraction to handle cases where the column might not exist.
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

                // Extract data
                val nitrogen = it.getFloat(nitrogenIndex)
                val phosphorus = it.getFloat(phosphorusIndex)
                val potassium = it.getFloat(potassiumIndex)
                val phLevel = it.getFloat(phLevelIndex)
                val ecLevel = it.getFloat(ecLevelIndex)
                val humidity = it.getFloat(humidityIndex)
                val temperature = it.getFloat(temperatureIndex)
                val fertilizerRecommendation = it.getFloat(fertilizerRecommendationIndex)
                val limeRecommendation = it.getFloat(limeRecommendationIndex)
                val dateOfRecommendation = it.getString(dateOfRecommendationIndex)
                val initialStorageType = it.getString(initialStorageTypeIndex)

                // Create a RecommendationData object with the extracted values and add it to the list
                val soilData = SoilData (nitrogen, phosphorus, potassium, phLevel, ecLevel, humidity, temperature)
                val recommendationData = RecommendationData(soilData, fertilizerRecommendation, limeRecommendation, dateOfRecommendation, initialStorageType)
                recommendationList.add(recommendationData)
            }
        }

        db.close()
        return recommendationList
    }

    /**
     * Retrieves the recently saved soil data.
     * @return a list of SoilDataModel containing the data from the database
     */
    fun getLatestSoilData(): RecommendationData? {

        // Create object RecommendationData that holds the latest data
        var recommendationData: RecommendationData? = null

        // Get database
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_SOIL"
        // Execute the query and get a cursor to iterate over the results
        val cursor: Cursor? = db.rawQuery(selectQuery, null)
        // Use the cursor in a safe block to ensure it's closed after use
        cursor?.use {
            while (it.moveToFirst()) {
                // Get the index of the columns.
                // This is separated from data extraction to handle cases where the column might not exist.
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

                // Extract data
                val nitrogen = it.getFloat(nitrogenIndex)
                val phosphorus = it.getFloat(phosphorusIndex)
                val potassium = it.getFloat(potassiumIndex)
                val phLevel = it.getFloat(phLevelIndex)
                val ecLevel = it.getFloat(ecLevelIndex)
                val humidity = it.getFloat(humidityIndex)
                val temperature = it.getFloat(temperatureIndex)
                val fertilizerRecommendation = it.getFloat(fertilizerRecommendationIndex)
                val limeRecommendation = it.getFloat(limeRecommendationIndex)
                val dateOfRecommendation = it.getString(dateOfRecommendationIndex)
                val initialStorageType = it.getString(initialStorageTypeIndex)

                // Create a RecommendationData object with the extracted values and add it to the list
                val soilData = SoilData (nitrogen, phosphorus, potassium, phLevel, ecLevel, humidity, temperature)
                val recommendationData = RecommendationData(soilData, fertilizerRecommendation, limeRecommendation, dateOfRecommendation, initialStorageType)
            }
        }

        db.close()
        return recommendationData
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

        localDataList.forEach { recommendationData ->
            referenceDetails.push().setValue(recommendationData)
        }
    }

}
