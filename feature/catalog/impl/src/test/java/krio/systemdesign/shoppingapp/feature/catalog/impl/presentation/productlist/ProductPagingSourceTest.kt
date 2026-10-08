package krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productlist

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import assertk.assertions.prop
import java.io.IOException
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.model.ProductsPage
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.repository.TestProductRepository
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.usecase.GetProductsUseCase
import krio.systemdesign.shoppingapp.shared.domain.model.Product
import krio.systemdesign.shoppingapp.shared.domain.model.testProduct

class ProductPagingSourceTest {

    private val repository = TestProductRepository()
    private val source = ProductPagingSource(GetProductsUseCase(repository), query = "", pageSize = 2)

    @Test
    fun `first page has no page before it`() = runTest {
        repository.pages[1] = ProductsPage(listOf(testProduct("1"), testProduct("2")), endReached = false)

        val page = source.loadPage(1)

        assertThat(page.prevKey).isNull()
    }

    @Test
    fun `middle page points to its neighbours`() = runTest {
        repository.pages[2] = ProductsPage(listOf(testProduct("3"), testProduct("4")), endReached = false)

        val page = source.loadPage(2)

        assertThat(page.prevKey).isEqualTo(1)
        assertThat(page.nextKey).isEqualTo(3)
    }

    @Test
    fun `last page has no page after it`() = runTest {
        repository.pages[3] = ProductsPage(listOf(testProduct("5")), endReached = true)

        val page = source.loadPage(3)

        assertThat(page.nextKey).isNull()
    }

    // The list keeps placeholders for the pages above, so a list reopened in the middle has the right length.
    @Test
    fun `page counts the products before it`() = runTest {
        repository.pages[3] = ProductsPage(listOf(testProduct("5")), endReached = true)

        val page = source.loadPage(3)

        assertThat(page.itemsBefore).isEqualTo(4)
    }

    // A product renamed between two page loads can move to the next page; a repeated key crashes the list.
    @Test
    fun `product already loaded on an earlier page is dropped`() = runTest {
        repository.pages[1] = ProductsPage(listOf(testProduct("1"), testProduct("2")), endReached = false)
        repository.pages[2] = ProductsPage(listOf(testProduct("2"), testProduct("3")), endReached = true)
        source.loadPage(1)

        val page = source.loadPage(2)

        assertThat(page.data.map { it.id }).containsExactly("3")
    }

    @Test
    fun `failed load returns the error`() = runTest {
        val error = IOException("No network")
        repository.error = error

        val result = source.load(PagingSource.LoadParams.Refresh(key = 1, loadSize = 2, placeholdersEnabled = true))

        assertThat(
            result,
        ).isInstanceOf<LoadResult.Error<Int, Product>>().prop(LoadResult.Error<Int, Product>::throwable)
            .isEqualTo(error)
    }

    @Test
    fun `refresh starts from the page the user was looking at`() {
        val shownPage = LoadResult.Page(listOf(testProduct("3"), testProduct("4")), prevKey = 1, nextKey = 3)
        val state = PagingState(
            pages = listOf(shownPage),
            anchorPosition = 2,
            config = PagingConfig(pageSize = 2),
            leadingPlaceholderCount = 2,
        )

        assertThat(source.getRefreshKey(state)).isEqualTo(2)
    }

    private suspend fun ProductPagingSource.loadPage(key: Int): LoadResult.Page<Int, Product> {
        val result = load(PagingSource.LoadParams.Refresh(key = key, loadSize = 2, placeholdersEnabled = true))
        return result as LoadResult.Page
    }
}
