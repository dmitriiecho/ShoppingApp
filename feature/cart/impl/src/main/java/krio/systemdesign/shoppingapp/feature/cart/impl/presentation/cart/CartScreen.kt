package krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.persistentListOf
import krio.systemdesign.shoppingapp.core.composeutils.effects.ObserveEffects
import krio.systemdesign.shoppingapp.core.composeutils.text.asString
import krio.systemdesign.shoppingapp.core.designsystem.components.dialogs.ConfirmationDialog
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.designsystem.theme.Spacing
import krio.systemdesign.shoppingapp.feature.cart.impl.R
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart.components.CartBottomBar
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart.components.CartEmptyState
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart.components.CartItemCard
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart.components.CartTotalsCard
import krio.systemdesign.shoppingapp.feature.cart.ui.ClearCartIconButton

@Composable
internal fun CartScreen(
    onBack: () -> Unit,
    onOpenCheckout: () -> Unit,
    onOpenPromo: () -> Unit,
    onOpenProduct: (productId: String, productName: String, imageUrl: String) -> Unit,
    viewModel: CartViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    ObserveEffects(viewModel.effects) { effect ->
        when (effect) {
            CartEffect.NavigateBack -> navigate { onBack() }
            CartEffect.NavigateToCheckout -> navigate { onOpenCheckout() }
            CartEffect.NavigateToPromo -> navigate { onOpenPromo() }
            is CartEffect.NavigateToProduct -> navigate {
                onOpenProduct(effect.productId, effect.productName, effect.imageUrl)
            }
            is CartEffect.ShowSnackBar -> showSnackbar(snackbarHostState, effect.message.asString(resources))
        }
    }

    // The cart is validated every time the screen becomes visible, so the change notices are there right away.
    LifecycleStartEffect(Unit) {
        viewModel.onEvent(CartEvent.OnScreenShown)
        onStopOrDispose { }
    }

    CartScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
internal fun CartScreen(
    uiState: CartUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (CartEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.isClearCartDialogVisible) {
        ClearCartDialog(
            onConfirm = { onEvent(CartEvent.OnClearCartConfirmClick) },
            onDismiss = { onEvent(CartEvent.OnClearCartDialogDismiss) },
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CartTopBar(
                showsClearCart = uiState.hasItems,
                onPromoClick = { onEvent(CartEvent.OnPromoCodeClick) },
                onClearCartClick = { onEvent(CartEvent.OnClearCartClick) },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            val content = uiState.content
            if (content is CartUiState.Content.Loaded && uiState.hasItems) {
                CartBottomBar(
                    total = content.totals.total,
                    changes = content.changes,
                    canCheckout = uiState.canCheckout,
                    isOpeningCheckout = uiState.isOpeningCheckout,
                    onCheckout = { onEvent(CartEvent.OnCheckoutClick) },
                    onAcceptNewPrices = { onEvent(CartEvent.OnAcceptNewPricesClick) },
                    onRemoveUnavailable = { onEvent(CartEvent.OnRemoveUnavailableClick) },
                )
            }
        },
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        when (val content = uiState.content) {
            // Waiting for the cart from Room (a split second): drawing nothing avoids flashing the empty cart.
            CartUiState.Content.Loading -> Unit
            is CartUiState.Content.Loaded -> if (content.items.isEmpty()) {
                CartEmptyState(
                    promoCode = content.promoCode,
                    onRemovePromo = { onEvent(CartEvent.OnRemovePromoCodeClick) },
                    modifier = contentModifier,
                )
            } else {
                CartContent(content = content, onEvent = onEvent, modifier = contentModifier)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartTopBar(
    showsClearCart: Boolean,
    onPromoClick: () -> Unit,
    onClearCartClick: () -> Unit,
) {
    TopAppBar(
        title = { Text(stringResource(R.string.cart_title)) },
        actions = {
            TextButton(onClick = onPromoClick) {
                Text(stringResource(R.string.cart_promo_code))
            }
            if (showsClearCart) {
                ClearCartIconButton(onClick = onClearCartClick)
            }
        },
    )
}

@Composable
private fun ClearCartDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    ConfirmationDialog(
        title = stringResource(R.string.cart_clear_dialog_title),
        text = stringResource(R.string.cart_clear_dialog_message),
        confirmText = stringResource(R.string.cart_clear_dialog_confirm),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        isDestructive = true,
    )
}

@Composable
private fun CartContent(
    content: CartUiState.Content.Loaded,
    onEvent: (CartEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(Spacing.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(Spacing.CardSpacing),
    ) {
        items(
            items = content.items,
            key = { it.productId },
        ) { item ->
            CartItemCard(
                item = item,
                onClick = { onEvent(CartEvent.OnItemClick(it.productId, it.name, it.imageUrl)) },
                onQuantityChange = { productId, quantity -> onEvent(CartEvent.OnQuantityChange(productId, quantity)) },
                onRemove = { onEvent(CartEvent.OnRemoveFromCartClick(it)) },
            )
        }
        item(key = "totals") {
            CartTotalsCard(
                promoCode = content.promoCode,
                totals = content.totals,
                onRemovePromo = { onEvent(CartEvent.OnRemovePromoCodeClick) },
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

private fun previewState(empty: Boolean = false) = CartUiState(
    content = CartUiState.Content.Loaded(
        items = if (empty) {
            emptyList()
        } else {
            listOf(
                CartUiState.Item(
                    productId = "1",
                    name = "Wireless Headphones",
                    imageUrl = "",
                    price = 14999,
                    quantity = 1,
                    issues = persistentListOf(),
                    canAddOneMore = true,
                ),
                CartUiState.Item(
                    productId = "2",
                    name = "Mechanical Keyboard",
                    imageUrl = "",
                    price = 10995,
                    quantity = 1,
                    issues = persistentListOf(),
                    canAddOneMore = true,
                ),
            )
        },
        promoCode = CartUiState.AppliedPromoCode("SALE10", discountPercent = 10, isValid = true),
        totals = CartUiState.Totals(subtotal = 25994, discount = 2599, total = 23395),
        changes = CartUiState.Changes(priceChangeCount = 0, unavailableItemCount = 0, notEnoughStockItemCount = 0),
        allowsCheckout = !empty,
    ),
)

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CartScreenPreview() {
    ShoppingAppTheme {
        CartScreen(
            uiState = previewState(),
            snackbarHostState = remember { SnackbarHostState() },
            onEvent = {},
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CartScreenEmptyPreview() {
    ShoppingAppTheme {
        CartScreen(
            uiState = previewState(empty = true),
            snackbarHostState = remember { SnackbarHostState() },
            onEvent = {},
        )
    }
}
