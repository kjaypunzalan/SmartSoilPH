package com.iacademy.smartsoilph.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iacademy.smartsoilph.R
import com.iacademy.smartsoilph.datamodels.RecommendationData

class RecommendationHistoryAdapter(
    private val recommendationDataList: ArrayList<RecommendationData>,
    private val onItemClick: (RecommendationData) -> Unit
) : RecyclerView.Adapter<RecommendationHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvDateTime: TextView = view.findViewById(R.id.tv_date_time)
        private val tvFertilizer1: TextView = view.findViewById(R.id.tv_fertilizer_amount1)
        private val tvFertilizer2: TextView = view.findViewById(R.id.tv_fertilizer_amount2)
        private val tvFertilizer3: TextView = view.findViewById(R.id.tv_fertilizer_amount3)
        private val tvFertilizerName1: TextView = view.findViewById(R.id.tv_fertilizer_name1)
        private val tvFertilizerName2: TextView = view.findViewById(R.id.tv_fertilizer_name2)
        private val tvFertilizerName3: TextView = view.findViewById(R.id.tv_fertilizer_name3)
        private val tvNpkAmount: TextView = view.findViewById(R.id.tv_npk_amount)

        private val tvPHAmount: TextView = view.findViewById(R.id.tv_ph_amount)
        private val tvCrop: TextView = view.findViewById(R.id.tv_selected_crop)
        private val tvSoilTexture: TextView = view.findViewById(R.id.tv_selected_soil)

        fun bind(recommendationData: RecommendationData, onItemClick: (RecommendationData) -> Unit) {
            tvDateTime.text = recommendationData.dateOfRecommendation
            tvCrop.text = recommendationData.soilData.cropType
            tvSoilTexture.text = recommendationData.soilData.soilTexture

            val fertilizerData = recommendationData.requiredFertilizerData
            tvFertilizer1.text = String.format("%.2f kg", fertilizerData.kgFertilizer1)
            tvFertilizer2.text = String.format("%.2f kg", fertilizerData.kgFertilizer2)
            tvFertilizer3.text = String.format("%.2f kg", fertilizerData.kgFertilizer3)

            tvFertilizerName1.text = String.format("%.1f bag of ${fertilizerData.fertilizer1} is recommended", fertilizerData.bagFertilizer1)
            tvFertilizerName2.text = String.format("%.1f bag of ${fertilizerData.fertilizer2} is recommended", fertilizerData.bagFertilizer2)
            tvFertilizerName3.text = String.format("%.1f bag of ${fertilizerData.fertilizer3} is recommended", fertilizerData.bagFertilizer3)

            tvNpkAmount.text = "${fertilizerData.requiredN}-${fertilizerData.requiredP}-${fertilizerData.requiredK}"
            tvPHAmount.text = recommendationData.soilData.phLevel.toString()
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

