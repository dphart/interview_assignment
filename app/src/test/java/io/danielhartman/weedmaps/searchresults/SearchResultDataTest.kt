package io.danielhartman.weedmaps.searchresults

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SearchResultDataTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun `Verify right data is returned when api succeeds and that the offset is incremented for future calls`() {
        val searchResultData =
            SearchResultData(
                "test",
                mock {
                    onBlocking { getSearchResultsForTerm(any(), any(), any(), any()) } doReturn (SearchResults(
                        listOf(
                            Business(
                                name = "name",
                                alias = "alias",
                                image_url = "image"
                            )
                        )
                    ))
                    onBlocking { getReviewsForAlias(any()) } doReturn SearchResultReview(
                        listOf(
                            Review(
                                text = "review1",
                                rating = 1
                            ),
                            Review(
                                text = "review2",
                                rating = 2
                            )
                        )
                    )
                }, mock {  })
        searchResultData.data.observeForever(mock {})
        val result = runBlocking { searchResultData.getAllSearchResultsAndMoveToNextPage() }
        //We check that we only return the top rated of the two results
        assertEquals(
            listOf(
                SearchResultModel(
                    name = "name",
                    imageUrl = "image",
                    review = "review2",
                    identifier = "alias"
                )
            ), result
        )
        assertEquals(searchResultData.offset, searchResultData.limit * 1)
        runBlocking { searchResultData.getAllSearchResultsAndMoveToNextPage() }
        assertEquals(searchResultData.offset, searchResultData.limit * 2)

    }

}