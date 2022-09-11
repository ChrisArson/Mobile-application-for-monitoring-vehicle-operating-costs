package com.example.polkar.statistics

import android.annotation.SuppressLint
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.polkar.R
import com.example.polkar.data.Car
import com.example.polkar.data.Event
import com.example.polkar.data.Fuel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.car_statistics_activity.*
import kotlinx.android.synthetic.main.global_statistics_activity.*
import kotlinx.android.synthetic.main.global_statistics_model_selection.*
import java.util.concurrent.TimeUnit

class GlobalStatistics : AppCompatActivity() {
    val db = Firebase.firestore
    var carBrand: String? = null
    var carModel: String? = null
    val events = mutableListOf<Event>()
    val cars = mutableListOf<Car>()
    var pbSum: Float = 0.0f
    var pbCount: Float = 0.0f
    var onSum: Float = 0.0f
    var onCount: Float = 0.0f
    var lpgSum: Float = 0.0f
    var lpgCount: Float = 0.0f
    var cngSum: Float = 0.0f
    var cngCount: Float = 0.0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.global_statistics_activity)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setSupportActionBar(toolbarGlobalStatistics)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        carBrand = intent.getStringExtra("carBrand")
        carModel = intent.getStringExtra("carModel")

        toolbarGlobalStatistics.setNavigationOnClickListener {
            finish()
        }
        loadCars()
        loadEvents()
    }

    private fun loadEvents() {
        db.collection("events").whereEqualTo("eventBrand", carBrand)
            .whereEqualTo("eventModel", carModel).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                    val UID = document.getString("UID")
                    val eventCarUID = document.getString("eventCarUID")
                    val eventUserUID = document.getString("eventUserUID")
                    val eventDate = document.getString("eventDate")
                    val eventDescription = document.getString("eventDescription")
                    val eventType = document.getString("eventType")
                    val eventMileage = document.getLong("eventMileage")?.toInt()
                    val eventPrice = document.getLong("eventPrice")?.toInt()
                    val time = document.getLong("time")
                    val eventBrand = document.getString("eventBrand")
                    val eventModel = document.getString("eventModel")
                    val eventReminder = document.getBoolean("eventReminder")
                    val eventMileageReminder = document.getLong("eventMileageReminder")?.toInt()

                    events.add(
                        Event(
                            UID = UID,
                            eventCarUID = eventCarUID,
                            eventUserUID = eventUserUID,
                            eventDate = eventDate,
                            eventDescription = eventDescription,
                            eventType = eventType,
                            eventMileage = eventMileage,
                            eventPrice = eventPrice,
                            time = time,
                            eventBrand = eventBrand,
                            eventModel = eventModel,
                            eventReminder = eventReminder,
                            eventMileageReminder = eventMileageReminder
                        )
                    )
                }
                showEventStats(events)
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    private fun loadCars() {
        db.collection("cars").whereEqualTo("carBrand", carBrand).whereEqualTo("carModel", carModel).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val UID = document.getString("UID")
                    val carBody = document.getString("carBody")
                    val carBrand = document.getString("carBrand")
                    val carEngine = document.getString("carEngine")
                    val carFuelTankCapacity = document.getLong("carFuelTankCapacity")?.toInt()
                    val carFuelType = document.getString("carFuelType")
                    val carInsurance = document.getString("carInsurance")
                    val carMileage = document.getLong("carMileage")?.toInt()
                    val carModel = document.getString("carModel")
                    val carService = document.getString("carService")
                    val time = document.getLong("time")
                    val carUserUID = document.getString("userUID")
                    val carAverageFuelUsage = document.getDouble("carAverageFuelUsage")?.toFloat()
                    cars.add(
                        Car(
                            UID = UID,
                            carBody = carBody,
                            carBrand = carBrand,
                            carEngine = carEngine,
                            carFuelTankCapacity = carFuelTankCapacity,
                            carFuelType = carFuelType,
                            carInsurance = carInsurance,
                            carMileage = carMileage,
                            carModel = carModel,
                            carService = carService,
                            time = time,
                            userUID = carUserUID,
                            carAverageFuelUsage = carAverageFuelUsage
                        )
                    )
                }
                showCarStats(cars)
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    fun showEventStats(events: MutableList<Event>) {

        var fixSum: Float = 0.0f
        var fixCount = 0
        var fixMileageMin: Float? = null
        var fixMileageMax: Float? = null
        var fixAverage: Float = 0.0f

        var partsReplacementSum: Float = 0.0f
        var partsReplacementCount = 0
        var partsReplacementMileageMin: Float? = null
        var partsReplacementMileageMax: Float? = null
        var partsReplacementAverage: Float = 0.0f

        var oilReplacementSum: Float = 0.0f
        var oilReplacementCount = 0
        var oilReplacementMileageMin: Float? = null
        var oilReplacementMileageMax: Float? = null
        var oilReplacementAverage: Float = 0.0f

        var otherSum: Float = 0.0f
        var otherCount = 0
        var otherMileageMin: Float? = null
        var otherMileageMax: Float? = null
        var otherAverage: Float = 0.0f

        var globalCount: Float = 4.0f

        for (event in events) {
            when (event.eventType) {
                "Naprawa" -> {
                    if (fixMileageMax != null) {
                        if (fixMileageMax < event.eventMileage!!) {
                            fixMileageMax = event.eventMileage.toFloat()
                        }
                    } else {
                        fixMileageMax = event.eventMileage?.toFloat()
                    }
                    if (fixMileageMin != null) {
                        if (fixMileageMin > event.eventMileage!!) {
                            fixMileageMin = event.eventMileage.toFloat()
                        }
                    } else {
                        fixMileageMin = event.eventMileage?.toFloat()
                    }
                    fixSum += event.eventPrice!!
                    fixCount += 1
                }
                "Wymiana czesci eksploatacyjnych" -> {
                    if (partsReplacementMileageMax != null) {
                        if (partsReplacementMileageMax < event.eventMileage!!) {
                            partsReplacementMileageMax = event.eventMileage.toFloat()
                        }
                    } else {
                        partsReplacementMileageMax = event.eventMileage?.toFloat()
                    }
                    if (partsReplacementMileageMin != null) {
                        if (partsReplacementMileageMin > event.eventMileage!!) {
                            partsReplacementMileageMin = event.eventMileage.toFloat()
                        }
                    } else {
                        partsReplacementMileageMin = event.eventMileage?.toFloat()
                    }
                    partsReplacementSum += event.eventPrice!!
                    partsReplacementCount += 1
                }
                "Wymiana oleji i filtrow" -> {
                    if (oilReplacementMileageMax != null) {
                        if (oilReplacementMileageMax < event.eventMileage!!) {
                            oilReplacementMileageMax = event.eventMileage.toFloat()
                        }
                    } else {
                        oilReplacementMileageMax = event.eventMileage?.toFloat()
                    }
                    if (oilReplacementMileageMin != null) {
                        if (oilReplacementMileageMin > event.eventMileage!!) {
                            oilReplacementMileageMin = event.eventMileage.toFloat()
                        }
                    } else {
                        oilReplacementMileageMin = event.eventMileage?.toFloat()
                    }
                    oilReplacementSum += event.eventPrice!!
                    oilReplacementCount += 1
                }
                "Inne" -> {
                    if (otherMileageMax != null) {
                        if (otherMileageMax < event.eventMileage!!) {
                            otherMileageMax = event.eventMileage.toFloat()
                        }
                    } else {
                        otherMileageMax = event.eventMileage?.toFloat()
                    }
                    if (otherMileageMin != null) {
                        if (otherMileageMin > event.eventMileage!!) {
                            otherMileageMin = event.eventMileage.toFloat()
                        }
                    } else {
                        otherMileageMin = event.eventMileage?.toFloat()
                    }
                    otherSum += event.eventPrice!!
                    otherCount += 1
                }
            }
        }

        if (fixMileageMax != null && fixMileageMin != null) {
            if (fixMileageMax - fixMileageMin != 0.0f) {
                fixAverage = fixSum / (fixMileageMax - fixMileageMin) * 1000
                val fixAverageString = String.format("%.2f", fixAverage)+ " zł"
                tvGlobalStatCarRepair.setText(fixAverageString)
            } else {
                tvGlobalStatCarRepair.setText("0 zł")
                globalCount -= 1
            }
        } else {
            tvGlobalStatCarRepair.setText("0 zł")
            globalCount -= 1
        }

        if (partsReplacementMileageMax != null && partsReplacementMileageMin != null) {
            if (partsReplacementMileageMax - partsReplacementMileageMin != 0.0f) {
                partsReplacementAverage =
                    partsReplacementSum / (partsReplacementMileageMax - partsReplacementMileageMin) * 1000
                val partsReplacementAverageString = String.format("%.2f", partsReplacementAverage)+ " zł"
                tvGlobalStatCarChangeParts.setText(partsReplacementAverageString)
            } else {
                tvGlobalStatCarChangeParts.setText("0 zł")
                globalCount -= 1
            }
        } else {
            tvGlobalStatCarChangeParts.setText("0 zł")
            globalCount -= 1
        }

        if (oilReplacementMileageMax != null && oilReplacementMileageMin != null) {
            if (oilReplacementMileageMax - oilReplacementMileageMin != 0.0f) {
                oilReplacementAverage =
                    oilReplacementSum / (oilReplacementMileageMax - oilReplacementMileageMin) * 1000
                val oilReplacementAverageString = String.format("%.2f", oilReplacementAverage)+ " zł"
                tvGlobalStatCarChangeOil.setText(oilReplacementAverageString)
            } else {
                tvGlobalStatCarChangeOil.setText("0 zł")
                globalCount -= 1
            }
        } else {
            tvGlobalStatCarChangeOil.setText("0 zł")
            globalCount -= 1
        }

        if (otherMileageMax != null && otherMileageMin != null) {
            if (otherMileageMax - otherMileageMin != 0.0f) {
                otherAverage = otherSum / (otherMileageMax - otherMileageMin) * 1000
                val otherAverageString = String.format("%.2f", otherAverage)+ " zł"
                tvGlobalStatCarAnother.setText(otherAverageString)
            } else {
                tvGlobalStatCarAnother.setText("0 zł")
                globalCount -= 1
            }
        } else {
            tvGlobalStatCarAnother.setText("0 zł")
            globalCount -= 1
        }

        if (globalCount != 0.0f) {
            val average =
                (fixAverage + partsReplacementAverage + oilReplacementAverage + otherAverage) / globalCount
            val averageString = String.format("%.2f", average)+ " zł"
            tvGlobalStatCarAverage.setText(averageString)
        } else {
            tvGlobalStatCarAverage.setText("0 zł")
        }
    }


    @SuppressLint("SetTextI18n")
    fun showCarStats(cars: MutableList<Car>) {
        tvGlobalStatCarName.text = "Statystyki dla pojazdu $carBrand $carModel"

        for (car in cars) {
            when (car.carFuelType) {
                "Benzyna" -> {
                    pbSum += car.carAverageFuelUsage!!; pbCount += 1
                }
                "Diesel" -> {
                    onSum += car.carAverageFuelUsage!!; onCount += 1
                }
                "LPG" -> {
                    lpgSum += car.carAverageFuelUsage!!; lpgCount += 1
                }
                "CNG" -> {
                    cngSum += car.carAverageFuelUsage!!; cngCount += 1
                }
            }
        }
        if (pbCount != 0.0f) {
            val pbAverage = pbSum/pbCount
            val pbAverageString = String.format("%.2f", pbAverage)
            tvGlobalStatCarPB.setText(pbAverageString)
        } else { tvGlobalStatCarPB.text = "0" }
        if (onCount != 0.0f) {
            val onAverage = onSum/onCount
            val onAverageString = String.format("%.2f", onAverage)
            tvGlobalStatCarON.setText(onAverageString)
        } else { tvGlobalStatCarON.text = "0" }
        if (lpgCount != 0.0f) {
            val lpgAverage = lpgSum/lpgCount
            val lpgAverageString = String.format("%.2f", lpgAverage)
            tvGlobalStatCarLPG.setText(lpgAverageString)
        } else { tvGlobalStatCarLPG.text = "0" }
        if (cngCount != 0.0f) {
            val cngAverage = cngSum/cngCount
            val onAverageString = String.format("%.2f", cngAverage)
            tvGlobalStatCarCNG.setText(onAverageString)
        } else { tvGlobalStatCarCNG.text = "0" }
    }
}
