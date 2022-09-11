package com.example.polkar.statistics

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.polkar.R
import com.example.polkar.cars.CarsAdapter
import com.example.polkar.data.Car
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.car_statistics_selection_activity.*

class CarStatisticsSelection : AppCompatActivity() {
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var mAuth: FirebaseAuth;
    var carsAdapter: CarsAdapter? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.car_statistics_selection_activity)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setSupportActionBar(toolbarCarStatisticsSelection)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mAuth = Firebase.auth
        setUpRecyclerview()

        toolbarCarStatisticsSelection.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setUpRecyclerview(){
        val uid = mAuth.currentUser!!.uid

        val query = db.collection("cars").whereEqualTo("userUID", uid).orderBy("time", Query.Direction.ASCENDING)
        query.get().addOnCompleteListener { task ->
            val b = task.result!!.isEmpty()
            if(b){
                tvEmptyCars.visibility = View.VISIBLE
            }
        }
        val firestoreRecyclerOptions: FirestoreRecyclerOptions<Car> = FirestoreRecyclerOptions.Builder<Car>()
            .setQuery(query, Car::class.java)
            .build()

        carsAdapter = CarsAdapter(firestoreRecyclerOptions)

        recyclerCarStatisticsSelection.layoutManager = LinearLayoutManager(this)
        recyclerCarStatisticsSelection.adapter = carsAdapter

        carsAdapter!!.setOnItemClickListener(object : CarsAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val carBrand = carsAdapter!!.getItem(position).carBrand
                val carModel = carsAdapter!!.getItem(position).carModel
                val carUID = carsAdapter!!.getItem(position).UID
                val carMileage = carsAdapter!!.getItem(position).carMileage.toString()
                val intent = Intent(this@CarStatisticsSelection, CarStatistics::class.java)
                intent.putExtra("model",carModel)
                intent.putExtra("brand",carBrand)
                intent.putExtra("mileage",carMileage)
                intent.putExtra("uid",carUID)
                startActivity(intent)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        carsAdapter!!.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        carsAdapter!!.stopListening()
    }

}