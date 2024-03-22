package com.iacademy.smartsoilph.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.iacademy.smartsoilph.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import android.content.ContentValues
import android.media.MediaScannerConnection
import android.provider.MediaStore


class ManualActivity: BaseActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual)

        setupButtonNavigation()

    }

    private fun setupButtonNavigation() {
        val btnReturn: ImageView = findViewById(R.id.toolbar_back_icon)
        btnReturn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        val tvEngButton: CardView = findViewById(R.id.eng_card)
        val tvTagButton: CardView = findViewById(R.id.ph_card)

        tvEngButton.setOnClickListener {
            downloadPdfFile("SmartSoilPH_Manual_ENG.pdf")
        }

        tvTagButton.setOnClickListener {
            downloadPdfFile("SmartSoilPH_Manual_FIL.pdf")
        }
    }

    private fun downloadPdfFile(fileName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val resolver = contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            resolver.openOutputStream(uri ?: return).use { outputStream ->
                assets.open(fileName).use { inputStream ->
                    inputStream.copyTo(outputStream ?: return)
                }
            }

            Toast.makeText(this, "File downloaded to Downloads folder", Toast.LENGTH_LONG).show()
        } else {
            try {
                val assetManager = assets
                val inputStream: InputStream = assetManager.open(fileName)
                val outputFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)

                val outputStream = FileOutputStream(outputFile)
                inputStream.copyTo(outputStream)
                outputStream.flush()
                outputStream.close()
                inputStream.close()

                // Notify media scanner
                MediaScannerConnection.scanFile(this, arrayOf(outputFile.toString()), null, null)

                Toast.makeText(this, "File downloaded to Downloads folder", Toast.LENGTH_LONG).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to download file", Toast.LENGTH_LONG).show()
            }
        }

    }


    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
}