package com.example.polkar.data

data class Event(val UID: String? = null,
                 val eventCarUID: String? = null,
                 val eventUserUID: String? = null,
                 val eventDate: String? = null,
                 val eventDescription: String? = null,
                 val eventType: String? = null,
                 val eventMileage: Int? = null,
                 val eventPrice: Int? = null,
                 val time: Long? = null,
                 val eventBrand: String? = null,
                 val eventModel: String? = null,
                 val eventReminder: Boolean? = null,
                 val eventMileageReminder: Int? = null)

