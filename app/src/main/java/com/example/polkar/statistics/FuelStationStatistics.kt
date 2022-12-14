package com.example.polkar.statistics

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.polkar.R
import com.example.polkar.data.FuelStation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fuel_station_statistics_activity.*

class FuelStationStatistics : AppCompatActivity() {
    val db = Firebase.firestore
    private lateinit var mAuth: FirebaseAuth

    val stations = arrayOf("Orlen", "BP", "Shell", "CircleK", "Amic", "Moya")
    val fuelTypes = arrayOf("Benzyna", "Diesel", "LPG", "CNG")
    var fuelStations = mutableListOf<FuelStation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fuel_station_statistics_activity)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setSupportActionBar(toolbarFuelStationStatistics)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbarFuelStationStatistics.setNavigationOnClickListener {
            finish()
        }

        fillStations()
        for (station in stations) {
            for (fuelType in fuelTypes) {
                loadData(station, fuelType)
            }
        }

    }

    private fun fillStations(){

        for (station in stations) {
            val newStation = FuelStation( station,  0.0f,  0.0f,  0.0f,  0.0f)
            fuelStations.add(newStation)
        }
    }

    private fun loadData(station:String, fuelType:String) {
        val prices = mutableListOf<Float>()

        db.collection("fuel").whereEqualTo("fuelStation", station).whereEqualTo("fuelType", fuelType)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val price = document.getDouble("fuelPrice")?.toFloat()
                    if (price != null) {
                        prices.add(price)
                    }
                }
                addData(prices, station, fuelType)
            }
    }

    private fun addData(prices: List<Float>, station:String, fuelType:String){
        for (n in 0..fuelStations.count() - 1) {
            if (fuelStations[n].name == station){
                var average: Float
                var sum = 0.0f
                for (price in prices) {
                    sum += price
                }
                if(prices.count() != 0) {
                    average = sum / prices.count()

                    when(fuelType){
                        "Benzyna" -> fuelStations[n].pb = average
                        "Diesel" -> fuelStations[n].on = average
                        "LPG" -> fuelStations[n].lpg = average
                        "CNG" -> fuelStations[n].cng = average
                    }
                    displayData(station,fuelType,average)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayData(station: String, fuelType:String, average: Float){
        val tmpTextView = "tv$station$fuelType"
        val averageRounded = String.format("%.2f", average)

        when(tmpTextView){
            "tvOrlenBenzyna" -> tvOrlenBenzyna.setText("$averageRounded z??")
            "tvOrlenDiesel" -> tvOrlenDiesel.setText("$averageRounded z??")
            "tvOrlenLPG" -> tvOrlenLPG.setText("$averageRounded z??")
            "tvOrlenCNG" -> tvOrlenCNG.setText("$averageRounded z??")

            "tvBPBenzyna" -> tvBPBenzyna.setText("$averageRounded z??")
            "tvBPDiesel" -> tvBPDiesel.setText("$averageRounded z??")
            "tvBPLPG" -> tvBPLPG.setText("$averageRounded z??")
            "tvBPCNG" -> tvBPCNG.setText("$averageRounded z??")

            "tvShellBenzyna" -> tvShellBenzyna.setText("$averageRounded z??")
            "tvShellDiesel" -> tvShellDiesel.setText("$averageRounded z??")
            "tvShellLPG" -> tvShellLPG.setText("$averageRounded z??")
            "tvShellCNG" -> tvShellCNG.setText("$averageRounded z??")

            "tvCircleKBenzyna" -> tvCircleKBenzyna.setText("$averageRounded z??")
            "tvCircleKDiesel" -> tvCircleKDiesel.setText("$averageRounded z??")
            "tvCircleKLPG" -> tvCircleKLPG.setText("$averageRounded z??")
            "tvCircleKCNG" -> tvCircleKCNG.setText("$averageRounded z??")

            "tvAmicBenzyna" -> tvAmicBenzyna.setText("$averageRounded z??")
            "tvAmicDiesel" -> tvAmicDiesel.setText("$averageRounded z??")
            "tvAmicLPG" -> tvAmicLPG.setText("$averageRounded z??")
            "tvAmicCNG" -> tvAmicCNG.setText("$averageRounded z??")

            "tvMoyaBenzyna" -> tvMoyaBenzyna.setText("$averageRounded z??")
            "tvMoyaDiesel" -> tvMoyaDiesel.setText("$averageRounded z??")
            "tvMoyaLPG" -> tvMoyaLPG.setText("$averageRounded z??")
            "tvMoyaCNG" -> tvMoyaCNG.setText("$averageRounded z??")
        }
    }
}