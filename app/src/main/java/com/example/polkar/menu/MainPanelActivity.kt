package com.example.polkar.menu

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.polkar.R
import com.example.polkar.cars.CarAddForm
import com.example.polkar.cars.CarInfoActivity
import com.example.polkar.cars.CarsAdapter
import com.example.polkar.data.Car
import com.example.polkar.userauth.LoginActivity
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fuel_add_form_activity.*
import kotlinx.android.synthetic.main.main_panel_activity.*

class MainPanelActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    val db = Firebase.firestore
    var carsAdapter: CarsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_panel_activity)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setSupportActionBar(toolbarMainPanel)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpRecyclerview()

        mAuth = FirebaseAuth.getInstance()
        getUserData()

        toolbarMainPanel.setNavigationOnClickListener {
            startActivity(Intent(this, MainMenuActivity::class.java))
        }
        buttonDodawanie.setOnClickListener {
            startActivity(Intent(this, CarAddForm::class.java ))
        }
    }

    private fun getUserData(){
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(currentUser : FirebaseUser?) {
        if(currentUser==null){
            startActivity(Intent(this, LoginActivity::class.java))
            Toast.makeText(baseContext, "Nie jestes zalogowany", Toast.LENGTH_SHORT).show()
        }else{
            val userUID = mAuth.currentUser!!.uid
            db.collection("users").document(userUID).get().addOnSuccessListener { documents ->
                val name = documents.getString("userName")
                toolbarMainPanel.setTitle("Witaj "+name)
            }
        }
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity()
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Kliknij dwa razy, aby wyjść.", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

    private fun setUpRecyclerview(){
        mAuth = Firebase.auth
        val uid = mAuth.currentUser!!.uid
        val query = db.collection("cars").whereEqualTo("userUID", uid).orderBy("time", Query.Direction.ASCENDING)
        query.get().addOnCompleteListener { task ->
            val b = task.result!!.isEmpty()
            if(b){
                tvEmptyCarList.visibility = View.VISIBLE
            }

        }

        val firestoreRecyclerOptions: FirestoreRecyclerOptions<Car> = FirestoreRecyclerOptions.Builder<Car>()
            .setQuery(query, Car::class.java).build()

        carsAdapter = CarsAdapter(firestoreRecyclerOptions)
        recyclerMainPanel.layoutManager = LinearLayoutManager(this)
        recyclerMainPanel.adapter = carsAdapter

        carsAdapter!!.setOnItemClickListener(object : CarsAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val carUID = carsAdapter!!.getItem(position).UID
                val carMileage = carsAdapter!!.getItem(position).carMileage.toString()
                val typeBody = carsAdapter!!.getItem(position).carBody
                val typeFuel = carsAdapter!!.getItem(position).carFuelType
                val carBrand = carsAdapter!!.getItem(position).carBrand
                val carModel = carsAdapter!!.getItem(position).carModel
                val carInsurance = carsAdapter!!.getItem(position).carInsurance
                val carService = carsAdapter!!.getItem(position).carService
                val intent = Intent(this@MainPanelActivity, CarInfoActivity::class.java)
                intent.putExtra("insurance",carInsurance)
                intent.putExtra("service",carService)
                intent.putExtra("bodyType",typeBody)
                intent.putExtra("fuelType",typeFuel)
                intent.putExtra("mileage",carMileage)
                intent.putExtra("brand",carBrand)
                intent.putExtra("model",carModel)
                intent.putExtra("uid",carUID)
                startActivity(intent)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        mAuth = Firebase.auth
        val uid = mAuth.currentUser!!.uid
        val query = db.collection("cars").whereEqualTo("userUID", uid)
            .orderBy("time", Query.Direction.ASCENDING)
        query.get().addOnCompleteListener { task ->
            val b = task.result!!.isEmpty()
            if (b) {
                tvEmptyCarList.visibility = View.VISIBLE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        carsAdapter!!.startListening()
        val currentUser: FirebaseUser? = mAuth.getCurrentUser()
        updateUI(currentUser)
    }

    override fun onDestroy() {
        super.onDestroy()
        carsAdapter!!.stopListening()
    }

}