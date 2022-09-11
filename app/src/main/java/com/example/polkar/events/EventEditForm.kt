package com.example.polkar.events

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.polkar.R
import com.example.polkar.data.Car
import com.example.polkar.data.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.car_edit_form_activity.*
import kotlinx.android.synthetic.main.event_add_form_activity.*
import kotlinx.android.synthetic.main.event_edit_form_activity.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class EventEditForm : AppCompatActivity() {
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var mAuth: FirebaseAuth;
    var eventUID: String? = null
    lateinit var eventEditFormType: String
    var eventType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_edit_form_activity)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setSupportActionBar(toolbarEventEditForm)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbarEventEditForm.setNavigationOnClickListener {
            finish()
        }

        eventUID = intent.getStringExtra("uid")
        eventType = intent.getStringExtra("type")
        Toast.makeText(this@EventEditForm, "Przesłano do edycji id $eventUID", Toast.LENGTH_SHORT).show()

        val query = eventUID?.let { db.collection("events").document(it) }
        if (query != null) {
            query.get().addOnSuccessListener { documentSnapshot ->
                val event = documentSnapshot.toObject<Event>()
                if (event != null) {
                    Toast.makeText(
                        this@EventEditForm,
                        "Odczytano ${event.eventDate}",
                        Toast.LENGTH_SHORT
                    ).show()
                    eventEditDate.setText(event.eventDate)
                    eventEditDesc.setText(event.eventDescription)
                    eventEditPrice.setText((event.eventPrice.toString()))
                    eventEditMileage.setText((event.eventMileage.toString()))
                }
            }
        }
        spinnerInit()
        dateCheck()

        eventEditFormButton.setOnClickListener {
            validateDate()
        }

    }
    private fun dateVal(){
        val pattern = Regex("^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))\$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$")
        val date = eventEditDate.text.toString()
        val result = pattern.containsMatchIn(date)

        if(result==false){
            eventEditDate.error="DD-MM-RRRR"
            eventEditDate.requestFocus()
        }
    }

    private fun dateCheck(){
        eventEditDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                dateVal()
            }
        })
    }

    private fun validateDate(){
        var dateFromForm = eventEditDate.text.toString()
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

            editEvent(dateAfterFormat,dateFromForm)
        }else{
            eventEditDate.error="DD-MM-RRRR"
            eventEditDate.requestFocus()
        }
    }

    private fun spinnerInit(){
        val eventTypeArray = arrayOf("Naprawa", "Wymiana czesci eksploatacyjnych", "Wymiana oleji i filtrow", "Inne")
        val arrayAdapter = ArrayAdapter(this, R.layout.spinner_item,eventTypeArray)

        spinner2.adapter = arrayAdapter

        if(eventType == "Naprawa"){
            spinner2.setSelection(0)
        }else if(eventType == "Wymiana czesci eksploatacyjnych"){
            spinner2.setSelection(1)
        }else if (eventType == "Wymiana oleji i filtrow"){
            spinner2.setSelection(2)
        }else if (eventType == "Inne"){
            spinner2.setSelection(3)
        }

        spinner2.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                eventEditFormType = eventTypeArray[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                eventEditFormType = eventTypeArray[2]
                spinner2.setSelection(2)
            }
        }

    }

    private fun editEvent(time: Long,date: String){
        eventUID?.let {
            db.collection("events").document(it)
                .update(mapOf(
                    "eventDate" to date,
                    "eventDescription" to eventEditDesc.text.toString(),
                    "eventType" to eventEditFormType,
                    "eventMileage" to eventEditMileage.text.toString().toInt(),
                    "eventPrice" to eventEditPrice.text.toString().toInt(),
                    "time" to time
                ))
        }
        finish()
    }

}