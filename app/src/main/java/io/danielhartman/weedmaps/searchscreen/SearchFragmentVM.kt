package io.danielhartman.weedmaps.searchscreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.danielhartman.weedmaps.searchresults.data.LocationData
import io.danielhartman.weedmaps.searchscreen.data.SearchData
import io.danielhartman.weedmaps.searchscreen.models.SearchHistory
import io.danielhartman.weedmaps.searchscreen.recentadapter.RecentSearchItem
import java.util.*

class SearchFragmentVM(val searchData: SearchData, val locationData: LocationData) : ViewModel() {
    //This could be replaced by dagger
    val searchHistory = Transformations.map(searchData.searchHistory) { searchHistory ->
        searchHistory.sortedBy { it.text }.map {
            RecentSearchItem(
                it.text
            )
        }.distinct()
    }
    val textOverride = MutableLiveData<String>().apply { postValue("") }

    fun handleAction(action: SearchFragment.Action) {
        when (action) {
            is SearchFragment.Action.SearchClicked -> handleSearchClicked(action.searchValue)
            is SearchFragment.Action.RecentClicked -> handleRecentClicked(action.recentValue)
            is SearchFragment.Action.LocationGranted -> handleLocationGranted()
        }
    }

    private fun handleLocationGranted() {
        locationData.getLatestLocation()
    }

    private fun handleRecentClicked(recentValue: String) {
        textOverride.postValue(recentValue)
    }

    private fun handleSearchClicked(searchValue: String) {
        textOverride.postValue("")
        searchData.searchHistory.postValue(
            searchData.searchHistory.value!! + SearchHistory(
                searchValue,
                GregorianCalendar().time
            )
        )
    }
}
class SearchFragmentVMFactory(val searchData:SearchData,val  locationData:LocationData) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchFragmentVM(searchData, locationData) as T
    }

}