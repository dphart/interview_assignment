package io.danielhartman.weedmaps.searchresults.data

import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient

open class LocationData(context: Context) {
    var lastLocation: Location? = null
    private var locationProvider = FusedLocationProviderClient(context)
    open fun getLatestLocation() {
        locationProvider.lastLocation.addOnCompleteListener {
            if (it.isSuccessful && it.result != null) {
                lastLocation = it.result
            }
        }
    }
}