package com.example.polkar.data

data class Fuel(val UID: String? = null,
                val fuelAmount: Float? = null,
                val fuelMileage: Float? = null,
                val fuelPrice: Float? = null,
                var fuelSum: Float? = null,
                val fuelAverage: Float? = null,
                val fuelCarUID: String? = null,
                val fuelFullTank: Boolean? = null,
                var fuelType: String? = null,
                val fuelStation: String? = null,
                val time: Long? = null,
                val fuelUserUID: String? = null)

