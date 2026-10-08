package krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productdetails

import androidx.lifecycle.SavedStateHandle
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import java.io.IOException
import kotlin.test.Test
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import krio.systemdesign.shoppingapp.core.composeutils.keepCollecting
import krio.systemdesign.shoppingapp.core.composeutils.viewModelTest
import krio.systemdesign.shoppingapp.feature.catalog.impl.analytics.ProductViewedAnalyticsEvent
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.model.ProductLoadResult
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.repository.TestProductRepository
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.usecase.GetProductUseCase
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.navigation.ProductDetailsRoute
import krio.systemdesign.shoppingapp.shared.analytics.TestAnalytics
import krio.systemdesign.shoppingapp.shared.analytics.sent
import krio.systemdesign.shoppingapp.shared.domain.model.testProduct
import krio.systemdesign.shoppingapp.shared.domain.repository.TestCartRepository
import krio.systemdesign.shoppingapp.shared.domain.usecase.AddToCartUseCase
import krio.systemdesign.shoppingapp.shared.domain.usecase.ObserveCartUseCase
import krio.systemdesign.shoppingapp.shared.domain.usecase.RemoveFromCartUseCase
import krio.systemdesign.shoppingapp.shared.domain.usecase.UpdateCartQuantityUseCase
import krio.systemdesign.shoppingapp.shared.ui.product.PRODUCT_IMAGE_TRANSITION_MILLIS

// advanceTimeBy and runCurrent are still marked experimental.
@OptIn(ExperimentalCoroutinesApi::class)
class ProductDetailsViewModelTest {

    private val product = testProduct(id = "1", price = 1000, availableQuantity = 5)
    private val productRepository = TestProductRepository()
    private val cartRepository = TestCartRepository()
    private val analytics = TestAnalytics()

    @Test
    fun `route's name and image are shown while the product loads`() = viewModelTest {
        productRepository.productAnswer = { CompletableDeferred<ProductLoadResult>().await() }

        val viewModel = productDetailsViewModel()

        assertThat(viewModel.uiState.value).isEqualTo(
            ProductDetailsUiState(
                productId = "1",
                name = "Mug",
                imageUrl = "mug.png",
                details = ProductDetailsUiState.Details.Loading,
            ),
        )
    }

    // Details that appear during the transition would change the layout under the flying image.
    @Test
    fun `details wait for the image transition to end`() = viewModelTest {
        productRepository.productAnswer = { ProductLoadResult.Success(product) }
        val viewModel = productDetailsViewModel()

        advanceTimeBy(PRODUCT_IMAGE_TRANSITION_MILLIS - 1L)

        assertThat(viewModel.uiState.value.details).isEqualTo(ProductDetailsUiState.Details.Loading)
    }

    @Test
    fun `details appear when the image transition ends`() = viewModelTest {
        productRepository.productAnswer = { ProductLoadResult.Success(product) }
        val viewModel = productDetailsViewModel()

        advanceTimeBy(PRODUCT_IMAGE_TRANSITION_MILLIS.toLong())
        runCurrent()

        assertThat(viewModel.uiState.value.details).isInstanceOf<ProductDetailsUiState.Details.Loaded>()
    }

    @Test
    fun `slow load shows the details as soon as it ends`() = viewModelTest {
        productRepository.productAnswer = {
            delay(SLOW_LOAD_MILLIS)
            ProductLoadResult.Success(product)
        }
        val viewModel = productDetailsViewModel()

        advanceTimeBy(SLOW_LOAD_MILLIS)
        runCurrent()

        assertThat(viewModel.uiState.value.details).isInstanceOf<ProductDetailsUiState.Details.Loaded>()
    }

    @Test
    fun `missing product says it wasn't found`() = viewModelTest {
        productRepository.productAnswer = { ProductLoadResult.NotFound }
        val viewModel = productDetailsViewModel()

        advanceTimeBy(PRODUCT_IMAGE_TRANSITION_MILLIS.toLong())
        runCurrent()

        assertThat(viewModel.uiState.value.details).isEqualTo(ProductDetailsUiState.Details.NotFound)
    }

