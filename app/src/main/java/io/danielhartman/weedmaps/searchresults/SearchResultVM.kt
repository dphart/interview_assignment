package io.danielhartman.weedmaps.searchresults

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.danielhartman.weedmaps.Success
import io.danielhartman.weedmaps.searchresults.data.LocationData
import io.danielhartman.weedmaps.searchresults.data.SearchResultData
import io.danielhartman.weedmaps.searchresults.resultadapter.SearchResultItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchResultVM(
    val searchResultData: SearchResultData
) : ViewModel() {

    @VisibleForTesting
    var hasRequested = false
    val searchItems: MutableLiveData<List<SearchResultItem>> = MutableLiveData()


    val loading: LiveData<Boolean> = MutableLiveData<Boolean>().apply { postValue(true) }
    fun handleAction(action: SearchResultFragment.Action) {
        when (action) {
            is SearchResultFragment.Action.OnEnterScreen -> handleOnEnterScreen()
            is SearchResultFragment.Action.UserScrolledToBottom -> handleUserScrolledToBottom()
        }

    }

    private fun handleUserScrolledToBottom() {
        getSearchResults()
    }

    fun getSearchResults(){
        CoroutineScope(Dispatchers.IO).launch {
            val result = searchResultData.getSearchResultForPage(searchItems.value?.size ?: 0)
            when (result) {
                is Success -> {
                    searchItems.postValue(result.data.map {
                        SearchResultItem(
                            it.imageUrl,
                            it.name,
                            it.review
                        )
                    })
                }

            }
        }
    }

    private fun handleOnEnterScreen() {
        if (!hasRequested) {
            getSearchResults()
            hasRequested = true
        }
    }

}

class SearchResultVMFactory(
    val searchResultData: SearchResultData
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchResultVM(searchResultData) as T
    }

}