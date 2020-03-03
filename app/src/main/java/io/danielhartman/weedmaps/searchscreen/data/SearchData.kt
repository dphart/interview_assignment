package io.danielhartman.weedmaps.searchscreen.data

import androidx.lifecycle.MutableLiveData
import io.danielhartman.weedmaps.searchscreen.models.SearchHistory

class SearchData {
    //In memory list of recent searches
    val searchHistory: MutableLiveData<List<SearchHistory>> =
        MutableLiveData<List<SearchHistory>>().apply {
            postValue(listOf())
        }
    //This could be far more complex. We could add persistent storage or an api call in here as well

}
