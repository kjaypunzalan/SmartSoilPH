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

object RiceRanges {
    val nitrogenRanges = listOf(0.0f..2.0f, 2.1f..3.5f, 3.6f..4.4f, 4.5f..5.4f, 5.5f..Float.MAX_VALUE)
    val phosphorusRanges = listOf(0f..6f, 7f..10f, 11f..15f, 16f..21f, 21f..Float.MAX_VALUE)
    val potassiumRanges = listOf(0f..60f, 61f..90f, 91f..114f, 115f..154f, 155f..Float.MAX_VALUE)

    // Specific labels for each nutrient type
    val nitrogenLabels = listOf("Low", "Moderately Low", "Moderately High", "High", "Very High")
    val phosphorusLabels = listOf("Low", "Moderately Low", "Moderately High", "High", "Very High")
    val potassiumLabels = listOf("Deficient", "Sufficient", "Sufficient+", "Sufficient++", "Sufficient++/+++")
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
        "Beans - Baguio" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(45, 40, 35, 30), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Beans - Batao" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(45, 40, 35, 30), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Beans - Lima (Patani)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(45, 40, 35, 30), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Beans - String (Pole Sitao)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(50, 40, 35, 30), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(90, 60, 30, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Beans - Tree (Type)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(60, 40, 30, 0), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(30, 20, 20, 10, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(45, 30, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Beans - Winged (Segiduillas)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(45, 40, 35, 30), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), NutrientRanges.phosphorusLabels),
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
        "Cabbage (Head)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(150, 140, 120, 100), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Cabbage (Local)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(150, 140, 120, 100), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(75, 60, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Carrot" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(90, 80, 70, 60), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(90, 70, 50, 30, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(120, 75, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Cauliflower" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(150, 140, 120, 100), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Celery" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(60, 50, 40, 30), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(90, 60, 40, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Chayote" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(60, 50, 40, 30), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(80, 60, 40, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Chili (Bell Pepper)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(90, 80, 70, 60), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(80, 60, 40, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(150, 75, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Chili (Green) (Sinigang)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(120, 90, 60, 30), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(80, 60, 40, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(150, 120, 90, 30), NutrientRanges.potassiumLabels)
        ),
        "Chili (Pepper)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(90, 60, 30, 0), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(80, 60, 40, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(150, 120, 90, 30), NutrientRanges.potassiumLabels)
        ),
        "Condiments (Mint Herb)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(80, 40, 20, 0), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(80, 40, 20, 0, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(20, 10, 5, 0), NutrientRanges.potassiumLabels)
        ),
        "Cucumber" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(100, 80, 60, 30), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(100, 75, 50, 30, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Eggplant" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(120, 100, 80, 60), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(90, 70, 60, 40, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(90, 60, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Garlic/Onion" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(120, 80, 40, 20), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(80, 60, 40, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(150, 120, 90, 45), NutrientRanges.potassiumLabels)
        ),
        "Ginger (Improved)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(80, 70, 60, 60), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(120, 75, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Ginger (Local)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(40, 30, 20, 0), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(120, 75, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Lettuce (Head)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(150, 140, 120, 100), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Mustard" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(150, 140, 120, 100), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(75, 60, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Okra (Hybrid)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(120, 100, 70, 60), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(80, 60, 40, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(90, 60, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Okra (Local)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(90, 80, 80, 60), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(80, 60, 40, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(90, 60, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Parsnip" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(90, 60, 45, 30), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Patola" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(90, 60, 30, 30), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(80, 60, 40, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Peas" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(20, 10, 0, 0), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(40, 30, 40, 10, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(45, 30, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Pechay" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(150, 140, 120, 100), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(75, 60, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Potato" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(80, 60, 70, 40), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(90, 60, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Radish/Turnips" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(90, 80, 70, 60), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(90, 70, 50, 30, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(120, 75, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Squash" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(90, 50, 40, 30), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(80, 60, 40, 30, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), NutrientRanges.potassiumLabels)
        ),
        "Tomatoes" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(90, 80, 70, 60), NutrientRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(120, 90, 60, 30, 0), NutrientRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(90, 60, 30, 0), NutrientRanges.potassiumLabels)
        ),

        /****************************
         * RICE CROPS
         *------------------------*/
        "Rice: Inbred Light Soils (Wet Season)" to CropNutrientRequirements(
            nitrogenRequirements = zipRiceNitrogenRequirements(listOf(10, 80, 60, 40, 23), RiceRanges.nitrogenLabels),
            phosphorusRequirements = zipRicePhosphorusRequirements(listOf(60, 40, 20, 7, 0), RiceRanges.phosphorusLabels),
            potassiumRequirements = zipRicePotassiumRequirements(listOf(60, 40, 20, 7, 0), RiceRanges.potassiumLabels)
        ),
        "Rice: Inbred Light Soils (Dry Season)" to CropNutrientRequirements(
            nitrogenRequirements = zipRiceNitrogenRequirements(listOf(120, 100, 80, 60, 0), RiceRanges.nitrogenLabels),
            phosphorusRequirements = zipRicePhosphorusRequirements(listOf(60, 40, 20, 7, 0), RiceRanges.phosphorusLabels),
            potassiumRequirements = zipRicePotassiumRequirements(listOf(60, 40, 20, 7, 0), RiceRanges.potassiumLabels)
        ),
        "Rice: Inbred Med-Heavy Soils (Wet Season)" to CropNutrientRequirements(
            nitrogenRequirements = zipRiceNitrogenRequirements(listOf(80, 70, 50, 30, 23), RiceRanges.nitrogenLabels),
            phosphorusRequirements = zipRicePhosphorusRequirements(listOf(60, 40, 20, 7, 0), RiceRanges.phosphorusLabels),
            potassiumRequirements = zipRicePotassiumRequirements(listOf(60, 40, 20, 7, 0), RiceRanges.potassiumLabels)
        ),
        "Rice: Inbred Med-Heavy Soils (Dry Season)" to CropNutrientRequirements(
            nitrogenRequirements = zipRiceNitrogenRequirements(listOf(100, 80, 60, 40, 0), RiceRanges.nitrogenLabels),
            phosphorusRequirements = zipRicePhosphorusRequirements(listOf(60, 40, 20, 7, 0), RiceRanges.phosphorusLabels),
            potassiumRequirements = zipRicePotassiumRequirements(listOf(60, 40, 20, 7, 0), RiceRanges.potassiumLabels)
        ),
        "Rice: Hybrid Light Soils (Wet Season)" to CropNutrientRequirements(
            nitrogenRequirements = zipRiceNitrogenRequirements(listOf(120, 100, 80, 60, 23), RiceRanges.nitrogenLabels),
            phosphorusRequirements = zipRicePhosphorusRequirements(listOf(70, 50, 30, 7, 0), RiceRanges.phosphorusLabels),
            potassiumRequirements = zipRicePotassiumRequirements(listOf(0, 0, 0, 0, 0), RiceRanges.potassiumLabels)
        ),
        "Rice: Hybrid Light Soils (Dry Season)" to CropNutrientRequirements(
            nitrogenRequirements = zipRiceNitrogenRequirements(listOf(140, 120, 100, 80, 0), RiceRanges.nitrogenLabels),
            phosphorusRequirements = zipRicePhosphorusRequirements(listOf(70, 50, 30, 7, 0), RiceRanges.phosphorusLabels),
            potassiumRequirements = zipRicePotassiumRequirements(listOf(70, 50, 30, 7, 0), RiceRanges.potassiumLabels)
        ),
        "Rice: Hybrid Med-Heavy Soils (Wet Season)" to CropNutrientRequirements(
            nitrogenRequirements = zipRiceNitrogenRequirements(listOf(110, 90, 70, 50, 23), RiceRanges.nitrogenLabels),
            phosphorusRequirements = zipRicePhosphorusRequirements(listOf(70, 50, 30, 7, 0), RiceRanges.phosphorusLabels),
            potassiumRequirements = zipRicePotassiumRequirements(listOf(70, 50, 30, 7, 0), RiceRanges.potassiumLabels)
        ),
        "Rice: Hybrid Med-Heavy Soils (Dry Season)" to CropNutrientRequirements(
            nitrogenRequirements = zipRiceNitrogenRequirements(listOf(120, 90, 70, 50, 23), RiceRanges.nitrogenLabels),
            phosphorusRequirements = zipRicePhosphorusRequirements(listOf(70, 50, 30, 7, 0), RiceRanges.phosphorusLabels),
            potassiumRequirements = zipRicePotassiumRequirements(listOf(70, 50, 30, 7, 0), RiceRanges.potassiumLabels)
        ),
        "Rice: Upland (Light Soils)" to CropNutrientRequirements(
            nitrogenRequirements = zipRiceNitrogenRequirements(listOf(45, 40, 35, 30, 0), RiceRanges.nitrogenLabels),
            phosphorusRequirements = zipRicePhosphorusRequirements(listOf(60, 40, 30, 20, 0), RiceRanges.phosphorusLabels),
            potassiumRequirements = zipRicePotassiumRequirements(listOf(60, 45, 30, 0, 0), RiceRanges.potassiumLabels)
        ),
        "Rice: Upland (Med-Heavy Soils)" to CropNutrientRequirements(
            nitrogenRequirements = zipRiceNitrogenRequirements(listOf(45, 40, 35, 30, 0), RiceRanges.nitrogenLabels),
            phosphorusRequirements = zipRicePhosphorusRequirements(listOf(60, 40, 30, 20, 0), RiceRanges.phosphorusLabels),
            potassiumRequirements = zipRicePotassiumRequirements(listOf(60, 45, 30, 0, 0), RiceRanges.potassiumLabels)
        ),

        /****************************
         * CORN CROPS
         *------------------------*/
        "Hybrid" to CropNutrientRequirements(
            nitrogenRequirements = zipRiceNitrogenRequirements(listOf(120, 100, 80, 60, 0), RiceRanges.nitrogenLabels),
            phosphorusRequirements = zipRicePhosphorusRequirements(listOf(60, 40, 20, 7, 0), RiceRanges.phosphorusLabels),
            potassiumRequirements = zipRicePotassiumRequirements(listOf(60, 45, 30, 7, 0), RiceRanges.potassiumLabels)
        ),
        "OPV (High Yielding Variety)" to CropNutrientRequirements(
            nitrogenRequirements = zipRiceNitrogenRequirements(listOf(80, 60, 40, 20, 0), RiceRanges.nitrogenLabels),
            phosphorusRequirements = zipRicePhosphorusRequirements(listOf(60, 40, 20, 7, 0), RiceRanges.phosphorusLabels),
            potassiumRequirements = zipRicePotassiumRequirements(listOf(60, 45, 30, 7, 0), RiceRanges.potassiumLabels)
        )
    )
    // Utility function to zip the nutrient requirements with their labels
    private fun zipNitrogenRequirements(requirements: List<Int>, labels: List<String>) =
        NutrientRanges.nitrogenRanges.zip(requirements.zip(labels)).toMap()
    private fun zipPhosphorusRequirements(requirements: List<Int>, labels: List<String>) =
        NutrientRanges.phosphorusRanges.zip(requirements.zip(labels)).toMap()
    private fun zipPotassiumRequirements(requirements: List<Int>, labels: List<String>) =
        NutrientRanges.potassiumRanges.zip(requirements.zip(labels)).toMap()

    // Utility function to zip Rice and Corn nutrient requirements with their labels
    private fun zipRiceNitrogenRequirements(requirements: List<Int>, labels: List<String>) =
        RiceRanges.nitrogenRanges.zip(requirements.zip(labels)).toMap()
    private fun zipRicePhosphorusRequirements(requirements: List<Int>, labels: List<String>) =
        RiceRanges.phosphorusRanges.zip(requirements.zip(labels)).toMap()
    private fun zipRicePotassiumRequirements(requirements: List<Int>, labels: List<String>) =
        RiceRanges.potassiumRanges.zip(requirements.zip(labels)).toMap()


    fun getNutrientRequirements(crop: String): CropNutrientRequirements? = cropRequirements[crop]

}

