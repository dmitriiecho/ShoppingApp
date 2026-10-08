package krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart

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
import krio.systemdesign.shoppingapp.core.composeutils.text.UiText
import krio.systemdesign.shoppingapp.core.composeutils.viewModelTest
import krio.systemdesign.shoppingapp.feature.cart.impl.R
import krio.systemdesign.shoppingapp.feature.cart.impl.analytics.CheckoutStartedAnalyticsEvent
import krio.systemdesign.shoppingapp.feature.cart.impl.domain.usecase.AcceptCartChangesUseCase
import krio.systemdesign.shoppingapp.feature.cart.impl.domain.usecase.ApplyPromoCodeUseCase
import krio.systemdesign.shoppingapp.feature.cart.impl.domain.usecase.ClearCartItemsUseCase
import krio.systemdesign.shoppingapp.feature.cart.impl.domain.usecase.RemovePromoCodeUseCase
import krio.systemdesign.shoppingapp.feature.cart.impl.domain.usecase.ValidateCartUseCase
import krio.systemdesign.shoppingapp.shared.analytics.TestAnalytics
import krio.systemdesign.shoppingapp.shared.analytics.sent
import krio.systemdesign.shoppingapp.shared.domain.model.Cart
import krio.systemdesign.shoppingapp.shared.domain.model.CartValidationResult
import krio.systemdesign.shoppingapp.shared.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.shared.domain.model.testCart
import krio.systemdesign.shoppingapp.shared.domain.model.testCartItem
import krio.systemdesign.shoppingapp.shared.domain.repository.TestCartRepository
import krio.systemdesign.shoppingapp.shared.domain.usecase.ObserveCartUseCase
import krio.systemdesign.shoppingapp.shared.domain.usecase.RemoveFromCartUseCase
import krio.systemdesign.shoppingapp.shared.domain.usecase.UpdateCartQuantityUseCase

class CartViewModelTest {

    private val mug = testCartItem(productId = "1", price = 1000, quantity = 2)
    private val lamp = testCartItem(productId = "2", price = 3000, quantity = 1)
    private val cartRepository = TestCartRepository(testCart(mug, lamp))
    private val analytics = TestAnalytics()

    @Test
    fun `shown screen marks the changes the server found`() = viewModelTest {
        cartRepository.validationAnswer = { invalid(ItemIssue.PriceChanged("1", newPrice = 1200)) }
        val viewModel = cartViewModel()

        viewModel.uiState.test {
            viewModel.onEvent(CartEvent.OnScreenShown)

            assertThat(expectMostRecentItem().loaded().changes.priceChangeCount).isEqualTo(1)
        }
    }

    @Test
    fun `checkout opens once the server confirms the cart`() = viewModelTest {
        val viewModel = cartViewModel()

        viewModel.effects.test {
            viewModel.onEvent(CartEvent.OnCheckoutClick)

            assertThat(awaitItem()).isEqualTo(CartEffect.NavigateToCheckout)
        }
    }

    @Test
    fun `checkout of a cart the server changed asks to review the changes`() = viewModelTest {
        cartRepository.validationAnswer = { invalid(ItemIssue.Unavailable("2")) }
        val viewModel = cartViewModel()

        viewModel.effects.test {
            viewModel.onEvent(CartEvent.OnCheckoutClick)

            assertThat(awaitItem()).isEqualTo(snackBar(R.string.cart_changed))
        }
    }

    @Test
    fun `checkout without an answer from the server says the cart wasn't checked`() = viewModelTest {
        cartRepository.validationAnswer = { CartValidationResult.Error(IOException("No network")) }
        val viewModel = cartViewModel()

        viewModel.effects.test {
            viewModel.onEvent(CartEvent.OnCheckoutClick)

            assertThat(awaitItem()).isEqualTo(snackBar(R.string.cart_validation_error))
        }
    }

    @Test
    fun `cart changed during the check is checked again before checkout`() = viewModelTest {
        val firstAnswer = CompletableDeferred<CartValidationResult>()
        val checkedCarts = mutableListOf<Cart>()
        cartRepository.validationAnswer = { cart ->
            checkedCarts += cart
            if (checkedCarts.size == 1) firstAnswer.await() else CartValidationResult.Success
        }
        val viewModel = cartViewModel()

        viewModel.effects.test {
            viewModel.onEvent(CartEvent.OnCheckoutClick)
            viewModel.onEvent(CartEvent.OnQuantityChange("1", quantity = 3))
            firstAnswer.complete(CartValidationResult.Success)

            assertThat(awaitItem()).isEqualTo(CartEffect.NavigateToCheckout)
            assertThat(checkedCarts.last().quantityOf("1")).isEqualTo(3)
        }
    }

