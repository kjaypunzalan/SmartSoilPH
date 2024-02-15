package com.iacademy.smartsoilph.datamodels

// Define nutrient requirement ranges as constants
object NutrientRanges {
    val nitrogenRanges = listOf(0.0f..2.0f, 2.1f..3.5f, 3.6f..4.4f, 4.5f..Float.MAX_VALUE)
    val phosphorusRanges = listOf(0f..6f, 7f..10f, 11f..15f, 16f..21f, 21f..Float.MAX_VALUE)
    val potassiumRanges = listOf(0f..75f, 76f..113f, 114f..150f, 151f..Float.MAX_VALUE)

    // Specific labels for each nutrient type
    val nitrogenLabels = listOf("Low", "Medium", "High", "Very High")
    val phosphorusLabels = listOf("Low", "Moderately Low", "Moderately High", "High", "Very High")
    val potassiumLabels = listOf("Low", "Sufficient", "Sufficient++", "Sufficient++/+++")
}

// Define a class to hold the nutrient requirements for each crop
data class CropNutrientRequirements(
    val nitrogenRequirements: Map<ClosedFloatingPointRange<Float>, Pair<Int, String>>,
    val phosphorusRequirements: Map<ClosedFloatingPointRange<Float>, Pair<Int, String>>,
    val potassiumRequirements: Map<ClosedFloatingPointRange<Float>, Pair<Int, String>>
)

// A model to map each crop to its nutrient requirements
class FertilizerNutrientModel {
    private val cropRequirements: Map<String, CropNutrientRequirements> = mapOf(
        "Eggplant" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(120, 100, 80, 60), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(90, 70, 60, 40, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(90, 60, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Ampalaya" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(90, 50, 40, 30), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(80, 60, 40, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Asparagus" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(90, 60, 45, 30), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(40, 30, 20, 10, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Broccoli" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(60, 50, 40, 30), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Cabbage (Chinese)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(150, 140, 120, 100), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(75, 60, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Cabbage (Local)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(150, 140, 120, 100), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(75, 60, 30, 0), NutrientRanges.potassiumLabels)
        )
    )

    // Utility function to zip the nutrient requirements with their labels
    private fun zipNitrogenRequirements(requirements: List<Int>, labels: List<String>) =
        NutrientRanges.nitrogenRanges.zip(requirements.zip(labels)).toMap()
    private fun zipPhosphorusRequirements(requirements: List<Int>, labels: List<String>) =
        NutrientRanges.phosphorusRanges.zip(requirements.zip(labels)).toMap()
    private fun zipPotassiumRequirements(requirements: List<Int>, labels: List<String>) =
        NutrientRanges.potassiumRanges.zip(requirements.zip(labels)).toMap()

    fun getNutrientRequirements(crop: String): CropNutrientRequirements? = cropRequirements[crop]

}

