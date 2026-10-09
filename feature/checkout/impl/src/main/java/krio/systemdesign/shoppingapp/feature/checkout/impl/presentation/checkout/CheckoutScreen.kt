package krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.checkout

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import krio.systemdesign.shoppingapp.core.composeutils.effects.ObserveEffects
import krio.systemdesign.shoppingapp.core.composeutils.text.asString
import krio.systemdesign.shoppingapp.core.designsystem.components.buttons.CloseIconButton
import krio.systemdesign.shoppingapp.core.designsystem.components.screenstates.EmptyState
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.ShoppingCart
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.designsystem.theme.Spacing
import krio.systemdesign.shoppingapp.core.designsystem.theme.contentBarWidth
import krio.systemdesign.shoppingapp.core.designsystem.theme.contentWidth
import krio.systemdesign.shoppingapp.feature.checkout.impl.R
import krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.checkout.components.CheckoutBottomBar
import krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.checkout.components.DeliveryAddressSection
import krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.checkout.components.OrderItemsSection
import krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.checkout.components.OrderTotalSection
import krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.checkout.components.PaymentSection
import krio.systemdesign.shoppingapp.shared.domain.model.CartItem
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode

@Composable
internal fun CheckoutScreen(
    onClose: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    ObserveEffects(viewModel.effects) { effect ->
        when (effect) {
            CheckoutEffect.Close -> navigate { onClose() }
            is CheckoutEffect.ShowSnackBar -> showSnackbar(snackbarHostState, effect.message.asString(resources))
        }
    }

    CheckoutScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
internal fun CheckoutScreen(
    uiState: CheckoutUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (CheckoutEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.imePadding(),
        topBar = { CheckoutTopBar(onClose = { onEvent(CheckoutEvent.OnCloseClick) }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            val order = uiState.order
            if (order is CheckoutUiState.Order.Loaded && uiState.showsOrder) {
                CheckoutBottomBar(
                    total = order.total,
                    canSubmit = uiState.canSubmit,
                    isSubmitting = uiState.isSubmitting,
                    onPlaceOrder = { onEvent(CheckoutEvent.OnPlaceOrderClick) },
                )
            }
        },
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        when (val order = uiState.order) {
            // Waiting for the cart from Room (a split second): drawing nothing avoids flashing the empty cart.
            CheckoutUiState.Order.Loading -> Unit
            is CheckoutUiState.Order.Loaded -> if (uiState.showsOrder) {
                CheckoutContent(
                    order = order,
                    address = uiState.address,
                    paymentMethod = uiState.paymentMethod,
                    enabled = !uiState.isSubmitting,
                    onEvent = onEvent,
                    modifier = contentModifier,
                )
            } else {
                // Not reachable today: the cart doesn't open checkout when it's empty, and there's no deep link
                // to checkout. Kept so the screen still makes sense if it's ever opened another way.
                CheckoutEmptyState(modifier = contentModifier)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CheckoutTopBar(onClose: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.checkout_title)) },
        modifier = Modifier.contentBarWidth(),
        actions = {
            CloseIconButton(onClick = onClose)
        },
    )
}

@Composable
private fun CheckoutEmptyState(modifier: Modifier = Modifier) {
    EmptyState(
        icon = AppIcons.ShoppingCart,
        title = stringResource(R.string.checkout_empty_title),
        message = stringResource(R.string.checkout_empty_message),
        modifier = modifier,
    )
}

@Composable
private fun CheckoutContent(
    order: CheckoutUiState.Order.Loaded,
    address: CheckoutUiState.Address,
    paymentMethod: CheckoutUiState.PaymentMethod,
    enabled: Boolean,
    onEvent: (CheckoutEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(Spacing.ScreenPadding)
            .contentWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.SectionSpacing),
    ) {
        OrderItemsSection(items = order.items)
        DeliveryAddressSection(address = address, enabled = enabled)
        PaymentSection(
            selected = paymentMethod,
            onSelect = { onEvent(CheckoutEvent.OnPaymentMethodChange(it)) },
            enabled = enabled,
        )
        OrderTotalSection(
            subtotal = order.subtotal,
            discount = order.discount,
            total = order.total,
            promoCode = order.promoCode,
        )
    }
}

private fun previewState(
    isSubmitting: Boolean = false,
    empty: Boolean = false,
) = CheckoutUiState(
    order = CheckoutUiState.Order.Loaded(
        items = if (empty) {
            emptyList()
        } else {
            listOf(
                CartItem("1", "Wireless Headphones", "", price = 14999, quantity = 1, availableQuantity = 10),
                CartItem("2", "Mechanical Keyboard", "", price = 10995, quantity = 1, availableQuantity = 5),
            )
        },
        subtotal = 25994,
        discount = 2599,
        total = 23395,
        promoCode = PromoCode("SALE10", 10),
    ),
    address = CheckoutUiState.Address(
        street = TextFieldState("Baker Street, 221"),
        apartment = TextFieldState("B"),
        courierComment = TextFieldState(),
    ),
    isSubmitting = isSubmitting,
)

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CheckoutScreenPreview() {
    ShoppingAppTheme {
        CheckoutScreen(
            uiState = previewState(),
            snackbarHostState = remember { SnackbarHostState() },
            onEvent = {},
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CheckoutScreenSubmittingPreview() {
    ShoppingAppTheme {
        CheckoutScreen(
            uiState = previewState(isSubmitting = true),
            snackbarHostState = remember { SnackbarHostState() },
            onEvent = {},
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CheckoutScreenEmptyPreview() {
    ShoppingAppTheme {
        CheckoutScreen(
            uiState = previewState(empty = true),
            snackbarHostState = remember { SnackbarHostState() },
            onEvent = {},
        )
    }
}
