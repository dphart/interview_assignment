package io.danielhartman.weedmaps.searchresults.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.danielhartman.weedmaps.searchresults.model.LocationModel
import io.danielhartman.weedmaps.searchresults.model.SearchResultModel
import io.danielhartman.weedmaps.searchresults.network.SearchResultService
import io.danielhartman.weedmaps.searchresults.network.response.Business
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

//Open For testing
open class SearchResultData(
    val searchText: String,
    val api: SearchResultService,
    val locationData: LocationData
//In the future this would also include storage
) {
    var offset = 0
    val limit = 5
    val defaultLocation = LocationModel(38.846464000000005, -77.1325952)
    var usedLocation: LocationModel? = null

    //In Memory Cache
    var data: MutableLiveData<List<SearchResultModel>> =
        MutableLiveData<List<SearchResultModel>>().apply {
            postValue(
                listOf()
            )
        }

    open suspend fun getAllSearchResultsAndMoveToNextPage(): List<SearchResultModel> {
        //We are going to only make requests off of the location the user had when we make the first request. Otherwise paging could get confused and show a dup.
        if (usedLocation == null) {
            usedLocation =
                locationData.lastLocation?.let { LocationModel(it.latitude, it.longitude) }
                    ?: defaultLocation
        }
        Log.d("SearchResultData", "Using $usedLocation")
        return try {
            val businesses =
                api.getSearchResultsForTerm(
                    searchTerm = searchText,
                    offset = offset,
                    latitude = usedLocation!!.lat,
                    longitude = usedLocation!!.lon
                ).businesses
            val response = getReviewsForBusinesses(businesses)
            data.postValue(data.value!! + response!!)
            offset += limit
            response
        } catch (e: Exception) {
            listOf()
        }

    }

    suspend fun getReviewsForBusiness(business: Business): Deferred<SearchResultModel> {
        return coroutineScope {
            async {
                try {
                    val reviews = api.getReviewsForAlias(business.alias)
                    SearchResultModel(
                        business.name, business.image_url,
                        reviews.reviews.maxBy { it.rating }!!.text, business.alias
                    )
                } catch (e: Exception) {
                    SearchResultModel(
                        business.name,
                        business.image_url,
                        "Review Not found",
                        business.alias
                    )
                }
            }
        }
    }

    open suspend fun getReviewsForBusinesses(businesses: List<Business>): List<SearchResultModel>? {
        return businesses.map { coroutineScope { getReviewsForBusiness(it) } }.map { it.await() }
    }
}