package krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productlist

import androidx.lifecycle.SavedStateHandle
import androidx.paging.testing.asSnapshot
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import kotlin.test.Test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import krio.systemdesign.shoppingapp.core.composeutils.keepCollecting
import krio.systemdesign.shoppingapp.core.composeutils.typeText
import krio.systemdesign.shoppingapp.core.composeutils.viewModelTest
import krio.systemdesign.shoppingapp.feature.catalog.impl.analytics.ProductsSearchedAnalyticsEvent
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.model.ProductsPage
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.repository.TestProductRepository
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.usecase.GetProductsUseCase
import krio.systemdesign.shoppingapp.shared.analytics.TestAnalytics
import krio.systemdesign.shoppingapp.shared.analytics.sent
import krio.systemdesign.shoppingapp.shared.domain.model.testProduct
import krio.systemdesign.shoppingapp.shared.domain.repository.TestCartRepository
import krio.systemdesign.shoppingapp.shared.domain.usecase.AddToCartUseCase
import krio.systemdesign.shoppingapp.shared.domain.usecase.ObserveCartUseCase
import krio.systemdesign.shoppingapp.shared.domain.usecase.RemoveFromCartUseCase
import krio.systemdesign.shoppingapp.shared.domain.usecase.UpdateCartQuantityUseCase

// advanceTimeBy and runCurrent are still marked experimental.
@OptIn(ExperimentalCoroutinesApi::class)
class ProductListViewModelTest {

    // Three full pages of PAGE_SIZE products, as the list loads them.
    private val productRepository = TestProductRepository().apply {
        productsAnswer = { _, page ->
            val products = (1..PAGE_SIZE).map { testProduct(id = "$page-$it") }
            Result.success(ProductsPage(products, endReached = page == 3))
        }
    }
    private val cartRepository = TestCartRepository()
    private val analytics = TestAnalytics()

    @Test
    fun `search waits until the user stops typing`() = viewModelTest {
        val viewModel = productListViewModel()

        viewModel.uiState.value.searchQuery.typeText("mug")
        advanceTimeBy(SEARCH_DEBOUNCE_MILLIS - 1)

        assertThat(analytics.sentEvents).isEmpty()
    }

    @Test
    fun `search starts once the user stops typing`() = viewModelTest {
        val viewModel = productListViewModel()

        viewModel.uiState.value.searchQuery.typeText("mug")
        advanceTimeBy(SEARCH_DEBOUNCE_MILLIS)
        runCurrent()

        assertThat(analytics.sentEvents).containsExactly(ProductsSearchedAnalyticsEvent(queryLength = 3).sent())
    }

    @Test
    fun `spaces around the query don't start a new search`() = viewModelTest {
        val viewModel = productListViewModel()
        val field = viewModel.uiState.value.searchQuery
        field.typeText("mug")
        advanceTimeBy(SEARCH_DEBOUNCE_MILLIS)
        runCurrent()

        field.typeText(" mug ")
        advanceTimeBy(SEARCH_DEBOUNCE_MILLIS)
        runCurrent()

        assertThat(analytics.sentEvents).containsExactly(ProductsSearchedAnalyticsEvent(queryLength = 3).sent())
    }

    @Test
    fun `reopened list starts at the page the user stopped at`() = viewModelTest {
        val savedStateHandle = SavedStateHandle()
        productListViewModel(savedStateHandle).onEvent(
            ProductListEvent.OnFirstVisibleItemChange(
                index =
                    2 * PAGE_SIZE + 5,
            ),
        )

        productListViewModel(savedStateHandle).products.asSnapshot()

        assertThat(productRepository.pageRequests.first()).isEqualTo("" to 3)
    }

    @Test
    fun `new search starts at the first page`() = viewModelTest {
        val savedStateHandle = SavedStateHandle()
        productListViewModel(savedStateHandle).onEvent(
            ProductListEvent.OnFirstVisibleItemChange(
                index =
                    2 * PAGE_SIZE + 5,
            ),
        )
        val viewModel = productListViewModel(savedStateHandle)

        viewModel.uiState.value.searchQuery.typeText("mug")
        advanceTimeBy(SEARCH_DEBOUNCE_MILLIS)
        viewModel.products.asSnapshot()

        assertThat(productRepository.pageRequests.last()).isEqualTo("mug" to 1)
    }

    private fun TestScope.productListViewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) =
        ProductListViewModel(
            getProducts = GetProductsUseCase(productRepository),
            addToCart = AddToCartUseCase(cartRepository),
            updateCartQuantity = UpdateCartQuantityUseCase(cartRepository),
            removeFromCart = RemoveFromCartUseCase(cartRepository),
            observeCart = ObserveCartUseCase(cartRepository),
            analytics = analytics.analytics,
            savedStateHandle = savedStateHandle,
        ).also {
            keepCollecting(it.uiState)
            // Searches start only while the list collects its products, as the screen does.
            keepCollecting(it.products)
        }

    private companion object {
        // SEARCH_DEBOUNCE_MS and PAGE_SIZE in ProductListViewModel.kt.
        const val SEARCH_DEBOUNCE_MILLIS = 300L
        const val PAGE_SIZE = 10
    }
}
