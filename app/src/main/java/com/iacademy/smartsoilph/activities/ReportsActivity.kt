package com.iacademy.smartsoilph.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.iacademy.smartsoilph.R

class ReportsActivity : AppCompatActivity() {

    //declare layout variables
    private lateinit var flDonutChart: FrameLayout
    private lateinit var flLineChart: FrameLayout
    private lateinit var flBarChart: FrameLayout

    //declare Firebase variables
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // initialize variables
        flDonutChart = findViewById<FrameLayout>(R.id.donut_chart_placeholder);
        flLineChart = findViewById<FrameLayout>(R.id.line_chart_placeholder);
        flBarChart = findViewById<FrameLayout>(R.id.bar_chart_placeholder);

    }

    /*private fun fetchAndDisplayInitialStorageTypeData() {
        // Example Firebase path might differ
        val reference = Firebase.database.reference.child("SmartSoilPH").child("Users").child(auth.currentUser!!.uid).child("RecommendationHistory")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var sqliteCount = 0
                var firebaseCount = 0

                snapshot.children.forEach {
                    val storageType = it.child("initialStorageType").value.toString()
                    if (storageType == "SQLite") sqliteCount++
                    else if (storageType == "Firebase") firebaseCount++
                }

                // Now that you have counts, you can display this data in a donut chart
                displayInitialStorageTypeChart(sqliteCount, firebaseCount)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle potential errors
            }
        })
    }

    private fun displayInitialStorageTypeChart(sqliteCount: Int, firebaseCount: Int) {
        val chart = findViewById<PieChart>(R.id.donut_chart_placeholder) // Adjust with your actual chart ID
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(sqliteCount.toFloat(), "SQLite"))
        entries.add(PieEntry(firebaseCount.toFloat(), "Firebase"))

        val dataSet = PieDataSet(entries, "Storage Types")
        dataSet.colors = listOf(ColorTemplate.COLORFUL_COLORS.toList())
        val data = PieData(dataSet)
        chart.data = data
        chart.invalidate() // refresh chart
    }*/


}