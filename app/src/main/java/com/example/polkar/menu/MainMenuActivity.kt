package com.example.polkar.menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.polkar.R
import com.example.polkar.events.EventListActivity
import com.example.polkar.statistics.CarStatisticsSelection
import com.example.polkar.statistics.FuelStationStatistics
import com.example.polkar.statistics.GlobalStatisticsBrandSelection
import com.example.polkar.statistics.UserStatistics
import com.example.polkar.userauth.SettingsActivity
import kotlinx.android.synthetic.main.main_menu_activity.*

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu_activity)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()

        buttonCars.setOnClickListener {
            finish()
        }
        buttonEventList.setOnClickListener {
            startActivity(Intent(this, EventListActivity::class.java))
            finish()
        }
        buttonFuelStationStatistics.setOnClickListener {
            startActivity(Intent(this, FuelStationStatistics::class.java))
            finish()
        }
        buttonSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
        }
        buttonUserStats.setOnClickListener {
            startActivity(Intent(this, UserStatistics::class.java))
            finish()
        }
        buttonCarStats.setOnClickListener {
            startActivity(Intent(this, CarStatisticsSelection::class.java))
            finish()
        }
        buttonGlobalStatistics.setOnClickListener {
            startActivity(Intent(this, GlobalStatisticsBrandSelection::class.java))
            finish()
        }
    }

}