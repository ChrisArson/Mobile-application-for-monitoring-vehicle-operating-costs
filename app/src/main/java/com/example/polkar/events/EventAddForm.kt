package com.example.polkar.events

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.polkar.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.event_add_form_activity.*
import kotlinx.android.synthetic.main.fuel_add_form_activity.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class EventAddForm : AppCompatActivity() {
    val db = Firebase.firestore
    private lateinit var mAuth: FirebaseAuth;
    lateinit var eventAddFormType: String
    var carUID: String? = null
    var carModel: String? = null
    var carBrand: String? = null
    var eventReminder = false
    var eventMilReminder = 0
    var mileage = 0
    var carMileage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_add_form_activity)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setSupportActionBar(toolbarEventAddForm)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbarEventAddForm.setNavigationOnClickListener {
            finish()
        }
        mAuth = Firebase.auth
        carUID = intent.getStringExtra("uid")
        carModel = intent.getStringExtra("model")
        carBrand = intent.getStringExtra("brand")
        carMileage = intent.getStringExtra("mileage")?.toInt()!!

        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        eventAddDate.setText(currentDate)
        dateCheck()
        spinnerInit()

        eventAddFormButton.setOnClickListener {
            dataVal()
        }

        switchEventAddForm.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                textView48.visibility = View.VISIBLE
                eventAddMileageReminder.visibility = View.VISIBLE
                eventReminder = true
            }else{
                textView48.visibility = View.INVISIBLE
                eventAddMileageReminder.visibility = View.INVISIBLE
                eventReminder = false
            }
        }
    }


    private fun spinnerInit(){
        val eventType = arrayOf("Naprawa", "Wymiana cześci eksploatacyjnych", "Wymiana oleji i filtrów", "Inne")
        val arrayAdapter = ArrayAdapter(this, R.layout.spinner_item,eventType)

        spinnerEventAddForm.adapter = arrayAdapter
        spinnerEventAddForm.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                eventAddFormType = eventType[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                eventAddFormType = eventType[0]
            }
        }
    }

    private fun dateCheck(){
        eventAddDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                dateVal()
            }
        })
    }

    private fun dateVal(){
        val pattern = Regex("^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))\$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$")
        val date = eventAddDate.text.toString()
        val result = pattern.containsMatchIn(date)
        if(result==false){
            eventAddDate.error="DD-MM-RRRR"
            eventAddDate.requestFocus()
        }
    }

    private fun dataVal(){
        val pattern = Regex("^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))\$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$")
        val date = eventAddDate.text.toString()
        val result = pattern.containsMatchIn(date)

        if(result==false){
            eventAddDate.error="DD-MM-RRRR"
            eventAddDate.requestFocus()
        }else if(eventAddDesc.text.toString().isEmpty()){
            eventAddDesc.error="Uzupełnij to pole!"
            eventAddDesc.requestFocus()
        }else if(eventAddPrice.text.toString().isEmpty()){
            eventAddPrice.error="To pole nie można pozostac puste!"
            eventAddPrice.requestFocus()
        }else if(eventAddMileage.text.toString().isEmpty()){
            eventAddMileage.error="Uzupełnij to pole!"
            eventAddMileage.requestFocus()
        }else{
            validateDate()
        }
    }



    private fun validateDate(){
        var dateFromForm = eventAddDate.text.toString()
        val secondChar: Char = dateFromForm.get(1)
        val fourthChar: Char = dateFromForm.get(3)
        val fifthChar: Char = dateFromForm.get(4)

        if(secondChar == '-' && fourthChar != '-'){                 //1-11-1990 nie moze byc na [1] -> wtedy 0 na [0]
            dateFromForm = "0" + dateFromForm.substring(0, dateFromForm.length)
        }else if(fifthChar == '-'){                                 //11-1-1990 nie moze byc na [4] -> wtedy 0 na [3]
            dateFromForm = dateFromForm.substring(0, 3) + "0" + dateFromForm.substring(3, dateFromForm.length)
        }else if(secondChar == '-' && fourthChar == '-'){            //1-1-1990 nie moze byc na [1][3] -> wtedy 0 na [0] i 0 na [3]
            dateFromForm = "0" + dateFromForm.substring(0, 2) + "0" + dateFromForm.substring(2, dateFromForm.length)
        }

        if(dateFromForm.length==10){
            val dateToFormat = LocalDate.parse(dateFromForm, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            val dateAfterFormat = dateToFormat.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond

            addEvent(dateAfterFormat,dateFromForm)
        }else{
            eventAddDate.error="DD-MM-RRRR"
            eventAddDate.requestFocus()
        }
    }

    private fun addEvent(time: Long, date: String){
        val userUID = mAuth.currentUser!!.uid
        if(eventReminder==true){
            eventMilReminder = eventAddMileageReminder.text.toString().toInt() + eventAddMileage.text.toString().toInt()
        }else{
            eventMilReminder = 0
        }

        val event = hashMapOf(
            "UID" to "",
            "eventCarUID" to carUID,
            "eventUserUID" to userUID,
            "eventDate" to date,
            "eventDescription" to eventAddDesc.text.toString(),
            "eventType" to eventAddFormType,
            "eventMileage" to eventAddMileage.text.toString().toInt(),
            "eventPrice" to eventAddPrice.text.toString().toInt(),
            "time" to time,
            "eventBrand" to carBrand,
            "eventModel" to carModel,
            "eventReminder" to eventReminder,
            "eventMileageReminder" to eventMilReminder
        )
        db.collection("events")
            .add(event)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                val eventAdded = db.collection("events").document(documentReference.id)
                eventAdded.update("UID", documentReference.id)
                updateCarMileage()
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }

    private fun updateCarMileage() {
        mileage = eventAddMileage.text.toString().toInt()
        val carRef = carUID?.let { db.collection("cars").document(it) }

        if(mileage > carMileage){
            if (carRef != null) {
                carRef
                    .update("carMileage", mileage)
                    .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
            }
        }
        finish()
    }
}


