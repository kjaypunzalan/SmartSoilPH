package com.iacademy.smartsoilph.models

// Define nutrient requirement ranges as constants
object VegetableRanges {
    val nitrogenRanges = listOf(0.00f..2.09f, 2.10f..3.59f, 3.60f..4.50f, 4.51f..Float.MAX_VALUE)
    val phosphorusRanges = listOf(0.00f..6.99f, 7.00f..10.99f, 11.00f..15.99f, 16.00f..20.99f, 21.00f..Float.MAX_VALUE)
    val potassiumRanges = listOf(0.00f..75.99f, 76.00f..113.99f, 114.00f..150.99f, 151.00f..Float.MAX_VALUE)

    // Specific labels for each nutrient type
    val nitrogenLabels = listOf("Low", "Medium", "High", "Very High")
    val phosphorusLabels = listOf("Low", "Moderately Low", "Moderately High", "High", "Very High")
    val potassiumLabels = listOf("Low", "Sufficient", "Sufficient++", "Sufficient++/+++")
}

object RiceRanges {
    val nitrogenRanges = listOf(0.00f..2.00f, 2.01f..3.59f, 3.60f..4.59f, 4.60f..5.59f, 5.60f..Float.MAX_VALUE)
    val phosphorusRanges = listOf(0.00f..2.00f, 2.01f..6.00f, 6.01f..10.00f, 10.01f..15.00f, 15.01f..Float.MAX_VALUE)
    val potassiumRanges = listOf(0.00f..25.00f, 25.01f..50.99f, 51.00f..75.99f, 76.01f..100.00f, 100.01f..Float.MAX_VALUE)

    // Specific labels for each nutrient type
    val nitrogenLabels = listOf("Low", "Moderately Low", "Moderately High", "High", "Very High")
    val phosphorusLabels = listOf("Low", "Moderately Low", "Moderately High", "High", "Very High")
    val potassiumLabels = listOf("Deficient", "Sufficient", "Sufficient+", "Sufficient++", "Sufficient++/+++")
}

