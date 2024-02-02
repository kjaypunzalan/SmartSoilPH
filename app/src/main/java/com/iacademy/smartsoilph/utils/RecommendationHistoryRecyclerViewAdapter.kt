package com.iacademy.smartsoilph.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.datamodels.RecommendationData

class RecommendationHistoryRecyclerViewAdapter(
    private val recommendationDataList: ArrayList<RecommendationData>,
    private val onItemClick: (RecommendationData) -> Unit
) : RecyclerView.Adapter<RecommendationHistoryRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvDateTime: TextView = view.findViewById(R.id.tv_date_time)
        private val tvFertilizerAmount: TextView = view.findViewById(R.id.tv_fertilizer_amount)
        private val tvLimeAmount: TextView = view.findViewById(R.id.tv_lime_amount)
        private val tvNpkAmount: TextView = view.findViewById(R.id.tv_npk_amount)

        fun bind(recommendationData: RecommendationData, onItemClick: (RecommendationData) -> Unit) {
            tvDateTime.text = recommendationData.dateOfRecommendation
            tvFertilizerAmount.text = String.format("%.2f kg", recommendationData.fertilizerRecommendation)
            tvLimeAmount.text = String.format("%.2f pounds", recommendationData.limeRecommendation)
            val soilData = recommendationData.soilData
            tvNpkAmount.text = "${soilData.nitrogen}-${soilData.phosphorus}-${soilData.potassium}"
            itemView.setOnClickListener { onItemClick(recommendationData) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recommend_history_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(recommendationDataList[position], onItemClick)
    }

    override fun getItemCount() = recommendationDataList.size
}

