package com.iacademy.smartsoilph.activities

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.datamodels.SoilData
import com.iacademy.smartsoilph.models.SQLiteModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue


class ReportsActivity : BaseActivity() {

    //declare layout variables
    private lateinit var pieChart1: PieChart
    private lateinit var pieChart2: PieChart
    private lateinit var lineChart1: LineChart
    private lateinit var lineChart2: LineChart
    private lateinit var lineChart3: LineChart
    private lateinit var barChart1: BarChart
    private lateinit var barChart2: BarChart
    private lateinit var tvPCSummary1: TextView
    private lateinit var tvPCSummary2: TextView
    private lateinit var tvLCSummary1: TextView
    private lateinit var tvLCSummary2: TextView
    private lateinit var tvLCSummary3: TextView
    private lateinit var tvBCSummary1: TextView
    private lateinit var tvBCSummary2: TextView
    private lateinit var btnJPG: ImageView
    private lateinit var btnPDF: ImageView
    private lateinit var btnReturn: ImageView
    private lateinit var scrollView: NestedScrollView


    //declare Firebase variables
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        // initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // initialize variables
        initializeLayout()
        setupButtonNavigation()

        // fetch and display reports
        fetchAndDisplayInitialStorageTypeData()
        fetchAndDisplayPHLevelDistribution()
        fetchAndDisplayCumulativeFertilizerUsage()
        fetchAndDisplayPhLevelTrend()
        fetchAndDisplaySoilSampleEntries()
        fetchAndDisplayWeeklySoilHealth()
        fetchAndDisplayMonthlySoilHealth()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    /*******************************
     * A. Initialize Layout
     *-----------------------------*/
    private fun initializeLayout() {
        pieChart1 = findViewById<PieChart>(R.id.pieChart1);
        pieChart2 = findViewById<PieChart>(R.id.pieChart2);
        lineChart1 = findViewById<LineChart>(R.id.lineChart1);
        lineChart2 = findViewById<LineChart>(R.id.lineChart2);
        lineChart3 = findViewById<LineChart>(R.id.lineChart3);
        barChart1 = findViewById<BarChart>(R.id.barChart1);
        barChart2 = findViewById<BarChart>(R.id.barChart2);
        tvPCSummary1 = findViewById<TextView>(R.id.tv_pc_sum1);
        tvPCSummary2 = findViewById<TextView>(R.id.tv_pc_sum2);
        tvLCSummary1 = findViewById<TextView>(R.id.tv_lc_sum1);
        tvLCSummary2 = findViewById<TextView>(R.id.tv_lc_sum2);
        tvLCSummary3 = findViewById<TextView>(R.id.tv_lc_sum2);
        tvBCSummary1 = findViewById<TextView>(R.id.tv_bc_sum);
        tvBCSummary2 = findViewById<TextView>(R.id.tv_bc_sum);
        scrollView = findViewById<NestedScrollView>(R.id.sv_scrollview)
        btnJPG = findViewById<ImageView>(R.id.btnJPG);
        btnPDF = findViewById<ImageView>(R.id.btnPDF);
        btnReturn = findViewById<ImageView>(R.id.toolbar_back_icon)
    }

    /*******************************
     * B. Button Logistics
     *-----------------------------*/
    private fun setupButtonNavigation() {
        btnReturn.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
        btnJPG.setOnClickListener {
            saveAsJPG(scrollView)
        }
        btnPDF.setOnClickListener {
            saveAsPdf(scrollView)
        }
    }

