package io.danielhartman.weedmaps.searchresults.data

import android.util.Log
import io.danielhartman.weedmaps.Error
import io.danielhartman.weedmaps.Result
import io.danielhartman.weedmaps.Success
import io.danielhartman.weedmaps.searchresults.model.LocationModel
import io.danielhartman.weedmaps.searchresults.model.SearchResultModel
import io.danielhartman.weedmaps.searchresults.network.SearchResultService
import io.danielhartman.weedmaps.searchresults.network.response.Business
import io.danielhartman.weedmaps.toResult
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

//Open For testing
open class SearchResultData(
    val searchText: String,
    val api: SearchResultService,
    val locationData: LocationData,
    var inMem: List<SearchResultModel> = listOf()

//In the future this would also include storage
) {

    suspend fun getSearchResultForPage(offset: Int): Result<List<SearchResultModel>> {
        val usedLocation =
            locationData.lastLocation?.let { LocationModel(it.latitude, it.longitude) }
                ?: locationData.defaultLocation
        Log.d("SearchResultData", "Using $usedLocation")
        return try {
            val response =
                api.getSearchResultsForTerm(
                    searchTerm = searchText,
                    offset = offset,
                    latitude = usedLocation.lat,
                    longitude = usedLocation.lon
                ).toResult()

            when (response) {
                is Success -> {
                    val res = getReviewsForBusinesses(response.data.businesses)!!
                    inMem = inMem + res
                    Success(inMem)
                }
                else -> Error(0, "uhoh")
            }

        } catch (e: Exception) {
            Error(0, "uhoh")
        }
    }


    private suspend fun getReviewsForBusiness(business: Business): Deferred<SearchResultModel> {
        return coroutineScope {
            async {
                try {
                    val reviews = api.getReviewsForAlias(business.alias).toResult().getOrError()
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