    @Test
    fun `second checkout tap during the check is ignored`() = viewModelTest {
        val answer = CompletableDeferred<CartValidationResult>()
        cartRepository.validationAnswer = { answer.await() }
        val viewModel = cartViewModel()

        viewModel.effects.test {
            viewModel.onEvent(CartEvent.OnCheckoutClick)
            viewModel.onEvent(CartEvent.OnCheckoutClick)
            answer.complete(CartValidationResult.Success)

            assertThat(awaitItem()).isEqualTo(CartEffect.NavigateToCheckout)
            expectNoEvents()
        }
    }

    @Test
    fun `accepting new prices keeps the unavailable items`() = viewModelTest {
        cartRepository.validationAnswer = {
            invalid(ItemIssue.PriceChanged("1", newPrice = 1200), ItemIssue.Unavailable("2"))
        }
        val viewModel = cartViewModel()
        viewModel.onEvent(CartEvent.OnScreenShown)

        viewModel.onEvent(CartEvent.OnAcceptNewPricesClick)

        assertThat(cartRepository.currentCart.items).containsExactly(mug.copy(price = 1200), lamp)
    }

    @Test
    fun `removing unavailable items keeps the old prices`() = viewModelTest {
        cartRepository.validationAnswer = {
            invalid(ItemIssue.PriceChanged("1", newPrice = 1200), ItemIssue.Unavailable("2"))
        }
        val viewModel = cartViewModel()
        viewModel.onEvent(CartEvent.OnScreenShown)

        viewModel.onEvent(CartEvent.OnRemoveUnavailableClick)

        assertThat(cartRepository.currentCart.items).containsExactly(mug)
    }

    @Test
    fun `clear cart dialog stays open when the screen is recreated`() = viewModelTest {
        val savedStateHandle = SavedStateHandle()
        cartViewModel(savedStateHandle).onEvent(CartEvent.OnClearCartClick)

        val recreated = cartViewModel(savedStateHandle)

        recreated.uiState.test {
            assertThat(expectMostRecentItem().isClearCartDialogVisible).isTrue()
        }
    }

    @Test
    fun `opened checkout is reported with the cart's item count and total`() = viewModelTest {
        val viewModel = cartViewModel()

        viewModel.onEvent(CartEvent.OnCheckoutClick)

        assertThat(analytics.sentEvents)
            .containsExactly(CheckoutStartedAnalyticsEvent(itemCount = 3, totalCents = 5000).sent())
    }

    @Test
    fun `checkout stopped by the server is not reported`() = viewModelTest {
        cartRepository.validationAnswer = { invalid(ItemIssue.Unavailable("2")) }
        val viewModel = cartViewModel()

        viewModel.onEvent(CartEvent.OnCheckoutClick)

        assertThat(analytics.sentEvents).isEmpty()
    }

    @Test
    fun `failed cart change says the cart wasn't updated`() = viewModelTest {
        cartRepository.changeError = IOException("Disk full")
        val viewModel = cartViewModel()

        viewModel.effects.test {
            viewModel.onEvent(CartEvent.OnQuantityChange("1", quantity = 3))

            assertThat(awaitItem()).isEqualTo(snackBar(R.string.cart_update_error))
        }
    }

    @Test
    fun `failed cart change is not reported`() = viewModelTest {
        cartRepository.changeError = IOException("Disk full")
        val viewModel = cartViewModel()

        viewModel.onEvent(CartEvent.OnQuantityChange("1", quantity = 3))

        assertThat(analytics.sentEvents).isEmpty()
    }

    private fun cartViewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) = CartViewModel(
        observeCart = ObserveCartUseCase(cartRepository),
        updateCartQuantity = UpdateCartQuantityUseCase(cartRepository),
        removeFromCart = RemoveFromCartUseCase(cartRepository),
        validateCart = ValidateCartUseCase(cartRepository),
        acceptCartChanges = AcceptCartChangesUseCase(cartRepository),
        applyPromoCode = ApplyPromoCodeUseCase(cartRepository),
        removePromoCode = RemovePromoCodeUseCase(cartRepository),
        clearCartItems = ClearCartItemsUseCase(cartRepository),
        analytics = analytics.analytics,
        savedStateHandle = savedStateHandle,
    )

    private fun invalid(vararg issues: ItemIssue) =
        CartValidationResult.Invalid(issues.toList(), isPromoCodeValid = true)

    private fun snackBar(message: Int) = CartEffect.ShowSnackBar(UiText.Resource(message))

    private fun CartUiState.loaded() = content as CartUiState.Content.Loaded
}
