package com.example.polkar.cars

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.polkar.R
import com.example.polkar.data.Event
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.car_info_event_row.view.*
import kotlinx.android.synthetic.main.events_row.view.*

class CarInfoEventAdapter(options: FirestoreRecyclerOptions<Event>) :
    FirestoreRecyclerAdapter<Event, CarInfoEventAdapter.CarInfoEventAdapterVH>(options) {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarInfoEventAdapterVH {
        return CarInfoEventAdapterVH(LayoutInflater.from(parent.context).inflate(R.layout.car_info_event_row,parent,false),mListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CarInfoEventAdapterVH, position: Int, model: Event) {
        holder.date.text = model.eventDate
        holder.type.text = model.eventType
        holder.price.text = model.eventPrice.toString() + " zł"
    }

    class CarInfoEventAdapterVH(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        var date = itemView.carInfoEventsRowDate
        var type = itemView.carInfoEventsRowType1
        var price = itemView.carInfoEventsRowPrice

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

    }
}