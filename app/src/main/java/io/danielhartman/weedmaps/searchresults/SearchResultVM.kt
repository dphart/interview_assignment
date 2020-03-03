package io.danielhartman.weedmaps.searchresults

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.danielhartman.weedmaps.searchresults.data.SearchResultData
import io.danielhartman.weedmaps.searchresults.resultadapter.SearchResultItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchResultVM(searchTerm:String, val searchResultData: SearchResultData) : ViewModel(){

    @VisibleForTesting var hasRequested = false
    val searchItems:LiveData<List<SearchResultItem>> = Transformations.map(searchResultData.data){
        it.map {model ->
            SearchResultItem(
                imageUrl = model.imageUrl,
                name = model.name,
                review = model.review
            )
        }
    }

    fun handleAction(action:SearchResultFragment.Action){
        when(action){
            is SearchResultFragment.Action.OnEnterScreen -> handleOnEnterScreen()
            is SearchResultFragment.Action.UserScrolledToBottom -> handleUserScrolledToBottom()
        }

    }

    private fun handleUserScrolledToBottom() {
        CoroutineScope(Dispatchers.IO).launch{
            searchResultData.getAllSearchResultsAndMoveToNextPage()
        }
    }

    private fun handleOnEnterScreen() {
        if (!hasRequested) {
            CoroutineScope(Dispatchers.IO).launch {
                searchResultData.getAllSearchResultsAndMoveToNextPage()
            }
            hasRequested = true
        }
    }

}

class SearchResultVMFactory(val searchTerm: String, val searchResultData: SearchResultData) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchResultVM(searchTerm, searchResultData) as T
    }

}