package io.danielhartman.weedmaps.searchresults.data

import androidx.lifecycle.MutableLiveData
import io.danielhartman.weedmaps.searchresults.model.SearchResultModel
import io.danielhartman.weedmaps.searchresults.network.SearchResultService
import io.danielhartman.weedmaps.searchresults.network.response.Business

//Open For testing
open class SearchResultData(
    val searchText: String,
    val api: SearchResultService
//In the future this would also include storage
) {
    var offset = 0
    val limit = 5

    //In Memory Cache
    var data: MutableLiveData<List<SearchResultModel>> =
        MutableLiveData<List<SearchResultModel>>().apply {
            postValue(
                listOf()
            )
        }

    open suspend fun getAllSearchResultsAndMoveToNextPage(): List<SearchResultModel> {
        return try {
            val res =
                api.getSearchResultsForTerm(searchText, offset).businesses.getReviewsForBusinesses()
            data.postValue(data.value!! + res!!)
            offset += limit
            res
        } catch (e: Exception) {
            listOf()
        }

    }

    suspend fun List<Business>.getReviewsForBusinesses(): List<SearchResultModel>? {
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