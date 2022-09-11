package com.example.polkar.fuel

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.polkar.R
import com.example.polkar.data.Fuel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.fuel_row.view.*

class FuelAdapter(options: FirestoreRecyclerOptions<Fuel>) :
    FirestoreRecyclerAdapter<Fuel, FuelAdapter.FuelAdapterVH>(options) {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FuelAdapterVH {
        return FuelAdapterVH(LayoutInflater.from(parent.context).inflate(R.layout.fuel_row,parent,false),mListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FuelAdapterVH, position: Int, model: Fuel) {
        val tmpFuelAmount = model.fuelAmount
        val fuelAmountRounded = String.format("%.2f", tmpFuelAmount)
        holder.amount.text = fuelAmountRounded + " L"
        val tmpFuelAverage = model.fuelAverage
        val fuelAverageRounden = String.format("%.2f", tmpFuelAverage)
        holder.average.text = fuelAverageRounden + " L/100km"
        val tmpFuelSum = model.fuelSum
        val fuelSumRounded = String.format("%.2f", tmpFuelSum)
        holder.price.text = fuelSumRounded + " zł"
    }

    class FuelAdapterVH(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        var amount = itemView.tvFuelAddAmount
        var price = itemView.tvFuelAddPrice
        var average = itemView.tvFuelAddAverage

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

    }
}