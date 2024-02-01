package com.iacademy.smartsoilph.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.datamodels.RecommendationData
import com.iacademy.smartsoilph.models.FirebaseModel
import com.iacademy.smartsoilph.utils.RecyclerOnItemClickListener
import com.iacademy.smartsoilph.utils.RecyclerViewAdapter
import java.util.ArrayList

class RecommendationHistoryActivity : AppCompatActivity(), RecyclerOnItemClickListener {

    //declare layout variables
    private lateinit var rvRecommendationHistory: RecyclerView

    //declare Firebase variables
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommendation_history)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // initialize variables
        rvRecommendationHistory = findViewById<RecyclerView>(R.id.rv_recommendationHistory);

        // Fetch data from Firebase
        FirebaseModel.fetchRecommendationHistory(auth) { recommendationDataList ->
            // Update RecyclerView
            val adapter = RecyclerViewAdapter(ArrayList(recommendationDataList)) { recommendationData ->
                // Handle the item click event here
                // Example: show a Toast or navigate to a detail screen
                }
            rvRecommendationHistory.adapter = adapter
            rvRecommendationHistory.layoutManager = LinearLayoutManager(this)
        }
    }

    override fun onItemClick(recommendationData: RecommendationData) {
        // Handle item click event
        // Implement what should happen when an item is clicked
    }

}