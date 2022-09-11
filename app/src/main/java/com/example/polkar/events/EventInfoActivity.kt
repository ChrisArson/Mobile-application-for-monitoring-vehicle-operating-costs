package com.example.polkar.events

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.polkar.R
import com.example.polkar.cars.CarEditForm
import com.example.polkar.data.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.event_add_form_activity.*
import kotlinx.android.synthetic.main.event_edit_form_activity.*
import kotlinx.android.synthetic.main.event_info_activity.*

class EventInfoActivity : AppCompatActivity() {
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var mAuth: FirebaseAuth;
    var eventUID: String? = null
    var eventType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_info_activity)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setSupportActionBar(toolbarEventAddForm)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        eventUID = intent.getStringExtra("uid")
        eventType = intent.getStringExtra("type")

        toolbarEventInfo.setNavigationOnClickListener {
            finish()
        }

        val builder = AlertDialog.Builder(this)
        buttonEventInfoDelete.setOnClickListener {
            builder.setTitle("Uwaga!")
                .setMessage("Na pewno chcesz usunąć zdarzenie?")
                .setCancelable(true)
                .setPositiveButton("Tak"){dialogInterface,it->
                    deleteEvent()
                }
                .setNegativeButton("Anuluj"){dialogInterface,it->
                    dialogInterface.cancel()
                }
                .show()
        }

        buttonEventInfoEdit.setOnClickListener {
            val intent = Intent(this@EventInfoActivity, EventEditForm::class.java)
            intent.putExtra("uid", eventUID)
            intent.putExtra("type",eventType)
            startActivity(intent)
            finish()
        }

        val query = eventUID?.let { db.collection("events").document(it) }
        if (query != null) {
            query.get().addOnSuccessListener { documentSnapshot ->
                val event = documentSnapshot.toObject<Event>()
                if (event != null) {
                    Toast.makeText(
                        this@EventInfoActivity,
                        "Odczytano ${event.eventDate}",
                        Toast.LENGTH_SHORT
                    ).show()
                    tvEventInfoDate.setText(event.eventDate)
                    tvEventInfoDesc.setText(event.eventDescription)
                    tvEventInfoType.setText(event.eventType)
                    tvEventInfoPrice.setText(event.eventPrice.toString())
                    tvEventInfoMileage.setText(event.eventMileage.toString())
                    if(event.eventReminder==true){
                        val mileageReminder = event.eventMileageReminder!! - event.eventMileage!!
                        tvEventInfoReminder.setText("Ustawiono")
                        tvEventInfoReminderMileage.setText(mileageReminder.toString())
                    }else{
                        tvEventInfoReminder.setText("Nie ustawiono")
                        tvEventInfoReminderMileage.setText("Nie dotyczy")
                        tvEventInfoReminderMileage.setTextColor(Color.parseColor("#808080"))
                    }
                }
            }
        }

    }

    private fun deleteEvent() {
        if (eventUID != null) {
            db.collection("events").document(eventUID!!)
                .delete()
                .addOnSuccessListener {
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this@EventInfoActivity,
                        "Wydarzenie nie zostało usunięte",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}