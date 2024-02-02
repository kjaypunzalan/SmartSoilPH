package com.iacademy.smartsoilph.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.iacademy.smartsoilph.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class ReportsActivity : AppCompatActivity() {

    //declare layout variables
    private lateinit var pieChart1: PieChart
    private lateinit var pieChart2: PieChart
    private lateinit var lineChart1: LineChart
    private lateinit var lineChart2: LineChart
    private lateinit var barChart1: BarChart

    //declare Firebase variables
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // initialize variables
        pieChart1 = findViewById<PieChart>(R.id.pieChart1);
        pieChart2 = findViewById<PieChart>(R.id.pieChart2);
        lineChart1 = findViewById<LineChart>(R.id.lineChart1);
        lineChart2 = findViewById<LineChart>(R.id.lineChart2);
        barChart1 = findViewById<BarChart>(R.id.barChart1);

        //fetch Data in Reports
        fetchAndDisplayCumulativeFertilizerUsage()
        fetchAndDisplayPhLevelTrend()
        fetchAndDisplayInitialStorageTypeData()
    }

    private fun fetchAndDisplayInitialStorageTypeData() {
        val reference = Firebase.database.reference.child("SmartSoilPH")
            .child("Users").child(auth.currentUser!!.uid).child("RecommendationHistory")

        reference.get().addOnSuccessListener { snapshot ->
            var sqliteCount = 0
            var firebaseCount = 0

            snapshot.children.forEach {
                val storageType = it.child("initialStorageType").value.toString()
                if (storageType == "Local SQLite") sqliteCount++
                if (storageType == "Cloud Firebase") firebaseCount++
            }

            val entries = ArrayList<PieEntry>()
            entries.add(PieEntry(sqliteCount.toFloat(), "SQLite"))
            entries.add(PieEntry(firebaseCount.toFloat(), "Firebase"))

            val dataSet = PieDataSet(entries, "Storage Type")
            dataSet.colors = listOf(Color.MAGENTA, Color.BLUE)
            val data = PieData(dataSet)
            pieChart1.data = data

            // Create an empty Description object and set it to the chart to remove the top labels
            val description = Description()
            description.text = "Recent storage type save"
            pieChart1.description = description

            pieChart1.invalidate()
        }.addOnFailureListener {
            // Handle any errors
        }
    }

    private fun fetchAndDisplayCumulativeFertilizerUsage() {
        val reference = Firebase.database.reference.child("SmartSoilPH")
            .child("Users").child(auth.currentUser!!.uid).child("RecommendationHistory")

        reference.get().addOnSuccessListener { snapshot ->
            val entries = ArrayList<Entry>()
            var cumulativeTotal = 0f

            snapshot.children.forEach { dataSnapshot ->
                val dateStr = dataSnapshot.child("dateOfRecommendation").value.toString()
                val dateFormat = SimpleDateFormat("MMMM dd, yyyy (EEE) '@'hh:mma", Locale.US)
                val date = dateFormat.parse(dateStr)
                val fertilizerAmount = dataSnapshot.child("fertilizerRecommendation").getValue(Float::class.java) ?: 0f

                cumulativeTotal += fertilizerAmount
                if (date != null) {
                    entries.add(Entry(date.time.toFloat(), cumulativeTotal))
                }
            }

            val dataSet = LineDataSet(entries, "Cumulative Fertilizer Usage")
            val data = LineData(dataSet)
            lineChart1.data = data

            // Create an empty Description object and set it to the chart to remove the top labels
            val description = Description()
            description.text = "Total fertilizer usage added per entry"
            lineChart1.description = description

            //Hide X Axis
            val xAxis = lineChart1.xAxis
            xAxis.setDrawLabels(false)
            lineChart1.invalidate() //refresh the chart
        }
    }

    private fun fetchAndDisplayPhLevelTrend() {
        val reference = Firebase.database.reference.child("SmartSoilPH")
            .child("Users").child(auth.currentUser!!.uid).child("RecommendationHistory")

        reference.get().addOnSuccessListener { snapshot ->
            val entries = ArrayList<Entry>()

            snapshot.children.forEach { dataSnapshot ->
                val dateStr = dataSnapshot.child("dateOfRecommendation").value.toString()
                val dateFormat = SimpleDateFormat("MMMM dd, yyyy (EEE) '@'hh:mma", Locale.US)
                val date = dateFormat.parse(dateStr)
                val phLevel = dataSnapshot.child("soilData").child("phLevel").getValue(Float::class.java) ?: 0f

                if (date != null) {
                    entries.add(Entry(date.time.toFloat(), phLevel))
                }
            }

            val dataSet = LineDataSet(entries, "Soil pH Trend")
            val data = LineData(dataSet)
            lineChart2.data = data

            // Create an empty Description object and set it to the chart to remove the top labels
            val description = Description()
            description.text = "Trend of the soil pH Level"
            lineChart2.description = description


            //Hide X Axis
            val xAxis = lineChart2.xAxis
            xAxis.setDrawLabels(false)
            lineChart2.invalidate()
        }
    }

}