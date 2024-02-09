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
import com.iacademy.smartsoilph.datamodels.UserData

/**
 * SQLite Database helper class for managing local database operations.
 */
class SQLiteModel(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {

        // DATABASE
        private const val DATABASE_VERSION = 1                      // Database version, used for database upgrade management
        private const val DATABASE_NAME = "SmartSoilPHDatabase"     // Database name

        // RECOMMENDATION TABLE
        private const val TABLE_RECOMMENDATION = "RecommendationTable"                  // Table name
        private const val KEY_RECOMMENDATION_ID = "recommendationID"          // Constants for the user table columns
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
        private const val KEY_IS_SAVED_ONLINE = "isSavedOnline"

        // USER TABLE
        private const val TABLE_USER = "UserTable"                  // Table name
        private const val KEY_USER_ID = "userID"                    // Constants for the user table columns
        private const val KEY_NAME = "name"
        private const val KEY_EMAIL = "email"
        private const val KEY_NUMBER = "number"
    }

    /**
     * Called when the database is created for the first time.
     */
    override fun onCreate(db: SQLiteDatabase) {
        // Table creation for UserTable to use userID as the primary key
        val createUserTable = """
            CREATE TABLE $TABLE_USER (
                userID TEXT PRIMARY KEY,
                name TEXT,
                email TEXT,
                number TEXT
            )
        """.trimIndent()
        db.execSQL(createUserTable)

        // Table creation for SoilTable to use recommendationID as the primary key
        val createSoilTable = """
            CREATE TABLE $TABLE_RECOMMENDATION (
                recommendationID TEXT PRIMARY KEY,
                nitrogen REAL,
                potassium REAL,
                phosphorus REAL,
                phLevel REAL,
                ecLevel REAL,
                humidity REAL,
                temperature REAL,
                fertilizerRecommendation REAL,
                limeRecommendation REAL,
                dateOfRecommendation TEXT,
                initialStorageType TEXT,
                isSavedOnline INTEGER,
                FOREIGN KEY(userID) REFERENCES $TABLE_USER(userID)
            )
        """.trimIndent()
        db.execSQL(createSoilTable)
    }


    /**
     * Called when the database needs to be upgraded.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RECOMMENDATION")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }

    fun deleteDatabase() {
        close() // Close the database if it's open
        context.deleteDatabase(DATABASE_NAME)
    }

    /**
     * Adds a new UserData entry to the database.
     */
    fun addUserData(userData: UserData) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_USER_ID, userData.userID)
            put(KEY_NAME, userData.name)
            put(KEY_EMAIL, userData.email)
            put(KEY_NUMBER, userData.number.toString())
        }
        val userIdResult  = db.insert(TABLE_USER, null, values)

        if (userIdResult == -1L) {
            Log.e("DatabaseError", "Failed to insert user data")
        } else {
            Log.d("DatabaseSuccess", "User data inserted with row ID: $userIdResult")
        }

        db.close()
    }

    /**
     * Fetch user data
     */
    fun getUserData(userID: String): UserData? {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_USER, null, "userID=?", arrayOf(userID), null, null, null)

        var userData: UserData? = null
        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndex(KEY_NAME))
            val email = cursor.getString(cursor.getColumnIndex(KEY_EMAIL))
            val number = cursor.getDouble(cursor.getColumnIndex(KEY_NUMBER))
            userData = UserData(userID, name, email, number)
        }
        cursor.close()
        db.close()
        return userData
    }


    /**
     * Adds a new SoilDataModel entry to the database.
     * @param soilData the SoilDataModel to be added
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    fun addSoilData(recommendationData: RecommendationData) {
        // Get database
        val db = this.writableDatabase

        // Extracting soil data from the recommendationData
        val soilData = recommendationData.soilData

        // Putting values in ContentValues
        val contentValues = ContentValues().apply {
            put(KEY_RECOMMENDATION_ID, recommendationData.recommendationID)
            //put(KEY_USER_ID, recommendationData.userID) // Assuming you add userID in RecommendationData model
            put(KEY_NITROGEN, soilData.nitrogen)
            put(KEY_POTASSIUM, soilData.potassium)
            put(KEY_PHOSPHORUS, soilData.phosphorus)
            put(KEY_PH_LEVEL, soilData.phLevel)
            put(KEY_EC_LEVEL, soilData.ecLevel)
            put(KEY_HUMIDITY, soilData.humidity)
            put(KEY_TEMPERATURE, soilData.temperature)
            put(KEY_FERTILIZER_RECOMMENDATION, recommendationData.fertilizerRecommendation)
            put(KEY_LIME_RECOMMENDATION, recommendationData.limeRecommendation)
            put(KEY_DATE_OF_RECOMMENDATION, recommendationData.dateOfRecommendation)
            put(KEY_INITIAL_STORAGE_TYPE, recommendationData.initialStorageType)
            put(KEY_IS_SAVED_ONLINE, if (recommendationData.isSavedOnline) 1 else 0)
        }

        // Inserting Row and returning the row id
        val recommendationIdResult  = db.insert(TABLE_RECOMMENDATION, null, contentValues)
        if (recommendationIdResult  == -1L) {
            Log.e("DatabaseError", "Failed to insert recommendation data")
        } else {
            Log.d("DatabaseSuccess", "Recommendation data inserted with row ID: $recommendationIdResult")
        }

        db.close()
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
        val selectQuery = "SELECT * FROM $TABLE_RECOMMENDATION"
        // Execute the query and get a cursor to iterate over the results
        val cursor: Cursor? = db.rawQuery(selectQuery, null)
        // Use the cursor in a safe block to ensure it's closed after use
        cursor?.use {
            while (it.moveToNext()) {
                // Get the index of the columns.
                // This is separated from data extraction to handle cases where the column might not exist.
                val recommendationIDIndex = it.getColumnIndex(KEY_RECOMMENDATION_ID)
                val userIDIndex = it.getColumnIndex(KEY_USER_ID)
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
                val isSavedOnlineIndex = it.getColumnIndex(KEY_IS_SAVED_ONLINE)

                // Extract data
                val recommendationID = it.getString(recommendationIDIndex)
                val userID = it.getString(userIDIndex)
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
                val isSavedOnline = it.getInt(isSavedOnlineIndex) == 1

                // Create a RecommendationData object with the extracted values and add it to the list
                val soilData = SoilData (nitrogen, phosphorus, potassium, phLevel, ecLevel, humidity, temperature)
                val recommendationData = RecommendationData(recommendationID, soilData, fertilizerRecommendation, limeRecommendation, dateOfRecommendation, initialStorageType, isSavedOnline)
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
        // Make sure to order by an appropriate column to get the latest entry
        val selectQuery = "SELECT * FROM $TABLE_RECOMMENDATION ORDER BY id DESC LIMIT 1" // Assuming 'id' is your primary key
        // Execute the query and get a cursor to iterate over the results
        val cursor: Cursor? = db.rawQuery(selectQuery, null)

        // Use the cursor in a safe block to ensure it's closed after use
        cursor?.use {
            if (it.moveToFirst()) {
                // Get the index of the columns.
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

                // Create a RecommendationData object with the extracted values
                val soilData = SoilData(nitrogen, phosphorus, potassium, phLevel, ecLevel, humidity, temperature)
                recommendationData = RecommendationData(soilData, fertilizerRecommendation, limeRecommendation, dateOfRecommendation, initialStorageType)
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
        val dbHelper = SQLiteModel(context)
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
