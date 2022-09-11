package com.example.polkar.cars

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.polkar.R
import com.example.polkar.data.Car
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.car_edit_form_activity.*
import kotlinx.android.synthetic.main.car_add_form_activity.*
import kotlinx.android.synthetic.main.car_info_activity.*
import kotlinx.android.synthetic.main.event_edit_form_activity.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CarEditForm : AppCompatActivity() {
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var mAuth: FirebaseAuth
    var carUID: String? = null
    var bodyTypeSpinner: String? = null
    var fuelTypeSpinner: String? = null
    lateinit var fuelChosen: String
    lateinit var bodyChosen: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.car_edit_form_activity)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setSupportActionBar(toolbarCarEditForm)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbarCarEditForm.setNavigationOnClickListener {
            finish()
        }

        carUID = intent.getStringExtra("uid")
        bodyTypeSpinner = intent.getStringExtra("bodyType")
        fuelTypeSpinner = intent.getStringExtra("fuelType")
        Toast.makeText(this@CarEditForm, "Przesłano do edycji id $carUID", Toast.LENGTH_SHORT).show()

        spinnerBodyInit()
        spinnerFuelInit()
        loadCar()

        buttonEditAccept.setOnClickListener {
            dataVal()
        }
    }

    private fun spinnerFuelInit(){
        val fuelType = arrayOf("Benzyna", "Diesel", "LPG", "CNG")
        val arrayAdapter = ArrayAdapter(this, R.layout.spinner_item,fuelType)

        spinnerEditFuelType.adapter = arrayAdapter

        if(fuelTypeSpinner == "Benzyna"){
            spinnerEditFuelType.setSelection(0)
        }else if(fuelTypeSpinner == "Diesel"){
            spinnerEditFuelType.setSelection(1)
        }else if (fuelTypeSpinner == "LPG"){
            spinnerEditFuelType.setSelection(2)
        }else if (fuelTypeSpinner == "CNG"){
            spinnerEditFuelType.setSelection(3)
        }

        spinnerEditFuelType.onItemSelectedListener = object :
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

        spinnerEditBody.adapter = arrayAdapter

        if(bodyTypeSpinner == "Hatchback"){
            spinnerEditBody.setSelection(0)
        }else if(bodyTypeSpinner == "Kombi"){
            spinnerEditBody.setSelection(1)
        }else if (bodyTypeSpinner == "Sedan"){
            spinnerEditBody.setSelection(2)
        }else if (bodyTypeSpinner == "SUV"){
            spinnerEditBody.setSelection(3)
        }else if (bodyTypeSpinner == "Van"){
            spinnerEditBody.setSelection(4)
        }else if (bodyTypeSpinner == "Coupe"){
            spinnerEditBody.setSelection(5)
        }else if (bodyTypeSpinner == "Kabriolet"){
            spinnerEditBody.setSelection(6)
        }

        spinnerEditBody.onItemSelectedListener = object :
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

    private fun dataVal(){
        val pattern = Regex("^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))\$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$")
        val dateService = etEditService.text.toString()
        val dateInsurance = etEditInsurance.text.toString()
        val resultService = pattern.containsMatchIn(dateService)
        val resultInsurance = pattern.containsMatchIn(dateInsurance)

        if(resultService==false){
            etEditService.error="DD-MM-RRRR"
            etEditService.requestFocus()
        }else if(resultInsurance==false){
            etEditInsurance.error="DD-MM-RRRR"
            etEditInsurance.requestFocus()
        }else if(etEditBrand.text.toString().isEmpty()){
            etEditBrand.error="Uzupełnij to pole!"
            etEditBrand.requestFocus()
        }else if(etEditModel.text.toString().isEmpty()){
            etEditModel.error="To pole nie można pozostac puste!"
            etEditModel.requestFocus()
        }else if(etEditEngine.text.toString().isEmpty()){
            etEditEngine.error="Uzupełnij to pole!"
            etEditEngine.requestFocus()
        }else if(etEditMileage.text.toString().isEmpty()){
            etEditMileage.error="Uzupełnij to pole!"
            etEditMileage.requestFocus()
        }else if(etEditFuelTankCapacity.text.toString().isEmpty()){
            etEditFuelTankCapacity.error="Uzupełnij to pole!"
            etEditFuelTankCapacity.requestFocus()
        }else{
            validateDate()
        }
    }


    private fun validateDate(){
        var dateServiceFromForm = etEditService.text.toString()
        val secondCharService: Char = dateServiceFromForm.get(1)
        val fourthCharService: Char = dateServiceFromForm.get(3)
        val fifthCharService: Char = dateServiceFromForm.get(4)

        if(secondCharService == '-' && fourthCharService != '-'){                 //1-11-1990 nie moze byc na [1] -> wtedy 0 na [0]
            dateServiceFromForm = "0" + dateServiceFromForm.substring(0, dateServiceFromForm.length)
        }else if(fifthCharService == '-'){                                 //11-1-1990 nie moze byc na [4] -> wtedy 0 na [3]
            dateServiceFromForm = dateServiceFromForm.substring(0, 3) + "0" + dateServiceFromForm.substring(3, dateServiceFromForm.length)
        }else if(secondCharService == '-' && fourthCharService == '-'){            //1-1-1990 nie moze byc na [1][3] -> wtedy 0 na [0] i 0 na [3]
            dateServiceFromForm = "0" + dateServiceFromForm.substring(0, 2) + "0" + dateServiceFromForm.substring(2, dateServiceFromForm.length)
        }

        var dateInsuranceFromForm = etEditInsurance.text.toString()
        val secondCharInsurance: Char = dateInsuranceFromForm.get(1)
        val fourthCharInsurance: Char = dateInsuranceFromForm.get(3)
        val fifthCharInsurance: Char = dateInsuranceFromForm.get(4)

        if(secondCharInsurance == '-' && fourthCharInsurance != '-'){                 //1-11-1990 nie moze byc na [1] -> wtedy 0 na [0]
            dateInsuranceFromForm = "0" + dateInsuranceFromForm.substring(0, dateInsuranceFromForm.length)
        }else if(fifthCharInsurance == '-'){                                 //11-1-1990 nie moze byc na [4] -> wtedy 0 na [3]
            dateInsuranceFromForm = dateInsuranceFromForm.substring(0, 3) + "0" + dateInsuranceFromForm.substring(3, dateInsuranceFromForm.length)
        }else if(secondCharInsurance == '-' && fourthCharInsurance == '-'){            //1-1-1990 nie moze byc na [1][3] -> wtedy 0 na [0] i 0 na [3]
            dateInsuranceFromForm = "0" + dateInsuranceFromForm.substring(0, 2) + "0" + dateInsuranceFromForm.substring(2, dateInsuranceFromForm.length)
        }

        if(dateServiceFromForm.length!=10){
            etEditService.error="DD-MM-RRRR"
            etEditService.requestFocus()
        }else if(dateInsuranceFromForm.length!=10){
            etEditInsurance.error="DD-MM-RRRR"
            etEditInsurance.requestFocus()
        }else{
            val dateServiceToFormat = LocalDate.parse(dateServiceFromForm, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            val dateServiceAfterFormat = dateServiceToFormat.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond
            val dateInsuranceToFormat = LocalDate.parse(dateServiceFromForm, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            val dateInsuranceAfterFormat = dateInsuranceToFormat.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond
            editCar(dateServiceFromForm,dateInsuranceFromForm)
        }
    }

    private fun loadCar(){
        val query = carUID?.let { db.collection("cars").document(it) }
        if (query != null) {
            query.get().addOnSuccessListener { documentSnapshot ->
                val car = documentSnapshot.toObject<Car>()
                if (car != null) {
                    Toast.makeText(
                        this@CarEditForm,
                        "Odczytano ${car.carFuelTankCapacity}",
                        Toast.LENGTH_SHORT
                    ).show()
                    etEditBrand.setText(car.carBrand)
                    etEditModel.setText(car.carModel)
                    etEditMileage.setText(car.carMileage.toString())
                    etEditFuelTankCapacity.setText(car.carFuelTankCapacity.toString())
                    etEditEngine.setText(car.carEngine)
                    etEditInsurance.setText(car.carInsurance)
                    etEditService.setText(car.carService)
                }
            }
        }
    }

    private fun editCar(serviceDate: String, insuranceDate: String){
        carUID?.let {
            db.collection("cars").document(it)
                .update(mapOf(
                    "carBody" to bodyChosen,
                    "carBrand" to etEditBrand.text.toString(),
                    "carEngine" to etEditEngine.text.toString(),
                    "carFuelTankCapacity" to etEditFuelTankCapacity.text.toString().toInt(),
                    "carFuelType" to fuelChosen,
                    "carInsurance" to insuranceDate,
                    "carMileage" to etEditMileage.text.toString().toInt(),
                    "carModel" to etEditModel.text.toString(),
                    "carService" to serviceDate,
                ))
        }
        finish()
    }
}