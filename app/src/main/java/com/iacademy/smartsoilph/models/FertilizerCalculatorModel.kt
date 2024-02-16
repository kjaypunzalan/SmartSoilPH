package com.iacademy.smartsoilph.models

import kotlin.math.max

data class Fertilizer(val name: String, val nitrogen: Float, val phosphorus: Float, val potassium: Float)

class FertilizerCalculatorModel {
    private val fertilizers = mapOf(
        "Duophos" to Fertilizer("Duophos", 0f, 22f, 0f),
        "Urea" to Fertilizer("Urea", 46f, 0f, 0f),
        "Complete" to Fertilizer("Complete", 14f, 14f, 14f),
        "Ammonium Phosphate" to Fertilizer("Ammonium Phosphate", 16f, 20f, 0f),
        "Ammonium Sulfate" to Fertilizer("Ammonium Sulfate", 21f, 0f, 0f),
        "Muriate of Potash" to Fertilizer("Muriate of Potash", 0f, 0f, 60f)
    )

    fun calculateFertilizerRequirements(
        nRequirement: Float,
        pRequirement: Float,
        kRequirement: Float,
        initialN: Float
    ): Map<String, Float> {
        val result = mutableMapOf<String, Float>()

        // Apply "Complete" if all nutrients have requirements and none are zero
        if (nRequirement > 0 && pRequirement > 0 && kRequirement > 0) {
            applyComplete(nRequirement, pRequirement, kRequirement, result)
        }

        // Specific logic for Ammonium Phosphate when both N and P have requirements but K does not
        if (nRequirement > 0 && pRequirement > 0 && kRequirement == 0f) {
            val ammoniumPhosphate = fertilizers["Ammonium Phosphate"]!!
            val nRatio = nRequirement / ammoniumPhosphate.nitrogen
            val pRatio = pRequirement / ammoniumPhosphate.phosphorus
            val amountNeeded = maxOf(nRatio, pRatio) * 100 // Find the maximum amount needed to satisfy both
            result[ammoniumPhosphate.name] = amountNeeded
            // Subtract what's applied from requirements
            val nApplied = amountNeeded * ammoniumPhosphate.nitrogen / 100
            val pApplied = amountNeeded * ammoniumPhosphate.phosphorus / 100
            val nRemaining = maxOf(0f, nRequirement - nApplied)
            val pRemaining = maxOf(0f, pRequirement - pApplied)
            // Apply Urea or Ammonium Sulfate if N is still required
            if (nRemaining > 0) {
                applyNitrogen(nRemaining, initialN, result)
            }
            return result
        }

        // Direct application for N, P, K if not using "Complete" or "Ammonium Phosphate"
        applyDirectFertilizers(nRequirement, pRequirement, kRequirement, initialN, result)

        return result
    }

    private fun applyComplete(n: Float, p: Float, k: Float, result: MutableMap<String, Float>) {
        val complete = fertilizers["Complete"]!!
        val amounts = calculateAmountsForComplete(n, p, k, complete)
        result.putAll(amounts)
    }

    private fun applyDirectFertilizers(n: Float, p: Float, k: Float, initialN: Float, result: MutableMap<String, Float>) {
        if (n > 0) applyNitrogen(n, initialN, result)
        if (p > 0) applyPhosphorus(p, result)
        if (k > 0) applyPotassium(k, result)
    }

    private fun applyNitrogen(n: Float, initialN: Float, result: MutableMap<String, Float>) {
        val fertilizerName = if (initialN <= 6.6f) "Urea" else "Ammonium Sulfate"
        val fertilizer = fertilizers[fertilizerName]!!
        val amount = n / (fertilizer.nitrogen / 100)
        result[fertilizerName] = amount
    }

    private fun applyPhosphorus(p: Float, result: MutableMap<String, Float>) {
        val duophos = fertilizers["Duophos"]!!
        val amount = p / (duophos.phosphorus / 100)
        result["Duophos"] = amount
    }

    private fun applyPotassium(k: Float, result: MutableMap<String, Float>) {
        val mop = fertilizers["Muriate of Potash"]!!
        val amount = k / (mop.potassium / 100)
        result["Muriate of Potash"] = amount
    }

    // This method is a placeholder for the detailed logic to calculate the amounts when "Complete" is applied
    private fun calculateAmountsForComplete(n: Float, p: Float, k: Float, complete: Fertilizer): Map<String, Float> {
        val lowestRequirement = minOf(n / complete.nitrogen, p / complete.phosphorus, k / complete.potassium)
        val amountOfCompleteNeeded = lowestRequirement * 100 // Assuming the complete fertilizer percentages are based on 100kg basis
        val nSuppliedByComplete = amountOfCompleteNeeded * complete.nitrogen / 100
        val pSuppliedByComplete = amountOfCompleteNeeded * complete.phosphorus / 100
        val kSuppliedByComplete = amountOfCompleteNeeded * complete.potassium / 100

        // Calculate the remaining requirements after applying the complete fertilizer
        val remainingN = max(0f, n - nSuppliedByComplete)
        val remainingP = max(0f, p - pSuppliedByComplete)
        val remainingK = max(0f, k - kSuppliedByComplete)

        // Add the complete fertilizer amount to the result map
        val result = mutableMapOf("Complete" to amountOfCompleteNeeded)

        // If there are remaining requirements, these should be handled by the logic that calls this function
        return result
    }

}
