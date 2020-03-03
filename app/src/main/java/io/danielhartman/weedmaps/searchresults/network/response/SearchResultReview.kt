package io.danielhartman.weedmaps.searchresults.network.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResultReview(val reviews:List<Review>) {

}

@JsonClass(generateAdapter = true)
data class Review(val text:String, val rating:Int) {

}
