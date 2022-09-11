package com.example.polkar.statistics

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.polkar.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.global_statistics_brand_selection.*
import kotlinx.android.synthetic.main.main_panel_activity.*


class GlobalStatisticsBrandSelection : AppCompatActivity() {
    val db = Firebase.firestore
    var brand: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.global_statistics_brand_selection)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setSupportActionBar(toolbarGlobalStatisticsBrandSelection)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbarGlobalStatisticsBrandSelection.setNavigationOnClickListener {
            finish()
        }
        loadData()
    }

    private fun loadData() {
        val events = mutableListOf<String>()
        db.collection("events").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val brand = document.getString("eventBrand")
                    if (brand != null) {
                        events.add(brand)
                    }
                }
                val setData = events.toMutableSet()
                val brandArray = setData.toTypedArray()
                setUpRecyclerview(brandArray)
            }
    }
    private fun setUpRecyclerview(array: Array<String>){
        recyclerBrandStatistics.layoutManager = LinearLayoutManager(this)

        val adapter = BrandModelSelectionAdapter(array)
        recyclerBrandStatistics.adapter = adapter
        adapter.setOnItemClickListener(object : BrandModelSelectionAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val carBrand = array[position]
                val intent = Intent(this@GlobalStatisticsBrandSelection, GlobalStatisticsModelSelection::class.java)
                intent.putExtra("carBrand",carBrand)
                startActivity(intent)
            }
        })
    }
}