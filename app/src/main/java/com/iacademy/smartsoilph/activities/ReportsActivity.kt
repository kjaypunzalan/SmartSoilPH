package com.iacademy.smartsoilph.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import java.util.Locale
import kotlin.math.absoluteValue


class ReportsActivity : BaseActivity() {

    //declare layout variables
    private lateinit var pieChart1: PieChart
    private lateinit var pieChart2: PieChart
    private lateinit var lineChart1: LineChart
    private lateinit var lineChart2: LineChart
    private lateinit var barChart1: BarChart
    private lateinit var tvPCSummary1: TextView
    private lateinit var tvPCSummary2: TextView
    private lateinit var tvLCSummary1: TextView
    private lateinit var tvLCSummary2: TextView
    private lateinit var tvBCSummary1: TextView
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
        barChart1 = findViewById<BarChart>(R.id.barChart1);
        tvPCSummary1 = findViewById<TextView>(R.id.tv_pc_sum1);
        tvPCSummary2 = findViewById<TextView>(R.id.tv_pc_sum2);
        tvLCSummary1 = findViewById<TextView>(R.id.tv_lc_sum1);
        tvLCSummary2 = findViewById<TextView>(R.id.tv_lc_sum2);
        tvBCSummary1 = findViewById<TextView>(R.id.tv_bc_sum);
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

        var sqliteCount = 0
        var firebaseCount = 0

        recommendationDataList.forEach {
            if (it.initialStorageType == "Local SQLite") sqliteCount++
            if (it.initialStorageType == "Cloud Firebase") firebaseCount++
        }

        val total = sqliteCount + firebaseCount
        val entries = ArrayList<PieEntry>().apply {
            add(PieEntry(sqliteCount.toFloat(), "SQLite"))
            add(PieEntry(firebaseCount.toFloat(), "Firebase"))
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

        var acidicCount = 0f
        var neutralCount = 0f
        var alkalineCount = 0f

        recommendationDataList.forEach {
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
        val recommendationDataList = sqliteModel.getAllSoilData()

        val entries = ArrayList<Entry>()
        var cumulativeTotal = 0f

        // SimpleDateFormat to parse the stored date strings
        val dateFormat = SimpleDateFormat("MM-dd-yyyy '@'hh:mma", Locale.US)

        recommendationDataList.forEach { recommendationData ->
            // Assuming your RecommendationData model includes a dateOfRecommendation property in a suitable format
            val dateStr = recommendationData.dateOfRecommendation
            val date = dateFormat.parse(dateStr)
            val fertilizerAmount = recommendationData.requiredFertilizerData.kgFertilizer1

            cumulativeTotal += fertilizerAmount
            if (date != null) {
                // Assuming you're using an Entry class similar to MPAndroidChart's Entry class for plotting
                entries.add(Entry(date.time.toFloat(), cumulativeTotal))
            }
        }

        // Getting the formatted string from resources and inserting the variable values
        val summaryText = getString(R.string.sub_summary3, cumulativeTotal)
        tvLCSummary1.text = summaryText

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
     * G. Monthly Soil Statistics
     *---------------------------------*/
    private fun fetchAndDisplayMonthlySoilHealth() {
        val sqliteModel = SQLiteModel(this)
        val recommendationDataList = sqliteModel.getAllSoilData()

        val monthlyNPK = mutableMapOf<String, MutableList<SoilData>>()
        val dateFormat = SimpleDateFormat("MM-dd-yyyy '@'hh:mma", Locale.US)

        recommendationDataList.forEach { recommendationData ->
            val dateStr = recommendationData.dateOfRecommendation
            val date = dateFormat.parse(dateStr)
            val calendar = Calendar.getInstance()
            date?.let { calendar.time = it }
            val monthYearKey = "${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}"

            // Extracting NPK values directly from the RecommendationData's SoilData object
            val cropType = recommendationData.soilData.cropType
            val nitrogen = recommendationData.soilData.nitrogen
            val phosphorus = recommendationData.soilData.phosphorus
            val potassium = recommendationData.soilData.potassium

            // Assuming SoilData constructor matches the required structure
            //TODO: FIX
            val soilData = SoilData(
                cropType, nitrogen, phosphorus, potassium,
                0f, 0f, 0f, 0f,
                "clay")

            // Accumulating data by month and year
            monthlyNPK.getOrPut(monthYearKey) { mutableListOf() }.add(soilData)
        }

        // Now, 'monthlyNPK' contains a map of month-year keys to lists of SoilData, similar to the Firebase approach.

        var npkString = ""
        val entries = ArrayList<BarEntry>()
            val npkLabelsMap = mutableMapOf<String, String>()
            var index = 0f

            monthlyNPK.keys.sorted().forEach { month ->
                val soils = monthlyNPK[month]!!
                val avgNitrogen = soils.map { it.nitrogen }.average().toFloat()
                val avgPhosphorus = soils.map { it.phosphorus }.average().toFloat()
                val avgPotassium = soils.map { it.potassium }.average().toFloat()

                npkString = "${avgNitrogen.toInt()}-${avgPhosphorus.toInt()}-${avgPotassium.toInt()}"
                entries.add(BarEntry(index++, 1f)) // Dummy height for the bar
                npkLabelsMap[month] = npkString
            }

        // Getting the formatted string from resources and inserting the variable values
        val summaryText = getString(R.string.sub_summary5, npkString)
        tvBCSummary1.text = summaryText

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

            val barDataSet = BarDataSet(entries, "March")
            barDataSet.valueFormatter = NpkValueFormatter(npkLabelsMap)

            val barData = BarData(barDataSet)
            barChart1.data = barData

            barChart1.invalidate() // Refresh the chart

    }

    /***********************************
     * H. NpkValueFormatter
     *---------------------------------*/
    class NpkValueFormatter(private val npkDataMap: Map<String, String>) : ValueFormatter() {
        override fun getBarLabel(barEntry: BarEntry): String {
            // Assuming barEntry.x is the index for month
            val monthIndex = barEntry.x.toInt()
            val monthKey = npkDataMap.keys.sorted()[monthIndex]
            // Return the NPK string for this month
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
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = Uri.fromFile(file)
        sendBroadcast(intent)
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
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
        val fileName = "Reports_${System.currentTimeMillis()}.pdf"
        val file = File(path, fileName)

        try {
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            outputStream.close()

            // Notify the user or update UI
            Toast.makeText(this, "PDF Report saved to Downloads folder as $fileName", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save PDF report", Toast.LENGTH_LONG).show()
        }
    }

}