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
            userID TEXT,
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
        var userData: UserData? = null

        val cursor = db.query(
            TABLE_USER,
            arrayOf(KEY_USER_ID, KEY_NAME, KEY_EMAIL, KEY_NUMBER), // Specify columns to avoid "SELECT *"
            "$KEY_USER_ID=?",
            arrayOf(userID),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(KEY_NAME)
            val emailIndex = cursor.getColumnIndex(KEY_EMAIL)
            val numberIndex = cursor.getColumnIndex(KEY_NUMBER)

            // Check if indexes are valid
            if (nameIndex != -1 && emailIndex != -1 && numberIndex != -1) {
                val name = cursor.getString(nameIndex)
                val email = cursor.getString(emailIndex)
                val number = cursor.getString(numberIndex).toDouble() // Assuming number is stored as TEXT

                userData = UserData(userID, name, email, number)
            }
        }
        cursor.close()
        db.close()
        return userData
    }

    fun getCurrentUserUID(): String? {
        val db = this.readableDatabase
        val selectQuery = "SELECT userID FROM UserTable ORDER BY ROWID DESC LIMIT 1" // Ensure 'userID' matches your column name exactly
        val cursor = db.rawQuery(selectQuery, null)
        var userID: String? = null
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex("userID")
            if (columnIndex != -1) { // Check to ensure the column index is valid
                userID = cursor.getString(columnIndex)
            } else {
                // Handle the error or log it
                Log.e("SQLiteModel", "Column 'userID' not found.")
            }
        }
        cursor.close()
        db.close()
        return userID
    }

    fun getCurrentUserName(): String? {
        val db = this.readableDatabase
        val selectQuery = "SELECT name FROM UserTable ORDER BY ROWID DESC LIMIT 1" // Assuming you want the most recently added user
        val cursor = db.rawQuery(selectQuery, null)
        var name: String? = null
        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex("name")
            if (nameIndex != -1) { // Ensure index is valid
                name = cursor.getString(nameIndex)
            }
        }
        cursor.close()
        db.close()
        return name
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
            put(KEY_USER_ID, recommendationData.userID)
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
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToNext()) {
            // Extract data for RecommendationData
            val recommendationID = cursor.getString(cursor.getColumnIndexOrThrow(KEY_RECOMMENDATION_ID))
            val userID = cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_ID))
            val nitrogen = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_NITROGEN))
            val phosphorus = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_PHOSPHORUS))
            val potassium = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_POTASSIUM))
            val phLevel = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_PH_LEVEL))
            val ecLevel = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_EC_LEVEL))
            val humidity = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_HUMIDITY))
            val temperature = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_TEMPERATURE))
            val fertilizerRecommendation = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_FERTILIZER_RECOMMENDATION))
            val limeRecommendation = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_LIME_RECOMMENDATION))
            val dateOfRecommendation = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE_OF_RECOMMENDATION))
            val initialStorageType = cursor.getString(cursor.getColumnIndexOrThrow(KEY_INITIAL_STORAGE_TYPE))
            val isSavedOnline = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_SAVED_ONLINE)) == 1

            Log.d("", "recommendationID: $recommendationID")
            Log.d("", "currentUserUID: $userID")
            Log.d("", "soilData: $nitrogen")
            Log.d("", "fertilizer: $fertilizerRecommendation")
            Log.d("", "lime: $limeRecommendation")
            Log.d("", "dateOfRecommendation: $dateOfRecommendation")
            Log.d("", "storageType: $initialStorageType")
            Log.d("", "savedOnline: $isSavedOnline")

            // Constructing SoilData object
            val soilData = SoilData(
                nitrogen, phosphorus, potassium, phLevel, ecLevel, humidity, temperature
            )

            // Constructing RecommendationData object
            val recommendationData = RecommendationData(
                recommendationID, userID, soilData, fertilizerRecommendation, limeRecommendation,
                dateOfRecommendation, initialStorageType, true
            )
            recommendationList.add(recommendationData)
        }
        cursor.close()
        db.close()
        return recommendationList
    }

    /**
     * Retrieves the recently saved soil data.
     * @return a list of SoilDataModel containing the data from the database
     */
    fun getLatestSoilData(): RecommendationData? {
        val db = this.readableDatabase
        var recommendationData: RecommendationData? = null
        val selectQuery = "SELECT * FROM $TABLE_RECOMMENDATION ORDER BY $KEY_RECOMMENDATION_ID DESC LIMIT 1"
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            // Extract data for RecommendationData
            val recommendationID = cursor.getString(cursor.getColumnIndexOrThrow(KEY_RECOMMENDATION_ID))
            val userID = cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_ID))
            val nitrogen = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_NITROGEN))
            val phosphorus = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_PHOSPHORUS))
            val potassium = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_POTASSIUM))
            val phLevel = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_PH_LEVEL))
            val ecLevel = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_EC_LEVEL))
            val humidity = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_HUMIDITY))
            val temperature = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_TEMPERATURE))
            val fertilizerRecommendation = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_FERTILIZER_RECOMMENDATION))
            val limeRecommendation = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_LIME_RECOMMENDATION))
            val dateOfRecommendation = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE_OF_RECOMMENDATION))
            val initialStorageType = cursor.getString(cursor.getColumnIndexOrThrow(KEY_INITIAL_STORAGE_TYPE))
            val isSavedOnline = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_SAVED_ONLINE)) == 1

            Log.d("", "recommendationID: $recommendationID")
            Log.d("", "currentUserUID: $userID")
            Log.d("", "soilData: $nitrogen")
            Log.d("", "fertilizer: $fertilizerRecommendation")
            Log.d("", "lime: $limeRecommendation")
            Log.d("", "dateOfRecommendation: $dateOfRecommendation")
            Log.d("", "storageType: $initialStorageType")
            Log.d("", "savedOnline: $isSavedOnline")

            // Constructing SoilData object
            val soilData = SoilData(
                nitrogen, phosphorus, potassium, phLevel, ecLevel, humidity, temperature
            )

            // Constructing RecommendationData object
            recommendationData = RecommendationData(
                recommendationID, userID, soilData, fertilizerRecommendation, limeRecommendation,
                dateOfRecommendation, initialStorageType, true
            )
        }
        cursor.close()
        db.close()
        return recommendationData
    }




    /**
     * Syncs local SQLite data with FirebaseDatabase.
     * @return a list of SoilDataModel containing the data from the database
     */
    fun syncDataWithFirebase(auth: FirebaseAuth) {
        val db = this.readableDatabase
        val firebaseDB = Firebase.database.reference
        val localDataList = getAllSoilData()

        for (recommendationData in localDataList) {
            if (!recommendationData.isSavedOnline) {
                val referenceUser = firebaseDB.child("SmartSoilPH").child("Users").child(recommendationData.userID)
                val referenceDetails = referenceUser.child("RecommendationHistory").child(recommendationData.recommendationID)
                referenceDetails.setValue(recommendationData).addOnSuccessListener {
                    // Update the local database to mark this record as synced
                    val contentValues = ContentValues()
                    contentValues.put(KEY_IS_SAVED_ONLINE, 1) // Mark as saved online
                    db.update(TABLE_RECOMMENDATION, contentValues, "recommendationID=?", arrayOf(recommendationData.recommendationID))
                }
            }
        }
        db.close()
    }

    fun generateRecommendationID(): String {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT recommendationID FROM $TABLE_RECOMMENDATION ORDER BY recommendationID DESC LIMIT 1", null)

        var lastId = 0
        if (cursor.moveToFirst()) {
            val recommendationID = cursor.getString(0)
            val idPart = recommendationID.replace("RID-", "")
            lastId = idPart.toIntOrNull() ?: 0
        }
        cursor.close()
        db.close()

        val newId = lastId + 1
        return "RID-" + String.format("%06d", newId)
    }

}