object CornRanges {
    val nitrogenRanges = listOf(0.00f..2.00f, 2.01f..3.59f, 3.60f..4.59f, 4.60f..5.59f, 5.60f..Float.MAX_VALUE)
    val phosphorusRanges = listOf(0.00f..2.00f, 2.01f..6.00f, 6.01f..10.00f, 10.01f..15.00f, 15.01f..Float.MAX_VALUE)
    val potassiumRanges = listOf(0.00f..25.00f, 25.01f..50.99f, 51.00f..75.99f, 76.01f..100.00f, 100.01f..Float.MAX_VALUE)

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
            nitrogenRequirements = zipNitrogenRequirements(listOf(90, 50, 40, 30), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(80, 60, 40, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Asparagus" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(90, 60, 45, 30), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(40, 30, 20, 10, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Beans - Baguio" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(45, 40, 35, 30), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Beans - Batao" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(45, 40, 35, 30), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Beans - Lima (Patani)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(45, 40, 35, 30), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Beans - String (Pole Sitao)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(50, 40, 35, 30), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(90, 60, 30, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Beans - Tree (Type)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(60, 40, 30, 0), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(30, 20, 20, 10, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(45, 30, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Beans - Winged (Segiduillas)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(45, 40, 35, 30), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Broccoli" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(60, 50, 40, 30), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Cabbage (Chinese)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(150, 140, 120, 100), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(75, 60, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Cabbage (Head)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(150, 140, 120, 100), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Cabbage (Local)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(150, 140, 120, 100), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(75, 60, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Carrot" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(90, 80, 70, 60), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(90, 70, 50, 30, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(120, 75, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Cauliflower" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(150, 140, 120, 100), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Celery" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(60, 50, 40, 30), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(90, 60, 40, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Chayote" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(60, 50, 40, 30), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(80, 60, 40, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Chili (Bell Pepper)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(90, 80, 70, 60), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(80, 60, 40, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(150, 75, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Chili (Green) (Sinigang)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(120, 90, 60, 30), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(80, 60, 40, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(150, 120, 90, 30), VegetableRanges.potassiumLabels)
        ),
        "Chili (Pepper)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(90, 60, 30, 0), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(80, 60, 40, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(150, 120, 90, 30), VegetableRanges.potassiumLabels)
        ),
        "Condiments (Mint Herb)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(80, 40, 20, 0), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(80, 40, 20, 0, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(20, 10, 5, 0), VegetableRanges.potassiumLabels)
        ),
        "Cucumber" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(100, 80, 60, 30), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(100, 75, 50, 30, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Eggplant" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(120, 100, 80, 60), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(90, 70, 60, 40, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(90, 60, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Garlic/Onion" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(120, 80, 40, 20), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(80, 60, 40, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(150, 120, 90, 45), VegetableRanges.potassiumLabels)
        ),
        "Ginger (Improved)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(80, 70, 60, 60), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(120, 75, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Ginger (Local)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(40, 30, 20, 0), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(120, 75, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Lettuce (Head)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(150, 140, 120, 100), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Mustard" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(150, 140, 120, 100), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(75, 60, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Okra (Hybrid)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(120, 100, 70, 60), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(80, 60, 40, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(90, 60, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Okra (Local)" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(90, 80, 80, 60), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(80, 60, 40, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(90, 60, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Parsnip" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(90, 60, 45, 30), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Patola" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(90, 60, 30, 30), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(80, 60, 40, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Peas" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(20, 10, 0, 0), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(40, 30, 40, 10, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(45, 30, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Pechay" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(150, 140, 120, 100), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(75, 60, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Potato" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(80, 60, 70, 40), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(60, 40, 30, 20, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(90, 60, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Radish/Turnips" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(90, 80, 70, 60), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(90, 70, 50, 30, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(120, 75, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Squash" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(90, 50, 40, 30), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(80, 60, 40, 30, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(60, 45, 30, 0), VegetableRanges.potassiumLabels)
        ),
        "Tomatoes" to CropNutrientRequirements(
            nitrogenRequirements = zipNitrogenRequirements(listOf(90, 80, 70, 60), VegetableRanges.nitrogenLabels),
            phosphorusRequirements = zipPhosphorusRequirements(listOf(120, 90, 60, 30, 0), VegetableRanges.phosphorusLabels),
            potassiumRequirements = zipPotassiumRequirements(listOf(90, 60, 30, 0), VegetableRanges.potassiumLabels)
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

        /**************************
         * CORN CROPS
         *------------------------*/
        "Corn: Hybrid" to CropNutrientRequirements(
            nitrogenRequirements = zipCornNitrogenRequirements(listOf(120, 100, 80, 60, 60), CornRanges.nitrogenLabels),
            phosphorusRequirements = zipCornPhosphorusRequirements(listOf(60, 50, 40, 7, 0), CornRanges.phosphorusLabels),
            potassiumRequirements = zipCornPotassiumRequirements(listOf(60, 45, 30, 7, 0), CornRanges.potassiumLabels)
        ),
        "Corn: OPV (High Yielding Variety)" to CropNutrientRequirements(
            nitrogenRequirements = zipCornNitrogenRequirements(listOf(80, 60, 40, 20, 20), CornRanges.nitrogenLabels),
            phosphorusRequirements = zipCornPhosphorusRequirements(listOf(60, 50, 40, 7, 0), CornRanges.phosphorusLabels),
            potassiumRequirements = zipCornPotassiumRequirements(listOf(60, 45, 30, 7, 0), CornRanges.potassiumLabels)
        )
    )
    // Utility function to zip the nutrient requirements with their labels
    private fun zipNitrogenRequirements(requirements: List<Int>, labels: List<String>) =
        VegetableRanges.nitrogenRanges.zip(requirements.zip(labels)).toMap()
    private fun zipPhosphorusRequirements(requirements: List<Int>, labels: List<String>) =
        VegetableRanges.phosphorusRanges.zip(requirements.zip(labels)).toMap()
    private fun zipPotassiumRequirements(requirements: List<Int>, labels: List<String>) =
        VegetableRanges.potassiumRanges.zip(requirements.zip(labels)).toMap()

    // Utility function to zip Rice and Corn nutrient requirements with their labels
    private fun zipRiceNitrogenRequirements(requirements: List<Int>, labels: List<String>) =
        RiceRanges.nitrogenRanges.zip(requirements.zip(labels)).toMap()
    private fun zipRicePhosphorusRequirements(requirements: List<Int>, labels: List<String>) =
        RiceRanges.phosphorusRanges.zip(requirements.zip(labels)).toMap()
    private fun zipRicePotassiumRequirements(requirements: List<Int>, labels: List<String>) =
        RiceRanges.potassiumRanges.zip(requirements.zip(labels)).toMap()

    private fun zipCornNitrogenRequirements(requirements: List<Int>, labels: List<String>) =
        CornRanges.nitrogenRanges.zip(requirements.zip(labels)).toMap()
    private fun zipCornPhosphorusRequirements(requirements: List<Int>, labels: List<String>) =
        CornRanges.phosphorusRanges.zip(requirements.zip(labels)).toMap()
    private fun zipCornPotassiumRequirements(requirements: List<Int>, labels: List<String>) =
        CornRanges.potassiumRanges.zip(requirements.zip(labels)).toMap()


    fun getNutrientRequirements(crop: String): CropNutrientRequirements? = cropRequirements[crop]

}

