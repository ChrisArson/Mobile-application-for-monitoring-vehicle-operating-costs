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
import kotlinx.android.synthetic.main.events_row.view.*

class EventsAdapter(options: FirestoreRecyclerOptions<Event>) :
    FirestoreRecyclerAdapter<Event, EventsAdapter.EventsAdapterVH>(options) {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsAdapterVH {
        return EventsAdapterVH(LayoutInflater.from(parent.context).inflate(R.layout.events_row,parent,false),mListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: EventsAdapterVH, position: Int, model: Event) {
        holder.date.text = model.eventDate
        holder.type.text = model.eventType
        holder.price.text = model.eventPrice.toString() + " zł"
        holder.name.text = model.eventBrand + " " + model.eventModel
    }

    class EventsAdapterVH(itemView: View,listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        var date = itemView.eventsRowDate
        var type = itemView.eventsRowType
        var price = itemView.eventsRowPrice
        var name = itemView.eventsCarName

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

    }
}