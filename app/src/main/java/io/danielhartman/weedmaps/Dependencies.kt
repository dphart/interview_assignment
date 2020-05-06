package io.danielhartman.weedmaps

import android.app.Activity
import android.content.Context
import io.danielhartman.weedmaps.searchresults.data.LocationData
import io.danielhartman.weedmaps.searchresults.data.SearchResultData
import io.danielhartman.weedmaps.searchresults.network.SearchResultService
import io.danielhartman.weedmaps.searchscreen.data.SearchData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

//Typically I would use Dagger but it feels like overkill for this project with minimal dependencies.

object Dependencies {
    val recentSearches = SearchData()
    val locationData by lazy {LocationData(appContext)  }
    lateinit var appContext:Context
    val authToken =
        "Bearer //Insert new yelp api key here"
    private val getHttpClient = let {
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", authToken)
                    .build()
                chain.proceed(newRequest)
            }
        client
    }
    //visible for Tests
    private var searchResultService: SearchResultService = Retrofit.Builder()
        .baseUrl("https://api.yelp.com")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(getHttpClient.build())
        .build().create(SearchResultService::class.java)

    fun searchResultData(term:String) =
        SearchResultData(
            term,
            searchResultService,
            locationData
        )

}

fun <T>Response<T>.toResult():Result<T>{
    return if (this.isSuccessful){
        Success(this.body()!!)
    } else {
        Error(this.code(), this.message())
    }
}

sealed class Result<T>(){
    fun getOrError():T {
        return (this as Success<T>).data
    }
    fun isSuccessful():Boolean = this is Success<T>
}
class Success<T>(val data:T):Result<T>()
data class Error<T>(val code:Int, val message:String):Result<T>()
