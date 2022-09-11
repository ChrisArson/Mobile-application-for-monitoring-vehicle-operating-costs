package com.example.polkar.fuel

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.polkar.R
import com.example.polkar.cars.CarsAdapter
import com.example.polkar.data.Car
import com.example.polkar.data.Fuel
import com.example.polkar.data.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.event_add_form_activity.*
import kotlinx.android.synthetic.main.fuel_add_form_activity.*
import kotlinx.android.synthetic.main.fuel_car_selection_activity.*
import java.util.concurrent.TimeUnit

class FuelAddForm : AppCompatActivity() {
    val db = Firebase.firestore
    private lateinit var mAuth: FirebaseAuth
    lateinit var fuelStation: String
    var fuelAdapter: FuelAdapter? = null
    var carUID: String? = null
    var carTankType: String? = null
    var carMileage: Float? = 0.0f

    var fuelFullTank = false
    val fuels = mutableListOf<Fuel>()
    val car = mutableListOf<User>()
    var average: Float? = null
    var amount: Float? = null
    var price: Float? = null
    var mileage: Float? = null
    var sum: Float? = null
    var globalAverage: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fuel_add_form_activity)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setSupportActionBar(toolbarFuelAddForm)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mAuth = Firebase.auth
        carUID = intent.getStringExtra("uid")
        carTankType = intent.getStringExtra("tankType")
        carMileage = intent.getStringExtra("mileage")?.toFloat()

        spinnerInit()
        setUpRecyclerview()

        toolbarFuelAddForm.setNavigationOnClickListener {
            finish()
        }

        switchFuelAddForm.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                fuelFullTank = true
                averageInfo()
            }else{
                fuelFullTank = false
            }
        }

        buttonAddFuel.setOnClickListener {
            dataVal()
        }
        loadFuels()

        fuelAddFormPrice.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                averageInfo()
            }
        })
        fuelAddFormMileage.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                averageInfo()
            }
        })
        fuelAddFormAmount.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                averageInfo()
            }
        })

    }

    private fun dataVal(){
        if(fuelAddFormAmount.text.toString().isEmpty()){
            fuelAddFormAmount.error="Uzupełnij to pole!"
            fuelAddFormAmount.requestFocus()
        }else if(fuelAddFormPrice.text.toString().isEmpty()){
            fuelAddFormPrice.error="Uzupełnij to pole!"
            fuelAddFormPrice.requestFocus()
        }else if(fuelAddFormMileage.text.toString().isEmpty()){
            fuelAddFormMileage.error="Uzupełnij to pole!"
            fuelAddFormMileage.requestFocus()
        }else{
            addFuel()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun averageInfo(){
        val tmp1 = fuelAddFormAmount.text.toString()
        val tmp2 = fuelAddFormMileage.text.toString()
        val tmp3 = fuelAddFormPrice.text.toString()
        if(tmp1.isEmpty()==false && tmp2.isEmpty()==false && tmp3.isEmpty()==false){
            addMileage()
        }else{
            fuelAddFormAverageInfo.setText("0.0 L/100km")
        }
    }

    private fun spinnerInit(){
        val fuelStationArray = arrayOf("Orlen", "BP", "Shell", "CircleK", "Amic", "Moya")
        val arrayAdapter = ArrayAdapter(this, R.layout.spinner_item,fuelStationArray)

        spinnerFuelAddForm.adapter = arrayAdapter
        spinnerFuelAddForm.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                fuelStation = fuelStationArray[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                fuelStation = fuelStationArray[0]
            }
        }
    }

    private fun loadFuels(){
        db.collection("fuel").whereEqualTo("fuelCarUID",carUID)
            .orderBy("fuelMileage",Query.Direction.DESCENDING).get()
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
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    @SuppressLint("SetTextI18n")
    private fun addMileage(): Float? {
        amount = fuelAddFormAmount.text.toString().toFloat()
        mileage = fuelAddFormMileage.text.toString().toFloat()

        if(fuelFullTank==true && fuels.isNullOrEmpty()==false && fuels[0].fuelFullTank == true){
            average = amount!! /(mileage?.minus(fuels[0].fuelMileage!!)!!)*100
            val tmpaverage = String.format("%.2f", average).toFloat()
            fuelAddFormAverageInfo.setText("" + tmpaverage + " L/100km")
            return average
        }else if(fuelFullTank==true && fuels.isNullOrEmpty()==false){
            var tmp_mileage = 0.0f
            var tmp_amount = 0.0f

            var i = 0

            while(fuels[i].fuelFullTank == false || i > fuels.count() - 1) {
                tmp_mileage = fuels[i].fuelMileage!!
                tmp_amount += fuels[i].fuelAmount!!

                i += 1
            }
            average = (tmp_amount + amount!!) / (mileage!! - tmp_mileage) * 100
            return average
        }else{
            average = 0f
            return average
        }
    }

    private fun addAverageToCar() {
        var sumOfFuel: Float = 0.0f
        mileage = fuelAddFormMileage.text.toString().toFloat()

        for (tmpFuel in fuels) {
            sumOfFuel += tmpFuel.fuelAmount!!
        }
        if (fuels.isNullOrEmpty()==false){
            sumOfFuel -= fuels[fuels.count() - 1].fuelAmount!!
            globalAverage = sumOfFuel / (fuels[0].fuelMileage?.minus(fuels[fuels.count() - 1].fuelMileage!!)!!) * 100
        }else{
            globalAverage = 0.0F
        }

        val carRef = carUID?.let { db.collection("cars").document(it) }
        if (carRef != null) {
            carRef
                .update("carAverageFuelUsage", globalAverage)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
        }

        if(mileage!!> carMileage!!){
            if (carRef != null) {
                carRef
                    .update("carMileage", mileage)
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
            }
        }
        finish()
    }



    private fun addFuel(){
        val userUID = mAuth.currentUser!!.uid
        val average1 = addMileage()
        val timeMillis = System.currentTimeMillis()
        val timeSeconds: Long = TimeUnit.MILLISECONDS.toSeconds(timeMillis)
        val fuelPrice = fuelAddFormPrice.text.toString().toFloat()
        val fuelPriceRounded = String.format("%.2f", fuelPrice)

        val fuel = hashMapOf(
            "UID" to "",
            "fuelAmount" to fuelAddFormAmount.text.toString().toFloat(),
            "fuelAverage" to average1,
            "fuelCarUID" to carUID,
            "fuelFullTank" to fuelFullTank,
            "fuelMileage" to fuelAddFormMileage.text.toString().toInt(),
            "fuelPrice" to fuelPriceRounded.toFloat(),
            "fuelStation" to fuelStation,
            "fuelSum" to fuelAddFormPrice.text.toString().toFloat()*fuelAddFormAmount.text.toString().toFloat(),
            "fuelType" to carTankType,
            "time" to timeSeconds,
            "fuelUserUID" to userUID
        )
        db.collection("fuel")
            .add(fuel)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                val eventAdded = db.collection("fuel").document(documentReference.id)
                eventAdded.update("UID", documentReference.id)
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
        addAverageToCar()
    }

    private fun setUpRecyclerview(){
        val query = db.collection("fuel").whereEqualTo("fuelCarUID", carUID).orderBy("fuelMileage", Query.Direction.DESCENDING)
        query.get().addOnCompleteListener { task ->
            val b = task.result!!.isEmpty()
            if(!b){
                Log.d(ContentValues.TAG, "Dane zostały pobrane pomyślnie!")
            }
        }
        val firestoreRecyclerOptions: FirestoreRecyclerOptions<Fuel> = FirestoreRecyclerOptions.Builder<Fuel>()
            .setQuery(query, Fuel::class.java)
            .build()

        fuelAdapter = FuelAdapter(firestoreRecyclerOptions)

        recyclerFuelAddForm.layoutManager = LinearLayoutManager(this)
        recyclerFuelAddForm.adapter = fuelAdapter

        fuelAdapter!!.setOnItemClickListener(object : FuelAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
            }
        })

    }

    override fun onStart() {
        super.onStart()
        fuelAdapter!!.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        fuelAdapter!!.stopListening()
    }

}
