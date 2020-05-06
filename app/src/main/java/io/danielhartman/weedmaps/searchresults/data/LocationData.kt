package io.danielhartman.weedmaps.searchresults.data

import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import io.danielhartman.weedmaps.searchresults.model.LocationModel

open class LocationData(context: Context) {
    var lastLocation: Location? = null
    val defaultLocation = LocationModel(38.846464000000005, -77.1325952)

    private var locationProvider = FusedLocationProviderClient(context)
    open fun getLatestLocation() {
        locationProvider.lastLocation.addOnCompleteListener {
            if (it.isSuccessful && it.result != null) {
                lastLocation = it.result
            }
        }
    }
}