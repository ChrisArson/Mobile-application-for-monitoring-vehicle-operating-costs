package com.example.polkar.cars

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.polkar.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.car_add_form_activity.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class CarAddForm : AppCompatActivity() {
    val db = Firebase.firestore
    private lateinit var mAuth: FirebaseAuth;
    lateinit var fuelChosen: String
    lateinit var bodyChosen: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.car_add_form_activity)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setSupportActionBar(toolbarCarAddForm)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mAuth = Firebase.auth
        spinnerFuelInit()
        spinnerBodyInit()

        toolbarCarAddForm.setNavigationOnClickListener {
            finish()
        }

        buttonCarAdd.setOnClickListener {
            valid()
        }
    }

    private fun spinnerFuelInit(){
        val fuelType = arrayOf("Benzyna", "Diesel", "LPG", "CNG")
        val arrayAdapter = ArrayAdapter(this, R.layout.spinner_item,fuelType)

        spinnerFuelType.adapter = arrayAdapter
        spinnerFuelType.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                fuelChosen = fuelType[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                fuelChosen = fuelType[0]
            }
        }
    }

    private fun spinnerBodyInit(){
        val bodyType = arrayOf("Hatchback", "Kombi", "Sedan", "SUV", "Van", "Coupe","Kabriolet")
        val arrayAdapter = ArrayAdapter(this, R.layout.spinner_item,bodyType)

        spinnerBody.adapter = arrayAdapter
        spinnerBody.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                bodyChosen = bodyType[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                bodyChosen = bodyType[0]
            }
        }
    }

    private fun valid(){
        val pattern = Regex("^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1" +
                "|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)" +
                "?\\d{2})\$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468]" +
                "[048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))\$|^(?:0?[1-9]|1\\" +
                "d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$")
        val dateService = etService.text.toString()
        val dateInsurance = etInsurance.text.toString()
        val resultService = pattern.containsMatchIn(dateService)
        val resultInsurance = pattern.containsMatchIn(dateInsurance)

        if(resultService==false){
            etService.error="DD-MM-RRRR"
            etService.requestFocus()
        }else if(resultInsurance==false){
            etInsurance.error="DD-MM-RRRR"
            etInsurance.requestFocus()
        }else if(etBrand.text.toString().isEmpty()){
            etBrand.error="Uzupełnij to pole!"
            etBrand.requestFocus()
        }else if(etModel.text.toString().isEmpty()){
            etModel.error="To pole nie można pozostac puste!"
            etModel.requestFocus()
        }else if(etEngine.text.toString().isEmpty()){
            etEngine.error="Uzupełnij to pole!"
            etEngine.requestFocus()
        }else if(etMileage.text.toString().isEmpty()){
            etMileage.error="Uzupełnij to pole!"
            etMileage.requestFocus()
        }else if(etFuelTankCapacity.text.toString().isEmpty()){
            etFuelTankCapacity.error="Uzupełnij to pole!"
            etFuelTankCapacity.requestFocus()
        }else{
            validateDate()
        }
    }


    private fun validateDate(){
        var dateServiceFromForm = etService.text.toString()
        val secondCharService: Char = dateServiceFromForm.get(1)
        val fourthCharService: Char = dateServiceFromForm.get(3)
        val fifthCharService: Char = dateServiceFromForm.get(4)

        if(secondCharService == '-' && fourthCharService != '-'){
            dateServiceFromForm = "0" + dateServiceFromForm.substring(0, dateServiceFromForm.length)
        }else if(fifthCharService == '-'){
            dateServiceFromForm = dateServiceFromForm.substring(0, 3) + "0" +
                    dateServiceFromForm.substring(3, dateServiceFromForm.length)
        }else if(secondCharService == '-' && fourthCharService == '-'){
            dateServiceFromForm = "0" + dateServiceFromForm.substring(0, 2) + "0" +
                    dateServiceFromForm.substring(2, dateServiceFromForm.length) }

        var dateInsuranceFromForm = etInsurance.text.toString()
        val secondCharInsurance: Char = dateInsuranceFromForm.get(1)
        val fourthCharInsurance: Char = dateInsuranceFromForm.get(3)
        val fifthCharInsurance: Char = dateInsuranceFromForm.get(4)

        if(secondCharInsurance == '-' && fourthCharInsurance != '-'){
            dateInsuranceFromForm = "0" + dateInsuranceFromForm.substring(0, dateInsuranceFromForm.length)
        }else if(fifthCharInsurance == '-'){
            dateInsuranceFromForm = dateInsuranceFromForm.substring(0, 3) + "0" +
                    dateInsuranceFromForm.substring(3, dateInsuranceFromForm.length)
        }else if(secondCharInsurance == '-' && fourthCharInsurance == '-'){
            dateInsuranceFromForm = "0" + dateInsuranceFromForm.substring(0, 2) + "0" +
                    dateInsuranceFromForm.substring(2, dateInsuranceFromForm.length) }

        if(dateServiceFromForm.length!=10){
            etService.error="DD-MM-RRRR"
            etService.requestFocus()
        }else if(dateInsuranceFromForm.length!=10){
            etInsurance.error="DD-MM-RRRR"
            etInsurance.requestFocus()
        }else{
            addCar(dateServiceFromForm,dateInsuranceFromForm) }
    }

    private fun addCar(serviceDate: String, insuranceDate: String){
        val uid = mAuth.currentUser!!.uid
        val timeMillis = System.currentTimeMillis()
        val timeSeconds: Long = TimeUnit.MILLISECONDS.toSeconds(timeMillis)
        val car = hashMapOf(
            "UID" to "",
            "carBody" to bodyChosen,
            "carBrand" to etBrand.text.toString(),
            "carEngine" to etEngine.text.toString(),
            "carFuelTankCapacity" to etFuelTankCapacity.text.toString().toInt(),
            "carFuelType" to fuelChosen,
            "carInsurance" to insuranceDate,
            "carMileage" to etMileage.text.toString().toInt(),
            "carModel" to etModel.text.toString(),
            "carService" to serviceDate,
            "time" to timeSeconds,
            "userUID" to uid,
            "carAverageFuelUsage" to 0.0f
        )
        db.collection("cars")
            .add(car)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                val uid = mAuth.currentUser!!.uid
                val user = db.collection("users").document(uid)
                user.update("userCars", FieldValue.arrayUnion(documentReference.id))
                val carAdded = db.collection("cars").document(documentReference.id)
                carAdded.update("UID", documentReference.id)
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }

}