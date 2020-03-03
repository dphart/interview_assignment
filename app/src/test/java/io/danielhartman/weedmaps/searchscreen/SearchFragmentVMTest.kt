package io.danielhartman.weedmaps.searchscreen

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.danielhartman.weedmaps.searchscreen.data.SearchData
import io.danielhartman.weedmaps.searchscreen.recentadapter.RecentSearchItem
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class SearchFragmentVMTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()
    private val observer: Observer<String> = mock {}

    @Test
    fun `When user clicks search the text is cleared out and the item is added to the history`() {
        val vm = SearchFragmentVM(SearchData(), mock {  })
        vm.textOverride.observeForever(observer)
        vm.textOverride.postValue("something")
        vm.searchHistory.observeForever(mock { })
        vm.handleAction(SearchFragment.Action.SearchClicked("searchItem1"))
        assertEquals(vm.textOverride.value, "")
        assertEquals(
            vm.searchHistory.value, listOf(
                RecentSearchItem(
                    "searchItem1"
                )
            )
        )
    }

    @Test
    fun `When user clicks a recent search, it populates the search field`() {
        val vm = SearchFragmentVM(SearchData(), mock {  })
        vm.textOverride.observeForever(observer)
        vm.textOverride.postValue("something")
        vm.searchHistory.observeForever(mock { })
        vm.handleAction(SearchFragment.Action.RecentClicked("searchItem1"))
        assertEquals(vm.textOverride.value, "searchItem1")
    }

    @Test
    fun `When a user gives location permission we update the location`(){
        val vm = SearchFragmentVM(SearchData(), mock {  })
        vm.handleAction(SearchFragment.Action.LocationGranted())
        verify(vm.locationData).getLatestLocation()
    }


    @After
    fun cleanup(){
        Mockito.validateMockitoUsage()
    }

}