package io.danielhartman.weedmaps.searchresults.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.danielhartman.weedmaps.searchresults.model.LocationModel
import io.danielhartman.weedmaps.searchresults.model.SearchResultModel
import io.danielhartman.weedmaps.searchresults.network.SearchResultService
import io.danielhartman.weedmaps.searchresults.network.response.Business

//Open For testing
open class SearchResultData(
    val searchText: String,
    val api: SearchResultService,
    val locationData: LocationData
//In the future this would also include storage
) {
    var offset = 0
    val limit = 5
    val defaultLocation = LocationModel(37.786882, 122.399972)
    var usedLocation:LocationModel? = null

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
            val res =
                api.getSearchResultsForTerm(searchText, offset, usedLocation!!.lat, usedLocation!!.lon).businesses.getReviewsForBusinesses()
            data.postValue(data.value!! + res!!)
            offset += limit
            res
        } catch (e: Exception) {
            listOf()
        }

    }

    open suspend fun List<Business>.getReviewsForBusinesses(): List<SearchResultModel>? {
        val list = ArrayList<SearchResultModel>()
        this.forEach {
            try {
                val reviews = api.getReviewsForAlias(it.alias)
                list.add(
                    SearchResultModel(
                        it.name, it.image_url,
                        reviews.reviews.maxBy { it.rating }!!.text, it.alias
                    )
                )
            } catch (e: Exception) {
                list.add(
                    SearchResultModel(
                        it.name,
                        it.image_url,
                        "Review Not found",
                        it.alias
                    )
                )
            }
        }
        return list
    }
}