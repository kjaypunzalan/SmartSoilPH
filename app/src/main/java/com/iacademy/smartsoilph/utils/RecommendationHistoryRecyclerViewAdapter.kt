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
        private val tvFertilizer1: TextView = view.findViewById(R.id.tv_fertilizer_amount1)
        private val tvFertilizer2: TextView = view.findViewById(R.id.tv_fertilizer_amount2)
        private val tvFertilizer3: TextView = view.findViewById(R.id.tv_fertilizer_amount3)
        private val tvNpkAmount: TextView = view.findViewById(R.id.tv_npk_amount)

        fun bind(recommendationData: RecommendationData, onItemClick: (RecommendationData) -> Unit) {
            tvDateTime.text = recommendationData.dateOfRecommendation
            tvFertilizer1.text = String.format("%.2f kg", recommendationData.fertilizerRecommendation)

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

