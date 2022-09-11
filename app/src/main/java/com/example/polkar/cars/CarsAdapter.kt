package com.example.polkar.cars

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.polkar.R
import com.example.polkar.data.Car
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.cars_row.view.*

class CarsAdapter(options: FirestoreRecyclerOptions<Car>) :
    FirestoreRecyclerAdapter<Car, CarsAdapter.CarsAdapterVH>(options) {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarsAdapterVH {
        return CarsAdapterVH(LayoutInflater.from(parent.context).inflate(R.layout.cars_row,parent,false),mListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CarsAdapterVH, position: Int, model: Car) {
        holder.mileage.text = model.carMileage.toString() + " km"
        holder.brand.text = model.carBrand +" " + model.carModel
    }

    class CarsAdapterVH(itemView: View,listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        var mileage = itemView.carsRowMileage
        var brand = itemView.carsRowBrandModel

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

    }
}