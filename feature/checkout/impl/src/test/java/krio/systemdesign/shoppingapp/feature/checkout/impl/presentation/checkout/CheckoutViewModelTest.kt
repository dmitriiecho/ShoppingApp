package krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.checkout

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import java.io.IOException
import kotlin.test.Test
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.TestScope
import krio.systemdesign.shoppingapp.core.composeutils.keepCollecting
import krio.systemdesign.shoppingapp.core.composeutils.text.UiText
import krio.systemdesign.shoppingapp.core.composeutils.typeText
import krio.systemdesign.shoppingapp.core.composeutils.viewModelTest
import krio.systemdesign.shoppingapp.feature.checkout.impl.R
import krio.systemdesign.shoppingapp.feature.checkout.impl.analytics.OrderPlacedAnalyticsEvent
import krio.systemdesign.shoppingapp.feature.checkout.impl.domain.usecase.PlaceOrderUseCase
import krio.systemdesign.shoppingapp.shared.analytics.TestAnalytics
import krio.systemdesign.shoppingapp.shared.analytics.sent
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode
import krio.systemdesign.shoppingapp.shared.domain.model.testCart
import krio.systemdesign.shoppingapp.shared.domain.model.testCartItem
import krio.systemdesign.shoppingapp.shared.domain.repository.TestCartRepository
import krio.systemdesign.shoppingapp.shared.domain.usecase.ObserveCartUseCase

class CheckoutViewModelTest {

    private val cart = testCart(
        testCartItem(productId = "1", price = 1000, quantity = 2),
        promoCode = PromoCode("SALE10", 10),
    )
    private val cartRepository = TestCartRepository(cart)
    private val analytics = TestAnalytics()

    @Test
    fun `placed order closes the screen`() = viewModelTest {
        val viewModel = checkoutViewModel()
        viewModel.uiState.value.address.street.typeText("1 Main St")

        viewModel.effects.test {
            viewModel.onEvent(CheckoutEvent.OnPlaceOrderClick)

            assertThat(awaitItem()).isEqualTo(CheckoutEffect.Close)
        }
    }

    // Placing the order empties the cart, and the screen shows the cart's order until it closes.
    @Test
    fun `placed order stays on screen while the screen closes`() = viewModelTest {
        val viewModel = checkoutViewModel()
        viewModel.uiState.value.address.street.typeText("1 Main St")
        val order = viewModel.uiState.value.order

        viewModel.onEvent(CheckoutEvent.OnPlaceOrderClick)

        assertThat(viewModel.uiState.value.order).isEqualTo(order)
    }

    @Test
    fun `double tap places one order`() = viewModelTest {
        val placing = CompletableDeferred<Unit>()
        cartRepository.beforeChange = { placing.await() }
        val viewModel = checkoutViewModel()
        viewModel.uiState.value.address.street.typeText("1 Main St")

        viewModel.onEvent(CheckoutEvent.OnPlaceOrderClick)
        viewModel.onEvent(CheckoutEvent.OnPlaceOrderClick)
        placing.complete(Unit)

        assertThat(analytics.sentEvents).containsExactly(placedOrderEvent())
    }

    @Test
    fun `order without a street is not placed`() = viewModelTest {
        val viewModel = checkoutViewModel()

        viewModel.onEvent(CheckoutEvent.OnPlaceOrderClick)

        assertThat(cartRepository.currentCart).isEqualTo(cart)
    }

    @Test
    fun `failed order says it wasn't placed`() = viewModelTest {
        cartRepository.changeError = IOException("Disk full")
        val viewModel = checkoutViewModel()
        viewModel.uiState.value.address.street.typeText("1 Main St")

        viewModel.effects.test {
            viewModel.onEvent(CheckoutEvent.OnPlaceOrderClick)

            assertThat(awaitItem())
                .isEqualTo(CheckoutEffect.ShowSnackBar(UiText.Resource(R.string.checkout_place_order_error)))
        }
    }

    @Test
    fun `failed order can be placed again`() = viewModelTest {
        cartRepository.changeError = IOException("Disk full")
        val viewModel = checkoutViewModel()
        viewModel.uiState.value.address.street.typeText("1 Main St")

        viewModel.onEvent(CheckoutEvent.OnPlaceOrderClick)

        assertThat(viewModel.uiState.value.canSubmit).isTrue()
    }

    // Placing the order empties the cart, so the event must carry the order taken before that.
    @Test
    fun `placed order is reported with its items, total and promo code`() = viewModelTest {
        val viewModel = checkoutViewModel()
        viewModel.uiState.value.address.street.typeText("1 Main St")

        viewModel.onEvent(CheckoutEvent.OnPlaceOrderClick)

        assertThat(analytics.sentEvents).containsExactly(placedOrderEvent())
    }

    @Test
    fun `failed order is not reported`() = viewModelTest {
        cartRepository.changeError = IOException("Disk full")
        val viewModel = checkoutViewModel()
        viewModel.uiState.value.address.street.typeText("1 Main St")

        viewModel.onEvent(CheckoutEvent.OnPlaceOrderClick)

        assertThat(analytics.sentEvents).isEmpty()
    }

    @Test
    fun `payment method stays chosen when the screen is recreated`() = viewModelTest {
        val savedStateHandle = SavedStateHandle()
        val choosingCash = CheckoutEvent.OnPaymentMethodChange(CheckoutUiState.PaymentMethod.Cash)
        checkoutViewModel(savedStateHandle).onEvent(choosingCash)

        val recreated = checkoutViewModel(savedStateHandle)

        assertThat(recreated.uiState.value.paymentMethod).isEqualTo(CheckoutUiState.PaymentMethod.Cash)
    }

    private fun TestScope.checkoutViewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) =
        CheckoutViewModel(
            observeCart = ObserveCartUseCase(cartRepository),
            placeOrder = PlaceOrderUseCase(cartRepository),
            analytics = analytics.analytics,
            savedStateHandle = savedStateHandle,
        ).also { keepCollecting(it.uiState) }

    private fun placedOrderEvent() =
        OrderPlacedAnalyticsEvent(itemCount = 2, totalCents = 1800, promoCode = "SALE10").sent()
}
