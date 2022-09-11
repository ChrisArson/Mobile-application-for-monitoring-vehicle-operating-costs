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
import kotlinx.android.synthetic.main.global_statistics_model_selection.*

class GlobalStatisticsModelSelection : AppCompatActivity() {
    var carBrand: String? = null
    val db = Firebase.firestore
    var model: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.global_statistics_model_selection)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setSupportActionBar(toolbarGlobalStatisticsModelSelection)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        carBrand = intent.getStringExtra("carBrand")

        toolbarGlobalStatisticsModelSelection.setNavigationOnClickListener {
            finish()
        }
        loadData()
    }

    private fun loadData() {
        val events = mutableListOf<String>()
        db.collection("events").whereEqualTo("eventBrand",carBrand).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val model = document.getString("eventModel")
                    if (model != null) {
                        events.add(model)
                    }
                }
                val setData = events.toMutableSet()
                val modelArray = setData.toTypedArray()
                setUpRecyclerview(modelArray)
            }
    }
    private fun setUpRecyclerview(array: Array<String>){
        recyclerModelStatistics.layoutManager = LinearLayoutManager(this)
        val adapter = BrandModelSelectionAdapter(array)
        recyclerModelStatistics.adapter = adapter
        adapter.setOnItemClickListener(object : BrandModelSelectionAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val intent = Intent(this@GlobalStatisticsModelSelection, GlobalStatistics::class.java)
                intent.putExtra("carBrand",carBrand)
                intent.putExtra("carModel",array[position])
                startActivity(intent)
            }
        })
    }
}