    @Test
    fun `failed load shows the error`() = viewModelTest {
        productRepository.productAnswer = { ProductLoadResult.Error(IOException("No network")) }
        val viewModel = productDetailsViewModel()

        advanceTimeBy(PRODUCT_IMAGE_TRANSITION_MILLIS.toLong())
        runCurrent()

        assertThat(viewModel.uiState.value.details).isEqualTo(ProductDetailsUiState.Details.Error)
    }

    @Test
    fun `retry loads the product again`() = viewModelTest {
        productRepository.productAnswer = { ProductLoadResult.Error(IOException("No network")) }
        val viewModel = productDetailsViewModel()
        productRepository.productAnswer = { ProductLoadResult.Success(product) }

        viewModel.onEvent(ProductDetailsEvent.OnRetryClick)
        advanceTimeBy(PRODUCT_IMAGE_TRANSITION_MILLIS.toLong())
        runCurrent()

        assertThat(viewModel.uiState.value.details).isInstanceOf<ProductDetailsUiState.Details.Loaded>()
    }

    @Test
    fun `product in the cart up to its stock can't be increased`() = viewModelTest {
        cartRepository.addItem(product, quantity = 5)
        productRepository.productAnswer = { ProductLoadResult.Success(product) }
        val viewModel = productDetailsViewModel()

        advanceTimeBy(PRODUCT_IMAGE_TRANSITION_MILLIS.toLong())
        runCurrent()

        assertThat(viewModel.uiState.value.details)
            .isInstanceOf<ProductDetailsUiState.Details.Loaded>()
            .prop(ProductDetailsUiState.Details.Loaded::cartControl)
            .isEqualTo(ProductDetailsUiState.CartControl.InStock(quantity = 5, canIncrease = false))
    }

    @Test
    fun `product out of stock offers no cart buttons`() = viewModelTest {
        productRepository.productAnswer = { ProductLoadResult.Success(product.copy(availableQuantity = 0)) }
        val viewModel = productDetailsViewModel()

        advanceTimeBy(PRODUCT_IMAGE_TRANSITION_MILLIS.toLong())
        runCurrent()

        assertThat(viewModel.uiState.value.details)
            .isInstanceOf<ProductDetailsUiState.Details.Loaded>()
            .prop(ProductDetailsUiState.Details.Loaded::cartControl)
            .isEqualTo(ProductDetailsUiState.CartControl.OutOfStock)
    }

    @Test
    fun `add to cart before the product loads does nothing`() = viewModelTest {
        productRepository.productAnswer = { CompletableDeferred<ProductLoadResult>().await() }
        val viewModel = productDetailsViewModel()

        viewModel.onEvent(ProductDetailsEvent.OnAddToCartClick)

        assertThat(cartRepository.currentCart.items).isEmpty()
    }

    @Test
    fun `loaded product is reported as viewed`() = viewModelTest {
        productRepository.productAnswer = { ProductLoadResult.Success(product) }

        productDetailsViewModel()
        advanceTimeBy(PRODUCT_IMAGE_TRANSITION_MILLIS.toLong())
        runCurrent()

        assertThat(analytics.sentEvents).containsExactly(ProductViewedAnalyticsEvent("1", priceCents = 1000).sent())
    }

    @Test
    fun `product that failed to load is not reported`() = viewModelTest {
        productRepository.productAnswer = { ProductLoadResult.Error(IOException("No network")) }

        productDetailsViewModel()
        advanceTimeBy(PRODUCT_IMAGE_TRANSITION_MILLIS.toLong())
        runCurrent()

        assertThat(analytics.sentEvents).isEmpty()
    }

    // As the route passes them: the id, plus the name and image the list already showed.
    private fun TestScope.productDetailsViewModel() = ProductDetailsViewModel(
        getProduct = GetProductUseCase(productRepository),
        addToCart = AddToCartUseCase(cartRepository),
        updateCartQuantity = UpdateCartQuantityUseCase(cartRepository),
        removeFromCart = RemoveFromCartUseCase(cartRepository),
        observeCart = ObserveCartUseCase(cartRepository),
        analytics = analytics.analytics,
        savedStateHandle = SavedStateHandle(
            mapOf(
                ProductDetailsRoute::productId.name to "1",
                ProductDetailsRoute::productName.name to "Mug",
                ProductDetailsRoute::imageUrl.name to "mug.png",
            ),
        ),
    ).also { keepCollecting(it.uiState) }

    private companion object {
        const val SLOW_LOAD_MILLIS = 1000L
    }
}
