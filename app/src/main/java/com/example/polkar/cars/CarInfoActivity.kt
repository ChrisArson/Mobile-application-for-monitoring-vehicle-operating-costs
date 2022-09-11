package com.example.polkar.cars

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.polkar.data.Car
import com.example.polkar.data.Event
import com.example.polkar.data.Fuel
import com.example.polkar.events.EventAddForm
import com.example.polkar.events.EventInfoActivity
import com.example.polkar.fuel.FuelAdapter
import com.example.polkar.fuel.FuelAddForm
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.car_info_activity.*

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.car_add_form_activity.*
import kotlinx.android.synthetic.main.event_list_activity.*
import kotlinx.android.synthetic.main.fuel_add_form_activity.*
import kotlinx.android.synthetic.main.global_statistics_activity.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


class CarInfoActivity : AppCompatActivity() {
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var mAuth: FirebaseAuth
    var carInfoEventAdapter: CarInfoEventAdapter? = null
    var fuelAdapter: FuelAdapter? = null
    var carInfoEventReminderAdapter: CarInfoEventReminderAdapter? = null
    var carUID: String? = null
    var bodyType: String? = null
    var fuelType: String? = null
    var carMileage: String? = null
    var carModel: String? = null
    var carBrand: String? = null
    var carInsurance: String? = null
    var carService: String? = null
    val events = mutableListOf<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.polkar.R.layout.car_info_activity)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setSupportActionBar(toolbarCarInfo)

        carUID = intent.getStringExtra("uid")
        bodyType = intent.getStringExtra("bodyType")
        fuelType = intent.getStringExtra("fuelType")
        carMileage = intent.getStringExtra("mileage")
        carModel = intent.getStringExtra("model")
        carBrand = intent.getStringExtra("brand")
        carInsurance = intent.getStringExtra("insurance")
        carService= intent.getStringExtra("service")

        Toast.makeText(this@CarInfoActivity, "Przesłano $carUID", Toast.LENGTH_SHORT).show()

        toolbarCarInfo.setNavigationOnClickListener {
            finish()
        }

        val builder = AlertDialog.Builder(this)
        buttonDelete.setOnClickListener {
            builder.setTitle("Uwaga!")
                .setMessage("Na pewno chcesz usunąć pojazd?")
                .setCancelable(true)
                .setPositiveButton("Tak"){dialogInterface,it->
                    deleteCar()
                }
                .setNegativeButton("Anuluj"){dialogInterface,it->
                    dialogInterface.cancel()
                }
                .show()
        }

        buttonDodawanieEventu.setOnClickListener {
            val carUID = carUID
            val carBrand = carBrand
            val carModel = carModel
            val carMileage = carMileage
            val intent = Intent(this@CarInfoActivity, EventAddForm::class.java)
            intent.putExtra("uid",carUID)
            intent.putExtra("brand",carBrand)
            intent.putExtra("model",carModel)
            intent.putExtra("mileage",carMileage)
            startActivity(intent)
        }

        buttonDodawanieTankowania.setOnClickListener {
            val carUID = carUID
            val carTankType = fuelType
            val carMileage = carMileage
            val intent = Intent(this@CarInfoActivity, FuelAddForm::class.java)
            intent.putExtra("uid",carUID)
            intent.putExtra("tankType",carTankType)
            intent.putExtra("mileage",carMileage)
            startActivity(intent)
        }

        buttonEdit.setOnClickListener {
            val intent = Intent(this@CarInfoActivity, CarEditForm::class.java)
            intent.putExtra("uid", carUID)
            intent.putExtra("bodyType",bodyType)
            intent.putExtra("fuelType",fuelType)
            startActivity(intent)
            finish()
        }
        carInfo()
        setUpRecyclerviewEvents()
        setUpRecyclerviewFuel()
        setUpRecyclerviewEventReminder()
        insuranceServiceDate()
        loadEvents()
    }

    private fun insuranceServiceDate(){
        val timeMillis = System.currentTimeMillis()
        val timeSeconds: Long = TimeUnit.MILLISECONDS.toSeconds(timeMillis)

        val carInsuranceToFormat = LocalDate.parse(carInsurance, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val carInsuranceTimeInSeconds = carInsuranceToFormat.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond
        val carInsuranceEndInSeconds = ((carInsuranceTimeInSeconds-timeSeconds)/86400)+1
        if(carInsuranceEndInSeconds==1L||carInsuranceEndInSeconds==0L){
            val tvInsuranceFormatted = carInsuranceEndInSeconds.toString() + " dzień"
            tvInsuranceEnd.setText(tvInsuranceFormatted)
        }else{
            val tvInsuranceFormatted = carInsuranceEndInSeconds.toString() + " dni"
            tvInsuranceEnd.setText(tvInsuranceFormatted)
        }

        val carServiceToFormat = LocalDate.parse(carService, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val carServiceTimeInSeconds = carServiceToFormat.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond
        val carServiceEndInSeconds = ((carServiceTimeInSeconds-timeSeconds)/86400)+1
        if(carServiceEndInSeconds==1L||carServiceEndInSeconds==0L){
            val tvInsuranceFormatted = carServiceEndInSeconds.toString() + " dzień"
            tvServiceEnd.setText(tvInsuranceFormatted)
        }else{
            val tvInsuranceFormatted = carServiceEndInSeconds.toString() + " dni"
            tvServiceEnd.setText(tvInsuranceFormatted)
        }
    }

    private fun carInfo(){
        val query = carUID?.let { db.collection("cars").document(it) }
        if (query != null) {
            query.get().addOnSuccessListener { documentSnapshot ->
                val car = documentSnapshot.toObject<Car>()
                if (car != null) {
                    Toast.makeText(
                        this@CarInfoActivity,
                        "Odczytano ${car.carFuelTankCapacity}",
                        Toast.LENGTH_SHORT
                    ).show()
                    tvInfoInsurance.setText(car.carInsurance)
                    tvInfoService.setText(car.carService)
                    val tmpFuelSum = car.carAverageFuelUsage
                    if (tmpFuelSum != null) {
                        if(tmpFuelSum.isNaN()){
                            tvSpalanie.setText("0.0")
                        }else{
                            val fuelSumRounded = String.format("%.2f", tmpFuelSum) + " L/100km"
                            tvSpalanie.setText(fuelSumRounded)
                        }
                    }

                }

            }
        }
    }

    private fun deleteCar() {
        val carUID = intent.getStringExtra("uid")
        if (carUID != null) {
            db.collection("cars").document(carUID)
                .delete()
                .addOnSuccessListener {
                    mAuth = FirebaseAuth.getInstance();
                    val car = db.collection("users").document(mAuth.currentUser!!.uid)
                    car.update("userCars", FieldValue.arrayRemove(carUID))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this@CarInfoActivity,
                        "Pojazd nie został usunięty",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun loadEvents() {
        db.collection("events").whereEqualTo("eventCarUID", carUID).get()
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

    fun showEventStats(events: MutableList<Event>) {
        if (events.count() > 1) {
            val mileage = events[0].eventMileage?.minus(events[events.count() - 1].eventMileage!!)
            var sum: Float = 0.0f

            for (event in events) {
                sum += event.eventPrice!!
            }

            val average = sum / mileage!! * 1000
            val averageString = String.format("%.2f", average)+ " zł"
            tvKosztNa1000.setText(averageString)
        } else {
            tvKosztNa1000.setText("0 zł")
        }
    }

    private fun setUpRecyclerviewEvents(){
        val query = db.collection("events").whereEqualTo("eventCarUID", carUID).orderBy("time", Query.Direction.DESCENDING).limit(5)
        query.get().addOnCompleteListener { task ->
            val b = task.result!!.isEmpty()
            if(b){
                tvEmptyEvents.visibility = View.VISIBLE
            }
        }
        val firestoreRecyclerOptions: FirestoreRecyclerOptions<Event> = FirestoreRecyclerOptions.Builder<Event>()
            .setQuery(query, Event::class.java)
            .build()

        carInfoEventAdapter = CarInfoEventAdapter(firestoreRecyclerOptions)

        recyclerEventCarInfo.layoutManager = LinearLayoutManager(this)
        recyclerEventCarInfo.adapter = carInfoEventAdapter

        carInfoEventAdapter!!.setOnItemClickListener(object : CarInfoEventAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val eventUID = carInfoEventAdapter!!.getItem(position).UID
                val type = carInfoEventAdapter!!.getItem(position).eventType
                val intent = Intent(this@CarInfoActivity, EventInfoActivity::class.java)
                intent.putExtra("uid",eventUID)
                intent.putExtra("type",type)
                startActivity(intent)
            }
        })

    }

    private fun setUpRecyclerviewFuel(){
        val query = db.collection("fuel").whereEqualTo("fuelCarUID", carUID).orderBy("fuelMileage", Query.Direction.DESCENDING).limit(5)
        query.get().addOnCompleteListener { task ->
            val b = task.result!!.isEmpty()
            if(b){
                tvEmptyFuels.visibility = View.VISIBLE
            }
        }
        val firestoreRecyclerOptions: FirestoreRecyclerOptions<Fuel> = FirestoreRecyclerOptions.Builder<Fuel>()
            .setQuery(query, Fuel::class.java)
            .build()

        fuelAdapter = FuelAdapter(firestoreRecyclerOptions)

        recyclerFuelCarInfo.layoutManager = LinearLayoutManager(this)
        recyclerFuelCarInfo.adapter = fuelAdapter

        fuelAdapter!!.setOnItemClickListener(object : FuelAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
            }
        })
    }

    private fun setUpRecyclerviewEventReminder(){
        val carMileagetoReminder = intent.getStringExtra("mileage")?.toInt()
        val query =
            carMileagetoReminder?.let {
                db.collection("events").whereEqualTo("eventCarUID", carUID).whereGreaterThan("eventMileageReminder", it).orderBy("eventMileageReminder")
            }
        if (query != null) {
            query.get().addOnCompleteListener { task ->
                val b = task.result!!.isEmpty()
                if(b){
                    tvEmptyEventReminderEvents.visibility = View.VISIBLE
                }
            }
        }
        val firestoreRecyclerOptions: FirestoreRecyclerOptions<Event> = query?.let {
            FirestoreRecyclerOptions.Builder<Event>()
                .setQuery(it, Event::class.java)
                .build()
        } as FirestoreRecyclerOptions<Event>

        carInfoEventReminderAdapter = CarInfoEventReminderAdapter(firestoreRecyclerOptions)

        recyclerEventReminder.layoutManager = LinearLayoutManager(this)
        recyclerEventReminder.adapter = carInfoEventReminderAdapter

        carInfoEventReminderAdapter!!.setOnItemClickListener(object : CarInfoEventReminderAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val eventUID = carInfoEventReminderAdapter!!.getItem(position).UID
                val type = carInfoEventReminderAdapter!!.getItem(position).eventType
                val intent = Intent(this@CarInfoActivity, EventInfoActivity::class.java)
                intent.putExtra("uid",eventUID)
                intent.putExtra("type",type)
                startActivity(intent)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        carInfoEventAdapter!!.startListening()
        fuelAdapter!!.startListening()
        carInfoEventReminderAdapter!!.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        carInfoEventAdapter!!.stopListening()
        fuelAdapter!!.stopListening()
        carInfoEventReminderAdapter!!.stopListening()
    }
}





