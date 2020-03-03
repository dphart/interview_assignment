package io.danielhartman.weedmaps.searchresults

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.danielhartman.weedmaps.searchresults.data.SearchResultData
import io.danielhartman.weedmaps.searchresults.model.SearchResultModel
import io.danielhartman.weedmaps.searchresults.network.response.Business
import io.danielhartman.weedmaps.searchresults.network.response.Review
import io.danielhartman.weedmaps.searchresults.network.response.SearchResultReview
import io.danielhartman.weedmaps.searchresults.network.response.SearchResults
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class SearchResultDataTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()
    val observer: Observer<List<SearchResultModel>> = mock{}
    @Test
    fun `Verify right data is returned when api succeeds and that the offset is incremented for future calls`(){
        val searchResultData =
            SearchResultData(
                "test",
                mock {
                    onBlocking { getSearchResultsForTerm(any(), any()) } doReturn (SearchResults(
                        listOf(
                            Business(
                                "name",
                                "alias",
                                "image"
                            )
                        )
                    ))
                    onBlocking { getReviewsForAlias(any()) } doReturn SearchResultReview(
                        listOf(
                            Review(
                                "review1",
                                1
                            ),
                            Review(
                                "review2",
                                2
                            )
                        )
                    )
                })
        searchResultData.data.observeForever(observer)
        val result = runBlocking { searchResultData.getAllSearchResultsAndMoveToNextPage()}
        //We check that we only return the top rated of the two results
        assertEquals(listOf(
            SearchResultModel(
                "name",
                "image",
                "review2",
                "alias"
            )
        ), result)
        assertEquals(searchResultData.offset, searchResultData.limit *1)
        runBlocking { searchResultData.getAllSearchResultsAndMoveToNextPage()}
        assertEquals(searchResultData.offset, searchResultData.limit *2)

    }

}