    /*******************************
     * C. Recent Saves
     *-----------------------------*/
    private fun fetchAndDisplayInitialStorageTypeData() {
        val sqliteModel = SQLiteModel(this)

        val recommendationDataList = sqliteModel.getAllSoilData()
        val dateFormat = SimpleDateFormat("MM-dd-yyyy '@'hh:mma", Locale.US)
        val sevenDaysAgo = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -7)
        }.time

        val recentRecommendationDataList = recommendationDataList.filter {
            val dateOfRecommendation = dateFormat.parse(it.dateOfRecommendation)
            dateOfRecommendation != null && dateOfRecommendation.after(sevenDaysAgo)
        }
        var sqliteCount = 0
        var firebaseCount = 0

        recentRecommendationDataList.forEach {
            if (it.initialStorageType == "Local SQLite") sqliteCount++
            if (it.initialStorageType == "Cloud Firebase") firebaseCount++
        }

        val total = sqliteCount + firebaseCount
        val entries = ArrayList<PieEntry>().apply {
            add(PieEntry(sqliteCount.toFloat(), "Offline"))
            add(PieEntry(firebaseCount.toFloat(), "Online"))
            add(PieEntry(total.toFloat(), "Total"))
        }

        // Getting the formatted string from resources and inserting the variable values
        val summaryText = getString(R.string.sub_summary1, firebaseCount, sqliteCount, total)
        tvPCSummary1.text = summaryText

        // Set Description
        val description = Description()
        description.text = "Storage Type"
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
    }

    /*******************************
     * D. pH Level Distribution
     *-----------------------------*/
    private fun fetchAndDisplayPHLevelDistribution() {
        val sqliteModel = SQLiteModel(this)
        val recommendationDataList = sqliteModel.getAllSoilData()
        val dateFormat = SimpleDateFormat("MM-dd-yyyy '@'hh:mma", Locale.US)
        val sevenDaysAgo = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -7)
        }.time

        val recentRecommendationDataList = recommendationDataList.filter {
            val dateOfRecommendation = dateFormat.parse(it.dateOfRecommendation)
            dateOfRecommendation != null && dateOfRecommendation.after(sevenDaysAgo)
        }

        var acidicCount = 0f
        var neutralCount = 0f
        var alkalineCount = 0f

        recentRecommendationDataList.forEach {
            val phLevel = it.soilData.phLevel.absoluteValue
            phLevel?.let { level ->
                when {
                    level < 5.5 -> acidicCount++
                    level <= 7 -> neutralCount++
                    level > 7 -> alkalineCount++
                }
            }
        }

        // Getting the formatted string from resources and inserting the variable values
        val summaryText = getString(R.string.sub_summary2, acidicCount, neutralCount, alkalineCount)
        tvPCSummary2.text = summaryText


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


    }

    /***********************************
     * E. Cumulative Fertilizer Usage
     *---------------------------------*/
    private fun fetchAndDisplayCumulativeFertilizerUsage() {
        val sqliteModel = SQLiteModel(this)
        val allData = sqliteModel.getAllSoilData()

        // Filter data for the last 7 days
        val dateFormat = SimpleDateFormat("MM-dd-yyyy '@'hh:mma", Locale.US)
        val currentDate = Calendar.getInstance()
        val sevenDaysAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }
        val recentData = allData.filter { data ->
            dateFormat.parse(data.dateOfRecommendation)?.let { date ->
                date.after(sevenDaysAgo.time) && date.before(currentDate.time)
            } ?: false
        }

        // Calculate cumulative fertilizer usage
        var cumulativeTotal = 0f
        val entries = ArrayList<Entry>()
        recentData.sortedBy { it.dateOfRecommendation }.forEach { data ->
            val date = dateFormat.parse(data.dateOfRecommendation)
            cumulativeTotal += data.requiredFertilizerData.kgFertilizer1
            if (date != null) {
                entries.add(Entry(date.time.toFloat(), cumulativeTotal))
            }
        }

        // Update the UI
        val summaryText = getString(R.string.sub_summary3, cumulativeTotal)
        tvLCSummary1.text = summaryText

        // Chart setup
        val description = Description().apply { text = "Cumulative Fertilizer Usage" }
        lineChart1.apply {
            this.description = description
            xAxis.apply {
                setDrawLabels(false)
                setDrawAxisLine(false)
                setDrawGridLines(false)
            }
            axisLeft.setDrawGridLines(false)
            axisRight.setDrawGridLines(false)

            animateX(5000, Easing.EaseInOutQuad)

            val dataSet = LineDataSet(entries, "Cumulative Fertilizer Usage").apply {
                // You can customize the dataset's appearance here
            }
            data = LineData(dataSet)

            invalidate() // Refresh the chart
        }
    }

    /***********************************
     * F. pH Level Trend
     *---------------------------------*/
    private fun fetchAndDisplayPhLevelTrend() {
        val sqliteModel = SQLiteModel(this)
        val recommendationDataList = sqliteModel.getAllSoilData()

        val entries = ArrayList<Entry>()
        val dateFormat = SimpleDateFormat("MM-dd-yyyy '@'hh:mma", Locale.US)
        var recentPH = 0f

        recommendationDataList.forEach { recommendationData ->
            val dateStr = recommendationData.dateOfRecommendation
            val date = dateFormat.parse(dateStr)
            val phLevel = recommendationData.soilData.phLevel

            if (date != null) {
                // Assuming you're using an Entry class similar to MPAndroidChart's Entry class for plotting
                entries.add(Entry(date.time.toFloat(), phLevel))
                recentPH = phLevel
            }
        }

        // Getting the formatted string from resources and inserting the variable values
        if (recentPH > 6.5) {
            var summaryText = getString(com.iacademy.smartsoilph.R.string.sub_summary4, recentPH)
            tvLCSummary2.text = summaryText
        }
        if (recentPH < 5.5) {
            var summaryText = getString(com.iacademy.smartsoilph.R.string.sub_summary44, recentPH)
            tvLCSummary2.text = summaryText
        }

        val summaryText = getString(R.string.sub_summary4, recentPH)
        tvLCSummary2.text = summaryText

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

    /***********************************
     * G. Soil Entry Statistics
     *---------------------------------*/
    private fun fetchAndDisplaySoilSampleEntries() {
        val sqliteModel = SQLiteModel(this)
        val recommendationDataList = sqliteModel.getAllSoilData()

        val dateFormat = SimpleDateFormat("MM-dd-yyyy '@'hh:mma", Locale.US)
        val dateDisplayFormat = SimpleDateFormat("MM-dd-yyyy", Locale.US)

        val dailyNutrientMap = recommendationDataList.groupBy {
            dateDisplayFormat.format(dateFormat.parse(it.dateOfRecommendation) ?: Date())
        }.mapValues { (_, entries) ->
            val avgN = entries.map { it.soilData.nitrogen }.average().toFloat()
            val avgP = entries.map { it.soilData.phosphorus }.average().toFloat()
            val avgK = entries.map { it.soilData.potassium }.average().toFloat()
            Triple(avgN, avgP, avgK)
        }

        val entries = dailyNutrientMap.entries.mapIndexed { index, entry ->
            val avgValues = entry.value
            val average = listOf(avgValues.first, avgValues.second, avgValues.third).average().toFloat()
            BarEntry(index.toFloat(), average, "${avgValues.first.toInt()}-${avgValues.second.toInt()}-${avgValues.third.toInt()}")
        }

        val dataSet = BarDataSet(entries, "Daily Average N-P-K")
        dataSet.setValueFormatter(object : ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry): String {
                // Getting the formatted string from resources and inserting the variable values
                return barEntry.data.toString()
            }
        })

        val barData = BarData(dataSet)
        barChart2.data = barData

        // Assuming you have a BarChart instance named barChart2
        barChart2.xAxis.valueFormatter = IndexAxisValueFormatter(dailyNutrientMap.keys.toList())
        barChart2.xAxis.setLabelRotationAngle(-45f)
        barChart2.xAxis.granularity = 1f
        barChart2.description.text = "Your daily average N-P-K within 7 days."
        barChart2.animateY(1000)
        barChart2.invalidate()
    }



    /***********************************
     * H. Weekly Soil Statistics
     *---------------------------------*/
    private fun fetchAndDisplayWeeklySoilHealth() {
        val sqliteModel = SQLiteModel(this)
        val recommendationDataList = sqliteModel.getAllSoilData()

        val dateFormat = SimpleDateFormat("MM-dd-yyyy '@'hh:mma", Locale.US)
        val calendar = Calendar.getInstance()

        val weeklyNutrientMap = recommendationDataList.groupBy { recommendationData ->
            val date = dateFormat.parse(recommendationData.dateOfRecommendation) ?: return
            calendar.time = date
            val week = calendar.get(Calendar.WEEK_OF_YEAR)
            val year = calendar.get(Calendar.YEAR)
            "$year-$week"
        }.mapValues { (_, entries) ->
            val avgN = entries.map { it.soilData.nitrogen }.average().toFloat()
            val avgP = entries.map { it.soilData.phosphorus }.average().toFloat()
            val avgK = entries.map { it.soilData.potassium }.average().toFloat()
            Triple(avgN, avgP, avgK)
        }

        val entries = weeklyNutrientMap.entries.mapIndexed { index, entry ->
            Entry(index.toFloat(), entry.value.toList().average().toFloat())
        }

        val lineDataSet = LineDataSet(entries, "Weekly Average N-P-K")
        val lineData = LineData(lineDataSet)

        lineChart3.data = lineData
        lineChart3.xAxis.valueFormatter = IndexAxisValueFormatter(weeklyNutrientMap.keys.toList())
        lineChart3.xAxis.setLabelRotationAngle(-45f)
        lineChart3.xAxis.granularity = 1f
        lineChart3.description.text = "Weekly Average N-P-K"
        lineChart3.animateY(1000)

        // Custom value formatter to show N-P-K values
        lineData.setValueFormatter(object : ValueFormatter() {
            override fun getPointLabel(entry: Entry): String {
                val weekData = weeklyNutrientMap.entries.elementAtOrNull(entry.x.toInt())?.value
                return weekData?.let { "${it.first.toInt()}-${it.second.toInt()}-${it.third.toInt()}" } ?: ""
            }
        })

        lineChart3.invalidate()
    }

    /***********************************
     * I. Monthly Soil Statistics
     *---------------------------------*/
    private fun fetchAndDisplayMonthlySoilHealth() {
        val sqliteModel = SQLiteModel(this)
        val recommendationDataList = sqliteModel.getAllSoilData()

        val dateFormat = SimpleDateFormat("MM-dd-yyyy '@'hh:mma", Locale.US)
        val monthYearFormat = SimpleDateFormat("MMMM", Locale.US) // For displaying month names

        val monthlyNPK = mutableMapOf<String, MutableList<SoilData>>()
        recommendationDataList.forEach { recommendationData ->
            val date = dateFormat.parse(recommendationData.dateOfRecommendation)
            val calendar = Calendar.getInstance()
            date?.let { calendar.time = it }
            val monthYearKey = "${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}"

            val soilData = SoilData(
                recommendationData.soilData.cropType,
                recommendationData.soilData.nitrogen,
                recommendationData.soilData.phosphorus,
                recommendationData.soilData.potassium,
                0f, 0f, 0f, 0f,
                "clay"
            )
            monthlyNPK.getOrPut(monthYearKey) { mutableListOf() }.add(soilData)
        }

        val entries = ArrayList<BarEntry>()
        val monthLabels = ArrayList<String>()
        val npkStrings = ArrayList<String>()
        var index = 0f

        monthlyNPK.keys.sorted().forEach { monthYearKey ->
            val soils = monthlyNPK[monthYearKey]!!
            val avgNitrogen = soils.map { it.nitrogen }.average().toFloat()
            val avgPhosphorus = soils.map { it.phosphorus }.average().toFloat()
            val avgPotassium = soils.map { it.potassium }.average().toFloat()

            val npkString = "${avgNitrogen.toInt()}-${avgPhosphorus.toInt()}-${avgPotassium.toInt()}"
            npkStrings.add(npkString)
            entries.add(BarEntry(index, avgNitrogen + avgPhosphorus + avgPotassium, npkString))

            val dateParts = monthYearKey.split("-")
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.MONTH, dateParts[0].toInt() - 1)
            calendar.set(Calendar.YEAR, dateParts[1].toInt())
            monthLabels.add(monthYearFormat.format(calendar.time))

            index++
        }

        val barDataSet = BarDataSet(entries, "Average NPK per month")
        barDataSet.valueFormatter = object : ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry): String {
                return barEntry.data.toString()
            }
        }

        val barData = BarData(barDataSet)
        barChart1.data = barData

        barChart1.xAxis.valueFormatter = IndexAxisValueFormatter(monthLabels)
        barChart1.xAxis.setDrawLabels(true)
        barChart1.xAxis.setDrawAxisLine(true)
        barChart1.xAxis.setLabelRotationAngle(-45f)
        barChart1.xAxis.granularity = 1f

        val description = Description()
        description.text = "Monthly soil statistics"
        barChart1.description = description

        barChart1.animateXY(3000, 1000)
        barChart1.invalidate() // Refresh the chart
    }


    class NpkValueFormatter(private val npkDataMap: Map<String, String>) : ValueFormatter() {
        override fun getBarLabel(barEntry: BarEntry): String {
            val monthIndex = barEntry.x.toInt()
            val monthKey = npkDataMap.keys.sorted()[monthIndex]
            return npkDataMap[monthKey] ?: ""
        }
    }


    /***********************************
     * I. Save as JPG
     *---------------------------------*/
    private fun saveAsJPG(scrollView: NestedScrollView) {
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

            // Notify media scanner
            notifyMediaScanner(file)

            // Notify the user or update UI
            Toast.makeText(this, "JPG Report saved to Downloads folder as $fileName", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save JPG report", Toast.LENGTH_LONG).show()
        }
    }

    private fun notifyMediaScanner(file: File) {
        MediaScannerConnection.scanFile(
            this,
            arrayOf(file.absolutePath),
            null
        ) { path, uri ->
            Log.d("MediaScanner", "Scanned $path:")
            Log.d("MediaScanner", "-> uri=$uri")
        }
    }

    /***********************************
     * J. Save as PDF
     *---------------------------------*/
    private fun saveAsPdf(scrollView: NestedScrollView) {
        // Measure the view at its full height
        scrollView.measure(
            View.MeasureSpec.makeMeasureSpec(scrollView.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        scrollView.layout(0, 0, scrollView.measuredWidth, scrollView.measuredHeight)

        // Create a PdfDocument with the scrollView's dimensions
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(scrollView.width, scrollView.measuredHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        // Draw the scrollView content onto the PDF page
        scrollView.draw(page.canvas)

        // Finish and write the page
        pdfDocument.finishPage(page)

        // Save the pdf document to storage
        savePdfToStorage(pdfDocument)
    }

    private fun savePdfToStorage(pdfDocument: PdfDocument) {
        val fileName = "Reports_${System.currentTimeMillis()}.pdf"

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val resolver = contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                resolver.openOutputStream(uri ?: return).use { outputStream ->
                    pdfDocument.writeTo(outputStream ?: return)
                    pdfDocument.close()
                    outputStream?.close()
                }

                Toast.makeText(this, "PDF Report saved to Downloads folder as $fileName", Toast.LENGTH_LONG).show()
            } else {
                // Check and request for permission in pre-Android 10 devices
                // This part remains unchanged in your code
                // Remember to handle onRequestPermissionsResult for the permission result
                val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
                val file = File(path, fileName)
                val outputStream = FileOutputStream(file)
                pdfDocument.writeTo(outputStream)
                pdfDocument.close()
                outputStream.close()

                // Notify media scanner
                MediaScannerConnection.scanFile(this, arrayOf(file.toString()), null, null)

                Toast.makeText(this, "PDF Report saved to Downloads folder as $fileName", Toast.LENGTH_LONG).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save PDF report", Toast.LENGTH_LONG).show()
        }
    }


}