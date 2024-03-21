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
import com.iacademy.smartsoilph.datamodels.RequiredFertilizerData
import com.iacademy.smartsoilph.datamodels.UserData

/**
 * SQLite Database helper class for managing local database operations.
 */
class SQLiteModel(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {

        // DATABASE
        private const val DATABASE_VERSION = 1                      // Database version, used for database upgrade management
        private const val DATABASE_NAME = "SmartSoilPHDatabase"     // Database name

        // SOIL TABLE
        private const val KEY_CROP_TYPE = "cropType"
        private const val KEY_NITROGEN = "nitrogen"
        private const val KEY_POTASSIUM = "potassium"
        private const val KEY_PHOSPHORUS = "phosphorus"
        private const val KEY_PH_LEVEL = "phLevel"
        private const val KEY_EC_LEVEL = "ecLevel"
        private const val KEY_HUMIDITY = "humidity"
        private const val KEY_TEMPERATURE = "temperature"
        private const val KEY_SOIL_TEXTURE = "soilTexture"

        // RECOMMENDATION TABLE
        private const val TABLE_RECOMMENDATION = "RecommendationTable"                  // Table name
        private const val KEY_RECOMMENDATION_ID = "recommendationID"          // Constants for the user table columns
        private const val KEY_DATE_OF_RECOMMENDATION = "dateOfRecommendation"
        private const val KEY_INITIAL_STORAGE_TYPE = "initialStorageType"
        private const val KEY_IS_SAVED_ONLINE = "isSavedOnline"

        // REQUIRED FERTILIZER TABLE
        private const val KEY_REQUIRED_N = "requiredN"
        private const val KEY_REQUIRED_P = "requiredP"
        private const val KEY_REQUIRED_K = "requiredK"
        private const val KEY_FERTILIZER_1 = "fertilizer1"
        private const val KEY_FERTILIZER_2 = "fertilizer2"
        private const val KEY_FERTILIZER_3 = "fertilizer3"
        private const val KEY_KG_FERTILIZER_1 = "kgFertilizer1"
        private const val KEY_KG_FERTILIZER_2 = "kgFertilizer2"
        private const val KEY_KG_FERTILIZER_3 = "kgFertilizer3"
        private const val KEY_BAG_FERTILIZER_1 = "bagFertilizer1"
        private const val KEY_BAG_FERTILIZER_2 = "bagFertilizer2"
        private const val KEY_BAG_FERTILIZER_3 = "bagFertilizer3"

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
            cropType TEXT,
            nitrogen REAL,         
            potassium REAL,
            phosphorus REAL,
            phLevel REAL,
            ecLevel REAL,
            humidity REAL,
            temperature REAL,
            soilTexture TEXT,
            requiredN REAL,
            requiredP REAL,
            requiredK REAL,
            fertilizer1 TEXT,
            fertilizer2 TEXT,
            fertilizer3 TEXT,
            kgFertilizer1 REAL,
            kgFertilizer2 REAL,
            kgFertilizer3 REAL,
            bagFertilizer1 REAL,
            bagFertilizer2 REAL,
            bagFertilizer3 REAL,
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

    fun getCurrentUserUID(): String? {
        val db = this.readableDatabase
        val selectQuery = "SELECT userID FROM UserTable ORDER BY ROWID DESC LIMIT 1" // Ensure 'userID' matches your column name exactly
        val cursor = db.rawQuery(selectQuery, null)
        var userID: String? = null
        Log.d("", "10.1.5 ERROR NEXT")
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
        val requiredFertilizerData = recommendationData.requiredFertilizerData

        // Putting values in ContentValues
        val contentValues = ContentValues().apply {
            put(KEY_RECOMMENDATION_ID, recommendationData.recommendationID)
            put(KEY_USER_ID, recommendationData.userID)
            put(KEY_CROP_TYPE, soilData.cropType)
            put(KEY_NITROGEN, soilData.nitrogen)
            put(KEY_POTASSIUM, soilData.potassium)
            put(KEY_PHOSPHORUS, soilData.phosphorus)
            put(KEY_PH_LEVEL, soilData.phLevel)
            put(KEY_EC_LEVEL, soilData.ecLevel)
            put(KEY_HUMIDITY, soilData.humidity)
            put(KEY_TEMPERATURE, soilData.temperature)
            put(KEY_SOIL_TEXTURE, soilData.soilTexture)
            put(KEY_REQUIRED_N, requiredFertilizerData.requiredN)
            put(KEY_REQUIRED_P, requiredFertilizerData.requiredP)
            put(KEY_REQUIRED_K, requiredFertilizerData.requiredK)
            put(KEY_FERTILIZER_1, requiredFertilizerData.fertilizer1)
            put(KEY_FERTILIZER_2, requiredFertilizerData.fertilizer2)
            put(KEY_FERTILIZER_3, requiredFertilizerData.fertilizer3)
            put(KEY_KG_FERTILIZER_1, requiredFertilizerData.kgFertilizer1)
            put(KEY_KG_FERTILIZER_2, requiredFertilizerData.kgFertilizer2)
            put(KEY_KG_FERTILIZER_3, requiredFertilizerData.kgFertilizer3)
            put(KEY_BAG_FERTILIZER_1, requiredFertilizerData.bagFertilizer1)
            put(KEY_BAG_FERTILIZER_2, requiredFertilizerData.bagFertilizer2)
            put(KEY_BAG_FERTILIZER_3, requiredFertilizerData.bagFertilizer3)
            put(KEY_DATE_OF_RECOMMENDATION, recommendationData.dateOfRecommendation)
            put(KEY_INITIAL_STORAGE_TYPE, recommendationData.initialStorageType)
            put(KEY_IS_SAVED_ONLINE, if (recommendationData.isSavedOnline) 1 else 0)
        }

        val isSaved = recommendationData.isSavedOnline
        Log.d("", "SAVED THE FOLLOWING INFO $isSaved")

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

        while (cursor.moveToNext()) {
            // Extract data for RecommendationData
            val recommendationID = cursor.getString(cursor.getColumnIndexOrThrow(KEY_RECOMMENDATION_ID))
            val userID = cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_ID))
            // Soil Data
            val cropType = cursor.getString(cursor.getColumnIndexOrThrow(KEY_CROP_TYPE))
            val nitrogen = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_NITROGEN))
            val phosphorus = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_PHOSPHORUS))
            val potassium = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_POTASSIUM))
            val phLevel = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_PH_LEVEL))
            val ecLevel = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_EC_LEVEL))
            val humidity = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_HUMIDITY))
            val temperature = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_TEMPERATURE))
            val soilTexture = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SOIL_TEXTURE))
            // Required Fertilizer Data
            val requiredN = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_REQUIRED_N))
            val requiredP = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_REQUIRED_P))
            val requiredK = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_REQUIRED_K))
            val fertilizer1 = cursor.getString(cursor.getColumnIndexOrThrow(KEY_FERTILIZER_1))
            val fertilizer2 = cursor.getString(cursor.getColumnIndexOrThrow(KEY_FERTILIZER_2))
            val fertilizer3 = cursor.getString(cursor.getColumnIndexOrThrow(KEY_FERTILIZER_3))
            val kgFertilizer1 = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_KG_FERTILIZER_1))
            val kgFertilizer2 = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_KG_FERTILIZER_2))
            val kgFertilizer3 = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_KG_FERTILIZER_3))
            val bagFertilizer1 = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_BAG_FERTILIZER_1))
            val bagFertilizer2 = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_BAG_FERTILIZER_2))
            val bagFertilizer3 = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_BAG_FERTILIZER_3))
            // Recommendation Info Data
            val dateOfRecommendation = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE_OF_RECOMMENDATION))
            val initialStorageType = cursor.getString(cursor.getColumnIndexOrThrow(KEY_INITIAL_STORAGE_TYPE))
            val isSavedOnline = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_SAVED_ONLINE)) == 1

            Log.d("", "recommendationID: $recommendationID")
            Log.d("", "currentUserUID: $userID")
            Log.d("", "soilData: $nitrogen")
            Log.d("", "fertilizer1: $fertilizer1")
            Log.d("", "KG fertilizer1: $kgFertilizer1")
            Log.d("", "dateOfRecommendation: $dateOfRecommendation")
            Log.d("", "storageType: $initialStorageType")
            Log.d("", "savedOnline: $isSavedOnline")

            // Constructing SoilData object
            val soilData = SoilData(
                cropType, nitrogen, phosphorus, potassium, phLevel, ecLevel, humidity, temperature, soilTexture
            )

            // Constructing RequiredFertilizerData object
            val requiredFertilizerData = RequiredFertilizerData(
                requiredN, requiredP, requiredK,
                fertilizer1, fertilizer2, fertilizer3,
                kgFertilizer1, kgFertilizer2, kgFertilizer3,
                bagFertilizer1, bagFertilizer2, bagFertilizer3
            )

            // Constructing RecommendationData object
            val recommendationData = RecommendationData(
                recommendationID, userID, soilData, requiredFertilizerData,
                dateOfRecommendation, initialStorageType, isSavedOnline
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
            // Soil Data
            val cropType = cursor.getString(cursor.getColumnIndexOrThrow(KEY_CROP_TYPE))
            val nitrogen = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_NITROGEN))
            val phosphorus = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_PHOSPHORUS))
            val potassium = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_POTASSIUM))
            val phLevel = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_PH_LEVEL))
            val ecLevel = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_EC_LEVEL))
            val humidity = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_HUMIDITY))
            val temperature = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_TEMPERATURE))
            val soilTexture = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SOIL_TEXTURE))
            // Required Fertilizer Data
            val requiredN = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_REQUIRED_N))
            val requiredP = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_REQUIRED_P))
            val requiredK = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_REQUIRED_K))
            val fertilizer1 = cursor.getString(cursor.getColumnIndexOrThrow(KEY_FERTILIZER_1))
            val fertilizer2 = cursor.getString(cursor.getColumnIndexOrThrow(KEY_FERTILIZER_2))
            val fertilizer3 = cursor.getString(cursor.getColumnIndexOrThrow(KEY_FERTILIZER_3))
            val kgFertilizer1 = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_KG_FERTILIZER_1))
            val kgFertilizer2 = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_KG_FERTILIZER_2))
            val kgFertilizer3 = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_KG_FERTILIZER_3))
            val bagFertilizer1 = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_BAG_FERTILIZER_1))
            val bagFertilizer2 = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_BAG_FERTILIZER_2))
            val bagFertilizer3 = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_BAG_FERTILIZER_3))
            // Recommendation Info Data
            val dateOfRecommendation = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE_OF_RECOMMENDATION))
            val initialStorageType = cursor.getString(cursor.getColumnIndexOrThrow(KEY_INITIAL_STORAGE_TYPE))
            val isSavedOnline = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_SAVED_ONLINE)) == 1

            Log.d("", "recommendationID: $recommendationID")
            Log.d("", "currentUserUID: $userID")
            Log.d("", "soilData: $nitrogen")
            Log.d("", "fertilizer1: $fertilizer1")
            Log.d("", "KG fertilizer1: $kgFertilizer1")
            Log.d("", "dateOfRecommendation: $dateOfRecommendation")
            Log.d("", "storageType: $initialStorageType")
            Log.d("", "savedOnline: $isSavedOnline")

            // Constructing SoilData object
            val soilData = SoilData(
                cropType, nitrogen, phosphorus, potassium, phLevel, ecLevel, humidity, temperature, soilTexture
            )

            // Constructing RequiredFertilizerData object
            val requiredFertilizerData = RequiredFertilizerData(
                requiredN, requiredP, requiredK,
                fertilizer1, fertilizer2, fertilizer3,
                kgFertilizer1, kgFertilizer2, kgFertilizer3,
                bagFertilizer1, bagFertilizer2, bagFertilizer3
            )

            // Constructing RecommendationData object
            recommendationData = RecommendationData(
                recommendationID, userID, soilData, requiredFertilizerData,
                dateOfRecommendation, initialStorageType, isSavedOnline
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
        Log.d("SQLiteModel", "Starting data sync with Firebase.")
        val firebaseDB = Firebase.database.reference
        val localDataList = getAllSoilData() // Assuming this retrieves the list correctly

        if (localDataList.isEmpty()) {
            Log.d("SQLiteModel", "No data to sync.")
            return
        }

        localDataList.forEach { recommendationData ->
            if (!recommendationData.isSavedOnline) {
                Log.d("SQLiteModel", "Syncing data for recommendationID: ${recommendationData.recommendationID}")
                // Define Firebase paths
                val referenceUser = firebaseDB.child("SmartSoilPH").child("Users").child(recommendationData.userID)
                val referenceDetails = referenceUser.child("RecommendationHistory").child(recommendationData.recommendationID)

                // Push to Firebase
                referenceDetails.setValue(recommendationData.apply { this.isSavedOnline = true }).addOnSuccessListener {
                    // Use a method to update local DB flag
                    updateIsSavedOnlineFlag(recommendationData.recommendationID, true)
                    Log.d("SQLiteModel", "Successfully synced data for recommendationID: ${recommendationData.recommendationID}")
                }.addOnFailureListener {
                    Log.e("DatabaseSync", "Failed to sync recommendationID: ${recommendationData.recommendationID}")
                }
            }
        }
    }

    fun updateIsSavedOnlineFlag(recommendationID: String, isSavedOnline: Boolean) {
        val db = this.writableDatabase // Ensure this is the writable database instance

        try {
            val contentValues = ContentValues()
            contentValues.put(KEY_IS_SAVED_ONLINE, if (isSavedOnline) 1 else 0)

            db.update(TABLE_RECOMMENDATION, contentValues, "$KEY_RECOMMENDATION_ID=?", arrayOf(recommendationID))
        } catch (e: Exception) {
            Log.e("SQLiteModel", "Error updating isSavedOnline flag: ${e.message}")
        }
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
