package com.example.polkar.statistics

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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fuel_add_form_activity.*
import kotlinx.android.synthetic.main.user_statistics_activity.*
import java.util.concurrent.TimeUnit

class UserStatistics : AppCompatActivity() {
    val db = Firebase.firestore
    private lateinit var mAuth: FirebaseAuth
    val fuels = mutableListOf<Fuel>()
    val cars = mutableListOf<Car>()
    val events = mutableListOf<Event>()
    var fuelCount: Int? = null
    var eventCount: Int? = null
    var carCount: Int? = null
    var fuelMonthPrice = 0.0f
    var fuelFullPrice = 0.0f
    var eventMonthPrice = 0
    var eventFullPrice = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_statistics_activity)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setSupportActionBar(toolbarUserStats)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mAuth = Firebase.auth
        toolbarUserStats.setNavigationOnClickListener {
            finish()
        }
        loadFuels()
        loadCars()
        loadEvents()
    }

    private fun loadFuels(){
        val userUID = mAuth.currentUser!!.uid
        db.collection("fuel").whereEqualTo("fuelUserUID",userUID).get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    val UID = document.getString("UID")
                    val amount = document.getDouble("fuelAmount")?.toFloat()
                    val mileage = document.getLong("fuelMileage")?.toFloat()
                    val price = document.getDouble("fuelPrice")?.toFloat()
                    val sum = document.getDouble("fuelSum")?.toFloat()
                    val average = document.getDouble("fuelAverage")?.toFloat()
                    val fuelCarUID = document.getString("fuelCarUID")
                    val fullTank = document.getBoolean("fuelFullTank")
                    val fuelType = document.getString("fuelType")
                    val fuelStation = document.getString("fuelStation")
                    val fuelUserUID = document.getString("fuelUserUID")
                    val time = document.getLong("time")
                    fuels.add(
                        Fuel(
                            UID = UID,
                            fuelAmount = amount,
                            fuelMileage = mileage,
                            fuelPrice = price,
                            fuelSum = sum,
                            fuelAverage = average,
                            fuelCarUID = fuelCarUID,
                            fuelFullTank = fullTank,
                            fuelType = fuelType,
                            fuelStation = fuelStation,
                            fuelUserUID = fuelUserUID,
                            time = time
                        )
                    )
                }
                showFuelCount()
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    private fun loadCars(){
        val userUID = mAuth.currentUser!!.uid
        db.collection("cars").whereEqualTo("userUID",userUID).get()
            .addOnSuccessListener { documents ->
                for (document in documents){
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
                showCarCount()
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    private fun loadEvents(){
        val userUID = mAuth.currentUser!!.uid
        db.collection("events").whereEqualTo("eventUserUID",userUID).get()
            .addOnSuccessListener { documents ->
                for (document in documents){

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
                showEventCount()
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    fun showFuelCount(){
        fuelCount = fuels.count()
        tvFuelCount.setText(fuelCount.toString())

        for(n in fuels){
            fuelFullPrice += n.fuelSum!!
            val fuelFullPriceString = "$fuelFullPrice zł"
            tvFuelPriceAll.setText(fuelFullPriceString)
        }

        val timeMillis = System.currentTimeMillis()
        val timeSeconds: Long = TimeUnit.MILLISECONDS.toSeconds(timeMillis)
        val last30Days: Long = timeSeconds - 2592000
        for(n in fuels){
            if(n.time!!>last30Days){
                fuelMonthPrice+=n.fuelSum!!
            }
        }
        val fuelMonthPriceString = "$fuelMonthPrice zł"
        tvFuelPriceMonth.setText(fuelMonthPriceString)

    }

    fun showCarCount(){
        carCount = cars.count()
        tvCarCount.setText(carCount.toString())
    }

    fun showEventCount(){
        eventCount = events.count()
        tvEventCount.setText(eventCount.toString())

        for(n in events){
            eventFullPrice += n.eventPrice!!
        }
        val eventFullPriceString = "$eventFullPrice zł"
        tvEventPriceFull.setText(eventFullPriceString)

        val timeMillis = System.currentTimeMillis()
        val timeSeconds: Long = TimeUnit.MILLISECONDS.toSeconds(timeMillis)
        val last30Days: Long = timeSeconds - 2592000
        for(n in events){
            if(n.time!!>last30Days){
                eventMonthPrice+=n.eventPrice!!
            }
        }
        val eventMonthPriceString = "$eventMonthPrice zł"
        tvEventPriceMonth.setText(eventMonthPriceString)
    }
}
