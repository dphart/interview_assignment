package io.danielhartman.weedmaps.searchresults

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.validateMockitoUsage
import com.nhaarman.mockitokotlin2.verifyBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.internal.verification.Times

class SearchResultVMTest {

    @Test
    fun `Correctly Fetch the initial list when a user comes to the screen but not second time`() {
        val testTerm = "searchTerm"
        val vm = SearchResultVM(testTerm, mock {})
        assertEquals(vm.hasRequested, false)
        vm.handleAction(SearchResultFragment.Action.OnEnterScreen)
        assertEquals(vm.hasRequested, true)
        verifyBlocking(vm.searchResultData, Times(1)) { getAllSearchResultsAndMoveToNextPage() }
        vm.handleAction(SearchResultFragment.Action.OnEnterScreen)
        verifyBlocking(vm.searchResultData, Times(1)) { getAllSearchResultsAndMoveToNextPage() }
    }

    @Test
    fun `User should fetch next list when scrolled to the bottom`() {
        val testTerm = "searchTerm"
        val vm = SearchResultVM(testTerm, mock {})
        vm.handleAction(SearchResultFragment.Action.UserScrolledToBottom)
        verifyBlocking(vm.searchResultData, Times(1)) { getAllSearchResultsAndMoveToNextPage() }
        vm.handleAction(SearchResultFragment.Action.UserScrolledToBottom)
        verifyBlocking(vm.searchResultData, Times(2)) { getAllSearchResultsAndMoveToNextPage() }
    }

    @After
    fun after() {
        validateMockitoUsage()
    }

}