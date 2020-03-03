package io.danielhartman.weedmaps.searchscreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.danielhartman.weedmaps.Dependencies
import io.danielhartman.weedmaps.searchscreen.models.SearchHistory
import io.danielhartman.weedmaps.searchscreen.recentadapter.RecentSearchItem
import java.util.*

class SearchFragmentVM : ViewModel() {
    //This could be replaced by dagger
    private val searchData = Dependencies.recentSearches
    val searchHistory = Transformations.map(searchData.searchHistory) { searchHistory ->
        searchHistory.sortedBy { it.text }.map {
            RecentSearchItem(
                it.text
            )
        }.distinct()
    }
    val textOverride = MutableLiveData<String>().apply { postValue("") }

    fun handleAction(action:SearchFragment.Action){
        when(action){
            is SearchFragment.Action.SearchClicked -> handleSearchClicked(action.searchValue)
            is SearchFragment.Action.RecentClicked -> handleRecentClicked(action.recentValue)
        }
    }

    private fun handleRecentClicked(recentValue: String) {
        textOverride.postValue(recentValue)
    }

    private fun handleSearchClicked(searchValue: String) {
        textOverride.postValue("")
        searchData.searchHistory.postValue(searchData.searchHistory.value!! + SearchHistory(
            searchValue,
            GregorianCalendar().time
        )
        )
    }


}
