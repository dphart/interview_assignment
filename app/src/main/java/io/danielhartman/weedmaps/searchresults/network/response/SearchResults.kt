package io.danielhartman.weedmaps.searchresults.network.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResults(val businesses:List<Business>) {


}

@JsonClass(generateAdapter = true)
data class Business(
    val name:String,
    val alias:String,
    val image_url:String
)
