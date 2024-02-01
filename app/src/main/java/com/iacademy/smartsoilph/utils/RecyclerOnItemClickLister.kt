package com.iacademy.smartsoilph.utils

import com.iacademy.smartsoilph.datamodels.RecommendationData

interface RecyclerOnItemClickListener {
    fun onItemClick(recommendationData: RecommendationData)
}