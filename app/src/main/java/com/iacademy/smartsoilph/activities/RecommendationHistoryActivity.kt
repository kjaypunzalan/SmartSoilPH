package com.iacademy.smartsoilph.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.datamodels.RecommendationData
import com.iacademy.smartsoilph.models.FirebaseModel
import com.iacademy.smartsoilph.utils.RecyclerOnItemClickListener
import com.iacademy.smartsoilph.utils.RecommendationHistoryRecyclerViewAdapter
import java.util.ArrayList

class RecommendationHistoryActivity : BaseActivity(), RecyclerOnItemClickListener {

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
            val adapter = RecommendationHistoryRecyclerViewAdapter(ArrayList(recommendationDataList)) { recommendationData ->
                // Handle the item click event here
                // Example: show a Toast or navigate to a detail screen
                }
            rvRecommendationHistory.adapter = adapter
            rvRecommendationHistory.layoutManager = LinearLayoutManager(this)
        }

        //fab when pressed shifts view to top
        val fabScrollToTop: FloatingActionButton = findViewById(R.id.fab_scroll_to_top)
        fabScrollToTop.setOnClickListener {
            // Scroll to the top of the RecyclerView
            rvRecommendationHistory.scrollToPosition(0)
        }

        //shows fab when user scrolls downs & hides when view is at top
        rvRecommendationHistory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    // User is scrolling down
                    fabScrollToTop.show()
                } else if (dy < 0) {
                    // User is scrolling up
                    fabScrollToTop.hide()
                }
            }
        })

        initializeReturnButton()

    }
    private fun initializeReturnButton() {
        val btnReturn: ImageView = findViewById(R.id.toolbar_back_icon)
        btnReturn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }


    override fun onItemClick(recommendationData: RecommendationData) {
        // Handle item click event
        // Implement what should happen when an item is clicked
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

}