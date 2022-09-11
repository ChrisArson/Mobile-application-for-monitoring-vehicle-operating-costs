package com.example.polkar.events

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.polkar.R
import com.example.polkar.cars.EventsAdapter
import com.example.polkar.data.Event
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.event_add_form_activity.*
import kotlinx.android.synthetic.main.event_list_activity.*

class EventListActivity : AppCompatActivity() {
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var mAuth: FirebaseAuth;
    var eventsAdapter: EventsAdapter? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_list_activity)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setSupportActionBar(toolbarEventList)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbarEventList.setNavigationOnClickListener {
            finish()
        }

        setUpRecyclerview()
    }
    private fun setUpRecyclerview(){
        mAuth = Firebase.auth
        val uid = mAuth.currentUser!!.uid

        val query = db.collection("events").whereEqualTo("eventUserUID", uid).orderBy("time", Query.Direction.DESCENDING)
        query.get().addOnCompleteListener { task ->
            val b = task.result!!.isEmpty()
            if(b){
                Toast.makeText(this, "Brak wydarzeń", Toast.LENGTH_SHORT).show()
            }
        }
        val firestoreRecyclerOptions: FirestoreRecyclerOptions<Event> = FirestoreRecyclerOptions.Builder<Event>()
            .setQuery(query, Event::class.java)
            .build()

        eventsAdapter = EventsAdapter(firestoreRecyclerOptions)

        recycler3.layoutManager = LinearLayoutManager(this)
        recycler3.adapter = eventsAdapter

        eventsAdapter!!.setOnItemClickListener(object : EventsAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val uid = eventsAdapter!!.getItem(position).UID
                val type = eventsAdapter!!.getItem(position).eventType
                val intent = Intent(this@EventListActivity, EventInfoActivity::class.java)
                intent.putExtra("uid",uid)
                intent.putExtra("type",type)
                startActivity(intent)
            }
        })

    }

    override fun onStart() {
        super.onStart()
        eventsAdapter!!.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventsAdapter!!.stopListening()
    }
}