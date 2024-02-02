package com.iacademy.smartsoilph.activities

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.EaseOutBack
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.datamodels.SoilData
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ReportsActivity : AppCompatActivity() {

    //declare layout variables
    private lateinit var pieChart1: PieChart
    private lateinit var pieChart2: PieChart
    private lateinit var lineChart1: LineChart
    private lateinit var lineChart2: LineChart
    private lateinit var barChart1: BarChart
    private lateinit var btnDownload: Button
    private lateinit var scrollView: ScrollView

    //declare Firebase variables
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // initialize variables
        initializeLayout()

        //fetch and display reports
        fetchAndDisplayInitialStorageTypeData()
        fetchAndDisplayPHLevelDistribution()
        fetchAndDisplayCumulativeFertilizerUsage()
        fetchAndDisplayPhLevelTrend()
        fetchAndDisplayMonthlySoilHealth()

        // Initialize download button and set its click listener
        btnDownload.setOnClickListener {
            captureScrollView(scrollView)
        }
    }

    /**
     * A. Initialize Layout
     */
    private fun initializeLayout() {
        pieChart1 = findViewById<PieChart>(R.id.pieChart1);
        pieChart2 = findViewById<PieChart>(R.id.pieChart2);
        lineChart1 = findViewById<LineChart>(R.id.lineChart1);
        lineChart2 = findViewById<LineChart>(R.id.lineChart2);
        barChart1 = findViewById<BarChart>(R.id.barChart1);
        scrollView = findViewById<ScrollView>(R.id.sv_scrollview)
        btnDownload = findViewById<Button>(R.id.btnDownload);
    }

    /**
     * B. Total Saves
     */
    private fun fetchAndDisplayInitialStorageTypeData() {
        val reference = Firebase.database.reference.child("SmartSoilPH")
            .child("Users").child(auth.currentUser!!.uid).child("RecommendationHistory")

        reference.get().addOnSuccessListener { snapshot ->
            var sqliteCount = 0
            var firebaseCount = 0
            var total = 0

            // Get Each Data
            snapshot.children.forEach {
                val storageType = it.child("initialStorageType").value.toString()
                if (storageType == "Local SQLite") sqliteCount++
                if (storageType == "Cloud Firebase") firebaseCount++
            }

            // Add Entries
            total = sqliteCount + firebaseCount
            val entries = ArrayList<PieEntry>()
            entries.add(PieEntry(sqliteCount.toFloat(), "SQLite"))
            entries.add(PieEntry(firebaseCount.toFloat(), "Firebase"))
            entries.add(PieEntry(total.toFloat(), "Total"))

            // Set Description
            val description = Description()
            description.text = "Recent storage type save"
            pieChart1.description = description

            // Set Color
            val dataSet = PieDataSet(entries, "Storage Type")
            val colorBlue1 = ContextCompat.getColor(applicationContext, R.color.main_blue)
            val colorBlue2 = ContextCompat.getColor(applicationContext, R.color.ultramarine_blue)
            val colorRed = ContextCompat.getColor(applicationContext, R.color.infra_red)
            dataSet.colors = listOf(colorRed, colorBlue1, colorBlue2)

            // Animate Chart
            pieChart1.animateXY(2000, 1000)

            // Push Dataset
            val data = PieData(dataSet)
            pieChart1.data = data

            // Refresh Chart
            pieChart1.invalidate()

        }.addOnFailureListener {
            // Handle any errors
        }
    }

    /**
     * C. pH Level Distribution
     */
    private fun fetchAndDisplayPHLevelDistribution() {
        val reference = Firebase.database.reference.child("SmartSoilPH")
            .child("Users").child(auth.currentUser!!.uid).child("RecommendationHistory")

        reference.get().addOnSuccessListener { snapshot ->
            var acidicCount = 0f
            var neutralCount = 0f
            var alkalineCount = 0f

            // Get Each Data
            snapshot.children.forEach {
                val phLevel = it.child("soilData").child("phLevel").value.toString().toFloatOrNull()

                phLevel?.let { level ->
                    when {
                        level < 5.5 -> acidicCount++
                        level <= 7 -> neutralCount++
                        level > 7 -> alkalineCount++
                    }
                }
            }

            // Add Entries
            val entries = ArrayList<PieEntry>()
            entries.add(PieEntry(acidicCount, "Acidic"))
            entries.add(PieEntry(neutralCount, "Neutral"))
            entries.add(PieEntry(alkalineCount, "Alkaline"))

            // Set Description
            val description = Description()
            description.text = "Soil Acidity"
            pieChart2.description = description

            // Set Color
            val dataSet = PieDataSet(entries, "Storage Type")
            val colorBlue1 = ContextCompat.getColor(applicationContext, R.color.main_blue)
            val colorBlue2 = ContextCompat.getColor(applicationContext, R.color.ultramarine_blue)
            val colorRed = ContextCompat.getColor(applicationContext, R.color.infra_red)
            dataSet.colors = listOf(colorRed, colorBlue1, colorBlue2)

            // Animate Chart
            pieChart2.animateXY(2500, 1000)

            // Push Dataset
            val data = PieData(dataSet)
            pieChart2.data = data

            // Refresh Chart
            pieChart2.invalidate()

        }.addOnFailureListener {
            // Handle any errors
        }
    }

    /**
     * D. Cumulative Fertilizer Usage
     */
    private fun fetchAndDisplayCumulativeFertilizerUsage() {
        val reference = Firebase.database.reference.child("SmartSoilPH")
            .child("Users").child(auth.currentUser!!.uid).child("RecommendationHistory")

        reference.get().addOnSuccessListener { snapshot ->
            val entries = ArrayList<Entry>()
            var cumulativeTotal = 0f
            var total = 0

            // Get Each Data
            snapshot.children.forEach { dataSnapshot ->
                val dateStr = dataSnapshot.child("dateOfRecommendation").value.toString()
                val dateFormat = SimpleDateFormat("MMMM dd, yyyy (EEE) '@'hh:mma", Locale.US)
                val date = dateFormat.parse(dateStr)
                val fertilizerAmount = dataSnapshot.child("fertilizerRecommendation").getValue(Float::class.java) ?: 0f

                cumulativeTotal += fertilizerAmount
                total++
                if (date != null) {
                    entries.add(Entry(date.time.toFloat(), cumulativeTotal))
                }
            }

            // Set Description
            val description = Description()
            description.text = "Total fertilizer usage added per entry"
            lineChart1.description = description

            // Hide X Axis
            lineChart1.xAxis.setDrawLabels(false)
            lineChart1.xAxis.setDrawAxisLine(false)
            lineChart1.axisLeft.setDrawGridLines(false)
            lineChart1.axisRight.setDrawGridLines(false)
            lineChart1.xAxis.setDrawGridLines(false)

            // Animate Chart
            lineChart1.animateX(5000, Easing.EaseInOutQuad)

            // Push Dataset
            val dataSet = LineDataSet(entries, "Cumulative Fertilizer Usage")
            val data = LineData(dataSet)
            lineChart1.data = data

            // Refresh the chart
            lineChart1.invalidate()


        }
    }

    /**
     * E. pH Level Trend
     */
    private fun fetchAndDisplayPhLevelTrend() {
        val reference = Firebase.database.reference.child("SmartSoilPH")
            .child("Users").child(auth.currentUser!!.uid).child("RecommendationHistory")

        reference.get().addOnSuccessListener { snapshot ->
            val entries = ArrayList<Entry>()

            // Get Each Data
            snapshot.children.forEach { dataSnapshot ->
                val dateStr = dataSnapshot.child("dateOfRecommendation").value.toString()
                val dateFormat = SimpleDateFormat("MMMM dd, yyyy (EEE) '@'hh:mma", Locale.US)
                val date = dateFormat.parse(dateStr)
                val phLevel = dataSnapshot.child("soilData").child("phLevel").getValue(Float::class.java) ?: 0f

                if (date != null) {
                    entries.add(Entry(date.time.toFloat(), phLevel))
                }
            }

            // Set Description
            val description = Description()
            description.text = "Trend of the soil pH Level"
            lineChart2.description = description

            // Hide X Axis
            lineChart2.xAxis.setDrawLabels(false)
            lineChart2.xAxis.setDrawAxisLine(false)
            lineChart2.axisLeft.setDrawGridLines(false)
            lineChart2.axisRight.setDrawGridLines(false)
            lineChart2.xAxis.setDrawGridLines(false)

            // Animate Chart
            lineChart2.animateXY(3000, 1000)

            // Push Dataset
            val dataSet = LineDataSet(entries, "Soil pH Trend")
            val data = LineData(dataSet)
            lineChart2.data = data

            // Refresh the chart
            lineChart2.invalidate()
        }
    }

    /**
     * F. Monthly Soil Statistics
     */
    private fun fetchAndDisplayMonthlySoilHealth() {
        val reference = Firebase.database.reference.child("SmartSoilPH")
            .child("Users").child(auth.currentUser!!.uid).child("RecommendationHistory")

        reference.get().addOnSuccessListener { snapshot ->
            val monthlyNPK = mutableMapOf<String, MutableList<SoilData>>()

            snapshot.children.forEach { dataSnapshot ->
                val dateStr = dataSnapshot.child("dateOfRecommendation").value.toString()
                val dateFormat = SimpleDateFormat("MMMM dd, yyyy (EEE) '@'hh:mma", Locale.US)
                val date = dateFormat.parse(dateStr)
                val calendar = Calendar.getInstance()
                date?.let { calendar.time = it }
                val monthYearKey = "${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}"

                val nitrogen = dataSnapshot.child("soilData").child("nitrogen").getValue(Float::class.java) ?: 0f
                val phosphorus = dataSnapshot.child("soilData").child("phosphorus").getValue(Float::class.java) ?: 0f
                val potassium = dataSnapshot.child("soilData").child("potassium").getValue(Float::class.java) ?: 0f

                val soilData = SoilData(nitrogen, phosphorus, potassium, 0f, 0f, 0f, 0f)
                monthlyNPK.getOrPut(monthYearKey) { mutableListOf() }.add(soilData)
            }

            val entries = ArrayList<BarEntry>()
            val npkLabelsMap = mutableMapOf<String, String>()
            var index = 0f

            monthlyNPK.keys.sorted().forEach { month ->
                val soils = monthlyNPK[month]!!
                val avgNitrogen = soils.map { it.nitrogen }.average().toFloat()
                val avgPhosphorus = soils.map { it.phosphorus }.average().toFloat()
                val avgPotassium = soils.map { it.potassium }.average().toFloat()

                val npkString = "${avgNitrogen.toInt()}-${avgPhosphorus.toInt()}-${avgPotassium.toInt()}"
                entries.add(BarEntry(index++, 1f)) // Dummy height for the bar
                npkLabelsMap[month] = npkString
            }

            // Set Description
            val description = Description()
            description.text = "Monthly soil statistics"
            barChart1.description = description

            // Hide X Axis
            barChart1.xAxis.setDrawLabels(false)
            barChart1.xAxis.setDrawAxisLine(false)
            barChart1.axisLeft.setDrawGridLines(false)
            barChart1.axisRight.setDrawGridLines(false)
            barChart1.xAxis.setDrawGridLines(false)

            // Animate Chart
            barChart1.animateXY(3000, 1000)

            val barDataSet = BarDataSet(entries, "February")
            barDataSet.valueFormatter = NpkValueFormatter(npkLabelsMap)

            val barData = BarData(barDataSet)
            barChart1.data = barData

            barChart1.invalidate() // Refresh the chart
        }
    }

    class NpkValueFormatter(private val npkDataMap: Map<String, String>) : ValueFormatter() {
        override fun getBarLabel(barEntry: BarEntry): String {
            // Assuming barEntry.x is the index for month
            val monthIndex = barEntry.x.toInt()
            val monthKey = npkDataMap.keys.sorted()[monthIndex]
            // Return the NPK string for this month
            return npkDataMap[monthKey] ?: ""
        }
    }




    /**
     * G. Download Reports
     */
    private fun captureScrollView(scrollView: ScrollView) {
        // Measure the view at its full height
        scrollView.measure(
            View.MeasureSpec.makeMeasureSpec(scrollView.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        // Create a Bitmap to hold the content
        val bitmap = Bitmap.createBitmap(scrollView.width, scrollView.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        scrollView.draw(canvas)

        // Use this bitmap as needed
        saveBitmapToStorage(bitmap)
    }


    private fun saveBitmapToStorage(bitmap: Bitmap) {
        // Ensure you have the WRITE_EXTERNAL_STORAGE permission if under Android 10
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
        val fileName = "Reports_${System.currentTimeMillis()}.png"
        val file = File(path, fileName)

        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            // Notify the user or update UI
            Toast.makeText(this, "Report saved to Downloads folder as $fileName", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save the report", Toast.LENGTH_LONG).show()
        }
    }

}