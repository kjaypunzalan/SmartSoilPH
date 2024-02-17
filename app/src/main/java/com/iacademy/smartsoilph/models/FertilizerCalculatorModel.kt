package com.iacademy.smartsoilph.models

data class Fertilizer(val name: String, val nitrogen: Float, val phosphorus: Float, val potassium: Float)

class FertilizerCalculatorModel {
    private val fertilizers = mapOf(
        "Complete" to Fertilizer("Complete", 14f, 14f, 14f),
        "Ammonium Phosphate" to Fertilizer("Ammonium Phosphate", 16f, 20f, 0f),
        "Ammonium Sulfate" to Fertilizer("Ammonium Sulfate", 21f, 0f, 0f),
        "Urea" to Fertilizer("Urea", 46f, 0f, 0f),
        "Duophos" to Fertilizer("Duophos", 0f, 22f, 0f),
        "Muriate of Potash" to Fertilizer("Muriate of Potash", 0f, 0f, 60f)
    )

    fun calculateFertilizerRequirements(nRequirement: Float, pRequirement: Float, kRequirement: Float, initialN: Float): Map<String, Float> {
        val result = mutableMapOf<String, Float>()

        // Apply "Complete" if all nutrients have requirements and none are zero (Pattern 1)
        if (nRequirement > 0 && pRequirement > 0 && kRequirement > 0) {
            applyComplete(nRequirement, pRequirement, kRequirement, result)
        } else {
            // Other patterns: Directly apply specific fertilizers
            applySpecificFertilizers(nRequirement, pRequirement, kRequirement, initialN, result)
        }

        return result
    }

    private fun applyComplete(n: Float, p: Float, k: Float, result: MutableMap<String, Float>) {
        val lowestRequirement = minOf(n, p, k)
        val completeAmount = lowestRequirement / (fertilizers["Complete"]!!.phosphorus / 100)
        result["Complete"] = completeAmount

        // Subtract the nutrients provided by "Complete" from the requirements
        subtractNutrientsProvidedByComplete(n, p, k, completeAmount, result)
    }

    private fun subtractNutrientsProvidedByComplete(n: Float, p: Float, k: Float, amount: Float, result: MutableMap<String, Float>) {
        val complete = fertilizers["Complete"]!!
        val nAfterComplete = maxOf(0f, n - (amount * complete.nitrogen / 100))
        val pAfterComplete = maxOf(0f, p - (amount * complete.phosphorus / 100))
        val kAfterComplete = maxOf(0f, k - (amount * complete.potassium / 100))

        // Reapply specific fertilizers for remaining requirements
        applySpecificFertilizers(nAfterComplete, pAfterComplete, kAfterComplete, 0f, result) // Assuming initialN not needed here
    }

    private fun applySpecificFertilizers(n: Float, p: Float, k: Float, initialN: Float, result: MutableMap<String, Float>) {
        if (n > 0) chooseFertilizerForNutrient(n, "N", initialN, result)
        if (p > 0) chooseFertilizerForNutrient(p, "P", 0f, result) // initialN not relevant for P and K
        if (k > 0) chooseFertilizerForNutrient(k, "K", 0f, result)
    }

    private fun chooseFertilizerForNutrient(requirement: Float, type: String, initialN: Float, result: MutableMap<String, Float>) {
        when (type) {
            "N" -> {
                // Choose Urea or Ammonium Sulfate based on initialN level
                val fertilizerName = if (initialN <= 6.6) "Urea" else "Ammonium Sulfate"
                val fertilizer = fertilizers[fertilizerName]!!
                val amountNeeded = requirement / (fertilizer.nitrogen / 100)
                result[fertilizerName] = (result[fertilizerName] ?: 0f) + amountNeeded
            }
            "P" -> {
                // Duophos for P, unless Ammonium Phosphate was already chosen for N
                if (!result.containsKey("Ammonium Phosphate")) {
                    val fertilizer = fertilizers["Duophos"]!!
                    val amountNeeded = requirement / (fertilizer.phosphorus / 100)
                    result["Duophos"] = (result["Duophos"] ?: 0f) + amountNeeded
                }
            }
            "K" -> {
                // Muriate of Potash for K
                val fertilizer = fertilizers["Muriate of Potash"]!!
                val amountNeeded = requirement / (fertilizer.potassium / 100)
                result["Muriate of Potash"] = (result["Muriate of Potash"] ?: 0f) + amountNeeded
            }
        }

        // Handle the 1-1-0 pattern specifically where Ammonium Phosphate can cover both N and P
        if (type == "N" || type == "P") {
            val nRequirement = result["Urea"] ?: result["Ammonium Sulfate"] ?: 0f
            val pRequirement = result["Duophos"] ?: 0f
            if (nRequirement > 0 && pRequirement > 0) {
                // Check if Ammonium Phosphate is a better option
                val amp = fertilizers["Ammonium Phosphate"]!!
                val nCoveredByAmp = nRequirement * (amp.nitrogen / 100)
                val pCoveredByAmp = pRequirement * (amp.phosphorus / 100)
                if (nCoveredByAmp >= nRequirement && pCoveredByAmp >= pRequirement) {
                    // Use Ammonium Phosphate instead of separate N and P fertilizers
                    result.clear() // Clearing previous entries
                    result["Ammonium Phosphate"] = maxOf(nRequirement / (amp.nitrogen / 100), pRequirement / (amp.phosphorus / 100))
                }
            }
        }
    }

}
