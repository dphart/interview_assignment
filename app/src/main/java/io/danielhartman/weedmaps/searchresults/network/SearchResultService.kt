package io.danielhartman.weedmaps.searchresults.network

import io.danielhartman.weedmaps.searchresults.network.response.SearchResultReview
import io.danielhartman.weedmaps.searchresults.network.response.SearchResults
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SearchResultService {
    @GET("v3/businesses/{alias}/reviews")
    suspend fun getReviewsForAlias(@Path("alias") alias:String): SearchResultReview

   @GET("v3/businesses/search?latitude=37.786882&longitude=-122.399972&limit=5")
   suspend fun getSearchResultsForTerm(@Query("term") searchTerm:String, @Query("offset") offset:Int): SearchResults
}