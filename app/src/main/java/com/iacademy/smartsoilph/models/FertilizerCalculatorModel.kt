package com.iacademy.smartsoilph.models

import android.util.Log
import com.iacademy.smartsoilph.datamodels.RequiredFertilizerData


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

    fun calculateFertilizerRequirements(nRequirement: Float, pRequirement: Float, kRequirement: Float, initialN: Float) : RequiredFertilizerData {
        val datamodel = RequiredFertilizerData()

        // Apply "Complete" if all nutrients have requirements and none are zero (Pattern 1)
        if (nRequirement > 0 && pRequirement > 0 && kRequirement > 0) {
            Log.d("NPK", "$nRequirement $pRequirement $kRequirement")
            return applyComplete(datamodel, nRequirement, pRequirement, kRequirement, initialN)
        } else {
            // Other patterns: Directly apply specific fertilizers
            return applySpecificFertilizers(datamodel, nRequirement, pRequirement, kRequirement, initialN)
        }
    }

    private fun applyComplete(datamodel: RequiredFertilizerData, n: Float, p: Float, k: Float, initialN: Float) : RequiredFertilizerData {
        Log.d("NPK", "$n $p $k")
        val lowestRequirement = minOf(n, p, k)
        Log.d("Lowest", "$lowestRequirement")
        val completeAmount = lowestRequirement / (fertilizers["Complete"]!!.phosphorus / 100)
        Log.d("Complete", "$completeAmount")

        // Subtract the nutrients provided by "Complete" from the requirements
        return subtractNutrientsProvidedByComplete(assignValue(datamodel, "Complete", completeAmount), n, p, k, initialN, completeAmount)
    }

    private fun subtractNutrientsProvidedByComplete(datamodel: RequiredFertilizerData, n: Float, p: Float, k: Float, initialN: Float, amount: Float) : RequiredFertilizerData {
        val complete = fertilizers["Complete"]!!
        val nAfterComplete = maxOf(0f, n - (amount * complete.nitrogen / 100))
        val pAfterComplete = maxOf(0f, p - (amount * complete.phosphorus / 100))
        val kAfterComplete = maxOf(0f, k - (amount * complete.potassium / 100))

        // Reapply specific fertilizers for remaining requirements
        return applySpecificFertilizers(datamodel, nAfterComplete, pAfterComplete, kAfterComplete, initialN) // Assuming initialN not needed here
    }

    private fun applySpecificFertilizers(datamodel: RequiredFertilizerData, n: Float, p: Float, k: Float, initialN: Float) : RequiredFertilizerData {
        if (n > 0 && p > 0 && k==0f) {
            chooseFertilizerForNutrient(datamodel, n,p,k, "NP", initialN)
        }
        else {
            if (n > 0) chooseFertilizerForNutrient(datamodel, n,p,k, "N", initialN)
            if (p > 0) chooseFertilizerForNutrient(datamodel, n,p,k, "P", initialN)
            if (k > 0) chooseFertilizerForNutrient(datamodel, n,p,k, "K", initialN)
        }

        return datamodel
    }

    private fun chooseFertilizerForNutrient(datamodel: RequiredFertilizerData,
                                            nRequirement: Float, pRequirement: Float, kRequirement: Float,
                                            type: String, initialN: Float) : RequiredFertilizerData{
        when (type) {
            "N" -> {
                // Choose Urea or Ammonium Sulfate based on initialN level
                val fertilizerName = if (initialN <= 6.6) "Urea" else "Ammonium Sulfate"
                val fertilizer = fertilizers[fertilizerName]!!
                val amountNeeded = nRequirement / (fertilizer.nitrogen / 100)
                //result[fertilizerName] = (result[fertilizerName] ?: 0f) + amountNeeded //TODO: Remove
                return assignValue(datamodel, fertilizerName, amountNeeded)
            }
            "P" -> {
                // Duophos for P, unless Ammonium Phosphate was already chosen for N
                if (!datamodel.fertilizer1.contentEquals("Ammonium Phosphate")
                    || !datamodel.fertilizer2.contentEquals("Ammonium Phosphate")) {
                    val fertilizer = fertilizers["Duophos"]!!
                    val amountNeeded = pRequirement / (fertilizer.phosphorus / 100)
                    //result["Duophos"] = (result["Duophos"] ?: 0f) + amountNeeded //TODO: Remove
                    assignValue(datamodel, "Duophos", amountNeeded)
                }
            }
            "K" -> {
                // Muriate of Potash for K
                val fertilizer = fertilizers["Muriate of Potash"]!!
                val amountNeeded = kRequirement / (fertilizer.potassium / 100)
                //result["Muriate of Potash"] = (result["Muriate of Potash"] ?: 0f) + amountNeeded //TODO: Remove
                assignValue(datamodel, "Muriate of Potash", amountNeeded)
            }
            "NP" -> {
                //TODO: CALCULATION FOR AMMONIUM SULFATE
                // Handle the 1-1-0 pattern specifically where Ammonium Phosphate can cover both N and P
                // Check if Ammonium Phosphate is a better option
                val ammoniumPhosphate = fertilizers["Ammonium Phosphate"]!!
                var nCoveredByAmp = 0f
                var pCoveredByAmp = 0f

                if (nRequirement < pRequirement) {
                    nCoveredByAmp = nRequirement / (ammoniumPhosphate.nitrogen / 100)
                    pCoveredByAmp = (ammoniumPhosphate.phosphorus/100) * nCoveredByAmp
                    assignValue(datamodel, "Ammonium Phosphate", nCoveredByAmp)

                    val newPRequirement = pRequirement-pCoveredByAmp
                    val fertilizer = fertilizers["Duophos"]!!
                    val amountNeeded = newPRequirement / (fertilizer.phosphorus / 100)
                    assignValue(datamodel, "Duophos", amountNeeded)
                }

                if (pRequirement < nRequirement) {
                    pCoveredByAmp = pRequirement / (ammoniumPhosphate.phosphorus / 100)
                    nCoveredByAmp = (ammoniumPhosphate.nitrogen/100) * pCoveredByAmp
                    assignValue(datamodel, "Ammonium Phosphate", pCoveredByAmp)

                    val newNRequirement = nRequirement-nCoveredByAmp
                    val fertilizerName = if (initialN <= 6.6) "Urea" else "Ammonium Sulfate"
                    val fertilizer = fertilizers[fertilizerName]!!
                    val amountNeeded = newNRequirement / (fertilizer.nitrogen / 100)
                    return assignValue(datamodel, fertilizerName, amountNeeded)
                }

                // Use Ammonium Phosphate instead of separate N and P fertilizers
                //result["Ammonium Phosphate"] = maxOf(nRequirement / (ammoniumPhosphate.nitrogen / 100), pRequirement / (ammoniumPhosphate.phosphorus / 100)) //TODO: Remove
                //val amountNeeded = maxOf(nRequirement / (ammoniumPhosphate.nitrogen / 100), pRequirement / (ammoniumPhosphate.phosphorus / 100))

            }
        }

        return datamodel
    }

    private fun assignValue(datamodel: RequiredFertilizerData, fertilizerName: String, amountNeeded: Float): RequiredFertilizerData {
        // Dynamically assign to the first available slot
        if (datamodel.fertilizer1.isEmpty()) {
            datamodel.fertilizer1 = fertilizerName
            datamodel.kgFertilizer1 = amountNeeded
        } else if (datamodel.fertilizer2.isEmpty()) {
            datamodel.fertilizer2 = fertilizerName
            datamodel.kgFertilizer2 = amountNeeded
        } else if (datamodel.fertilizer3.isEmpty()) {
            datamodel.fertilizer3 = fertilizerName
            datamodel.kgFertilizer3 = amountNeeded
        }

        return datamodel
    }

}