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
            "tvOrlenBenzyna" -> tvOrlenBenzyna.setText("$averageRounded zł")
            "tvOrlenDiesel" -> tvOrlenDiesel.setText("$averageRounded zł")
            "tvOrlenLPG" -> tvOrlenLPG.setText("$averageRounded zł")
            "tvOrlenCNG" -> tvOrlenCNG.setText("$averageRounded zł")

            "tvBPBenzyna" -> tvBPBenzyna.setText("$averageRounded zł")
            "tvBPDiesel" -> tvBPDiesel.setText("$averageRounded zł")
            "tvBPLPG" -> tvBPLPG.setText("$averageRounded zł")
            "tvBPCNG" -> tvBPCNG.setText("$averageRounded zł")

            "tvShellBenzyna" -> tvShellBenzyna.setText("$averageRounded zł")
            "tvShellDiesel" -> tvShellDiesel.setText("$averageRounded zł")
            "tvShellLPG" -> tvShellLPG.setText("$averageRounded zł")
            "tvShellCNG" -> tvShellCNG.setText("$averageRounded zł")

            "tvCircleKBenzyna" -> tvCircleKBenzyna.setText("$averageRounded zł")
            "tvCircleKDiesel" -> tvCircleKDiesel.setText("$averageRounded zł")
            "tvCircleKLPG" -> tvCircleKLPG.setText("$averageRounded zł")
            "tvCircleKCNG" -> tvCircleKCNG.setText("$averageRounded zł")

            "tvAmicBenzyna" -> tvAmicBenzyna.setText("$averageRounded zł")
            "tvAmicDiesel" -> tvAmicDiesel.setText("$averageRounded zł")
            "tvAmicLPG" -> tvAmicLPG.setText("$averageRounded zł")
            "tvAmicCNG" -> tvAmicCNG.setText("$averageRounded zł")

            "tvMoyaBenzyna" -> tvMoyaBenzyna.setText("$averageRounded zł")
            "tvMoyaDiesel" -> tvMoyaDiesel.setText("$averageRounded zł")
            "tvMoyaLPG" -> tvMoyaLPG.setText("$averageRounded zł")
            "tvMoyaCNG" -> tvMoyaCNG.setText("$averageRounded zł")
        }
    }
}