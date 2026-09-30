package krio.systemdesign.shoppingapp.feature.cart.presentation.cart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RemoveShoppingCart
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import krio.systemdesign.shoppingapp.core.ui.components.AppliedPromoCodeRow
import krio.systemdesign.shoppingapp.core.ui.components.CartQuantityControl
import krio.systemdesign.shoppingapp.core.ui.components.OrderTotals
import krio.systemdesign.shoppingapp.core.ui.components.TotalBottomBar
import krio.systemdesign.shoppingapp.core.ui.text.asString
import krio.systemdesign.shoppingapp.domain.model.CartItem
import krio.systemdesign.shoppingapp.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.feature.cart.R
import kotlinx.collections.immutable.ImmutableList
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel,
    onBack: () -> Unit,
    onOpenCheckout: () -> Unit,
    onOpenPromo: () -> Unit,
    onOpenProduct: (productId: String, productName: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                CartEffect.NavigateBack -> onBack()
                CartEffect.NavigateToCheckout -> onOpenCheckout()
                CartEffect.NavigateToPromo -> onOpenPromo()
                is CartEffect.NavigateToProduct -> onOpenProduct(effect.productId, effect.productName)
                is CartEffect.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(effect.message.asString(resources))
                }
            }
        }
    }

    if (uiState.isClearCartDialogVisible) {
        ClearCartDialog(
            hasPromoCode = uiState.promoCode != null,
            onConfirm = { viewModel.onEvent(CartEvent.OnClearCartConfirmed) },
            onDismiss = { viewModel.onEvent(CartEvent.OnClearCartDismiss) },
        )
    }

    if (uiState.issues.isNotEmpty()) {
        CartIssuesDialog(
            issues = uiState.issues,
            items = uiState.items,
            onAccept = { viewModel.onEvent(CartEvent.OnAcceptChanges) },
            onDismiss = { viewModel.onEvent(CartEvent.OnDismissIssues) },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.cart_title)) },
                actions = {
                    TextButton(onClick = { viewModel.onEvent(CartEvent.OnPromoClick) }) {
                        Text(stringResource(R.string.cart_promo_code))
                    }
                    if (!uiState.isEmpty) {
                        IconButton(onClick = { viewModel.onEvent(CartEvent.OnClearCartClick) }) {
                            Icon(
                                imageVector = Icons.Outlined.RemoveShoppingCart,
                                contentDescription = stringResource(R.string.cart_clear),
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (!uiState.isEmpty) {
                TotalBottomBar(
                    total = formatPrice(uiState.totalPrice),
                    actionText = stringResource(R.string.cart_checkout),
                    enabled = !uiState.isValidating,
                    isLoading = uiState.isValidating,
                    onAction = { viewModel.onEvent(CartEvent.OnCheckoutClick) },
                )
            }
        },
    ) { innerPadding ->
        if (uiState.isEmpty) {
            EmptyCart(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    items = uiState.items,
                    key = { it.productId },
                ) { item ->
                    CartListItem(
                        item = item,
                        onClick = {
                            viewModel.onEvent(CartEvent.OnItemClick(item.productId, item.name))
                        },
                        onIncrease = {
                            viewModel.onEvent(
                                CartEvent.OnUpdateQuantity(item.productId, item.quantity + 1),
                            )
                        },
                        onDecrease = {
                            viewModel.onEvent(
                                CartEvent.OnUpdateQuantity(item.productId, item.quantity - 1),
                            )
                        },
                        onRemove = {
                            viewModel.onEvent(CartEvent.OnRemoveItem(item.productId))
                        },
                    )
                }
                item(key = "totals") {
                    CartTotals(
                        uiState = uiState,
                        onRemovePromo = { viewModel.onEvent(CartEvent.OnRemovePromoClick) },
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CartListItem(
    item: CartItem,
    onClick: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = formatPrice(item.price),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = formatPrice(item.price * item.quantity),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            CartQuantityControl(
                quantity = item.quantity,
                onAdd = onIncrease,
                onIncrease = onIncrease,
                onDecrease = onDecrease,
                onRemoveAll = onRemove,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun CartTotals(
    uiState: CartUiState,
    onRemovePromo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        HorizontalDivider()
        Text(
            text = stringResource(R.string.cart_order_total),
            style = MaterialTheme.typography.titleMedium,
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            val promoCode = uiState.promoCode
            if (promoCode != null) {
                AppliedPromoCodeRow(
                    code = promoCode.code,
                    discountPercent = promoCode.discountPercent,
                    onRemove = onRemovePromo,
                )
            }
            OrderTotals(
                subtotal = formatPrice(uiState.subtotal),
                total = formatPrice(uiState.totalPrice),
                discount = if (promoCode != null) "−${formatPrice(uiState.discount)}" else null,
            )
        }
    }
}

@Composable
private fun EmptyCart(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Outlined.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.cart_empty_title),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.cart_empty_message),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ClearCartDialog(
    hasPromoCode: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.cart_clear_dialog_title)) },
        text = {
            Text(
                if (hasPromoCode) {
                    stringResource(R.string.cart_clear_dialog_message_with_promo)
                } else {
                    stringResource(R.string.cart_clear_dialog_message)
                },
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Text(stringResource(R.string.cart_clear_dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cart_cancel))
            }
        },
    )
}

@Composable
private fun CartIssuesDialog(
    issues: ImmutableList<ItemIssue>,
    items: ImmutableList<CartItem>,
    onAccept: () -> Unit,
    onDismiss: () -> Unit,
) {
    val names = items.associate { it.productId to it.name }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.cart_issues_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.cart_issues_dialog_message))
                issues.forEach { issue ->
                    Text(
                        text = issueMessage(issue, names),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onAccept) {
                Text(stringResource(R.string.cart_issues_dialog_accept))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cart_cancel))
            }
        },
    )
}

@Composable
private fun issueMessage(
    issue: ItemIssue,
    names: Map<String, String>,
): String = when (issue) {
    is ItemIssue.Unavailable -> {
        val name = names[issue.productId] ?: stringResource(R.string.cart_issue_unknown_product)
        stringResource(R.string.cart_issue_unavailable, name)
    }
    is ItemIssue.PriceChanged -> {
        val name = names[issue.productId] ?: stringResource(R.string.cart_issue_unknown_product)
        stringResource(R.string.cart_issue_price_changed, name, formatPrice(issue.newPrice))
    }
}

private fun formatPrice(amountMinor: Long): String {
    val format = NumberFormat.getNumberInstance(Locale.forLanguageTag("ru-RU")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return "${format.format(amountMinor / 100.0)} ₽"
}
