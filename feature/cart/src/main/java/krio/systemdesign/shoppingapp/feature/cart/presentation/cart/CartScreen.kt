package krio.systemdesign.shoppingapp.feature.cart.presentation.cart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.ProductionQuantityLimits
import androidx.compose.material.icons.outlined.RemoveShoppingCart
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import krio.systemdesign.shoppingapp.core.ui.components.AppCard
import krio.systemdesign.shoppingapp.core.ui.components.CartQuantityControl
import krio.systemdesign.shoppingapp.core.ui.components.OrderTotals
import krio.systemdesign.shoppingapp.core.ui.components.TotalBottomBar
import krio.systemdesign.shoppingapp.core.ui.components.ProductImage
import krio.systemdesign.shoppingapp.core.ui.text.asString
import krio.systemdesign.shoppingapp.core.ui.text.formatPrice
import krio.systemdesign.shoppingapp.core.ui.theme.success
import krio.systemdesign.shoppingapp.domain.model.CartItem
import krio.systemdesign.shoppingapp.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.domain.model.PromoCode
import krio.systemdesign.shoppingapp.feature.cart.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

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
                    launch { snackbarHostState.showSnackbar(effect.message.asString(resources)) }
                }
            }
        }
    }

    if (uiState.isClearCartDialogVisible) {
        ClearCartDialog(
            onConfirm = { viewModel.onEvent(CartEvent.OnClearCartConfirmed) },
            onDismiss = { viewModel.onEvent(CartEvent.OnClearCartDismiss) },
        )
    }

    // Корзина проверяется каждый раз, когда экран становится видимым, чтобы пометки об изменениях были сразу.
    LifecycleStartEffect(Unit) {
        viewModel.onEvent(CartEvent.OnScreenShown)
        onStopOrDispose { }
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
                    enabled = uiState.canCheckout && !uiState.isValidating,
                    isLoading = uiState.isValidating,
                    onAction = { viewModel.onEvent(CartEvent.OnCheckoutClick) },
                    header = if (uiState.priceChangeCount > 0 || uiState.unavailableItemCount > 0) {
                        {
                            CartChangesActions(
                                priceChangeCount = uiState.priceChangeCount,
                                unavailableItemCount = uiState.unavailableItemCount,
                                onAcceptNewPrices = { viewModel.onEvent(CartEvent.OnAcceptNewPricesClick) },
                                onRemoveUnavailable = { viewModel.onEvent(CartEvent.OnRemoveUnavailableClick) },
                            )
                        }
                    } else {
                        null
                    },
                )
            }
        },
    ) { innerPadding ->
        if (uiState.isEmpty) {
            EmptyCart(
                promoCode = uiState.promoCode,
                isPromoCodeValid = uiState.isPromoCodeValid,
                onRemovePromo = { viewModel.onEvent(CartEvent.OnRemovePromoClick) },
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
                        issues = uiState.itemIssues[item.productId] ?: persistentListOf(),
                        canIncrease = uiState.canIncrease(item),
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
    // Изменения, которые нашла проверка корзины и которые ещё не исправлены.
    issues: ImmutableList<ItemIssue>,
    canIncrease: Boolean,
    onClick: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Закончившийся товар приглушён, чтобы его было видно сразу, даже не читая плашку.
    val contentAlpha = if (issues.any { it is ItemIssue.Unavailable }) UNAVAILABLE_ALPHA else 1f
    AppCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick)
                    .alpha(contentAlpha),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProductImage(
                    imageUrl = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier.size(88.dp),
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
            if (issues.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    issues.forEach { ItemIssueNotice(issue = it) }
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
                // В корзине уже весь остаток или товар закончился: добавить ещё нельзя, уменьшить и удалить можно.
                canIncrease = canIncrease,
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
    // Отдельная карточка, как у товаров: сумма не сливается со списком.
    AppCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Тот же стиль, что у строк суммы в OrderTotals, иначе заголовок выглядит другим шрифтом.
            Text(
                text = stringResource(R.string.cart_order_total),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            )
            val promoCode = uiState.promoCode
            if (promoCode != null) {
                CartPromoCode(
                    promoCode = promoCode,
                    isValid = uiState.isPromoCodeValid,
                    onRemove = onRemovePromo,
                )
                HorizontalDivider()
            }
            OrderTotals(
                subtotal = formatPrice(uiState.subtotal),
                total = formatPrice(uiState.totalPrice),
                discount = if (promoCode != null && uiState.isPromoCodeValid) "−${formatPrice(uiState.discount)}" else null,
            )
        }
    }
}

@Composable
private fun EmptyCart(
    promoCode: PromoCode?,
    isPromoCodeValid: Boolean,
    onRemovePromo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Box, а не Column: надпись стоит по центру всего экрана и не сдвигается,
    // когда строка промокода появляется или исчезает.
    Box(modifier = modifier.padding(24.dp)) {
        Column(
            modifier = Modifier.align(Alignment.Center),
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
        // Промокод переживает удаление товаров: показываем его, чтобы было видно,
        // что он сработает для следующих покупок, и чтобы его можно было убрать.
        if (promoCode != null) {
            CartPromoCode(
                promoCode = promoCode,
                isValid = isPromoCodeValid,
                onRemove = onRemovePromo,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

// Применённый промокод — плашка с кнопкой «Убрать»: зелёная, пока он действует, и красная,
// если проверка корзины нашла, что он больше не действует, с подсказкой, что сделать.
@Composable
private fun CartPromoCode(
    promoCode: PromoCode,
    isValid: Boolean,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isValid) {
        CartNoticeWithAction(
            icon = Icons.Outlined.CheckCircle,
            title = stringResource(R.string.cart_promo_active, promoCode.code, promoCode.discountPercent),
            accentColor = MaterialTheme.colorScheme.success,
            actionText = stringResource(R.string.cart_promo_remove),
            onAction = onRemove,
            modifier = modifier,
        )
    } else {
        CartNoticeWithAction(
            icon = Icons.Outlined.ErrorOutline,
            title = stringResource(R.string.cart_promo_invalid, promoCode.code),
            subtitle = stringResource(R.string.cart_promo_invalid_hint),
            accentColor = MaterialTheme.colorScheme.error,
            actionText = stringResource(R.string.cart_promo_remove),
            onAction = onRemove,
            modifier = modifier,
        )
    }
}

// Плашка в карточке товара: что нашла проверка корзины.
@Composable
private fun ItemIssueNotice(
    issue: ItemIssue,
    modifier: Modifier = Modifier,
) {
    when (issue) {
        is ItemIssue.Unavailable -> CartNotice(
            icon = Icons.Outlined.Inventory2,
            title = stringResource(R.string.cart_item_unavailable),
            accentColor = MaterialTheme.colorScheme.error,
            modifier = modifier,
        )
        is ItemIssue.PriceChanged -> CartNotice(
            icon = Icons.Outlined.Sell,
            // Старая цена видна строкой выше, в плашке только новая.
            title = stringResource(R.string.cart_item_price_changed, formatPrice(issue.newPrice)),
            accentColor = MaterialTheme.colorScheme.primary,
            modifier = modifier,
        )
        // Общей плашки с кнопкой нет: количество пользователь уменьшает сам кнопкой «−».
        is ItemIssue.NotEnoughStock -> CartNotice(
            icon = Icons.Outlined.ProductionQuantityLimits,
            title = stringResource(R.string.cart_item_not_enough_stock, issue.availableQuantity),
            accentColor = MaterialTheme.colorScheme.primary,
            modifier = modifier,
        )
    }
}

// Плашка с иконкой для пометок на карточках товаров и у промокода.
// Фон — лёгкий оттенок цвета акцента, текст и иконка — сам цвет акцента.
@Composable
private fun CartNotice(
    icon: ImageVector,
    title: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    action: (@Composable () -> Unit)? = null,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = accentColor.copy(alpha = NOTICE_BACKGROUND_ALPHA),
        contentColor = accentColor,
    ) {
        Row(
            // У кнопки свои отступы и высота, поэтому с ней плашке свои почти не нужны.
            modifier = if (action != null) {
                Modifier.padding(start = 12.dp, top = 4.dp, bottom = 4.dp, end = 4.dp)
            } else {
                Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
            },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            action?.invoke()
        }
    }
}

// Над «Итого» — по плашке на каждый вид изменений: сколько товаров затронуто и кнопка, которая исправляет все сразу.
// Каждая видна, только когда ей есть что делать.
@Composable
private fun CartChangesActions(
    priceChangeCount: Int,
    unavailableItemCount: Int,
    onAcceptNewPrices: () -> Unit,
    onRemoveUnavailable: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (priceChangeCount > 0) {
            CartNoticeWithAction(
                icon = Icons.Outlined.Sell,
                title = pluralStringResource(R.plurals.cart_price_changes, priceChangeCount, priceChangeCount),
                accentColor = MaterialTheme.colorScheme.primary,
                actionText = stringResource(R.string.cart_accept_new_prices),
                onAction = onAcceptNewPrices,
            )
        }
        if (unavailableItemCount > 0) {
            CartNoticeWithAction(
                icon = Icons.Outlined.Inventory2,
                title = pluralStringResource(R.plurals.cart_unavailable_items, unavailableItemCount, unavailableItemCount),
                accentColor = MaterialTheme.colorScheme.error,
                actionText = stringResource(R.string.cart_remove_unavailable),
                onAction = onRemoveUnavailable,
            )
        }
    }
}

// Плашка с текстовой кнопкой того же цвета справа.
@Composable
private fun CartNoticeWithAction(
    icon: ImageVector,
    title: String,
    accentColor: Color,
    actionText: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    CartNotice(
        icon = icon,
        title = title,
        accentColor = accentColor,
        modifier = modifier,
        subtitle = subtitle,
        action = {
            TextButton(
                onClick = onAction,
                colors = ButtonDefaults.textButtonColors(contentColor = accentColor),
            ) {
                Text(actionText)
            }
        },
    )
}

@Composable
private fun ClearCartDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.cart_clear_dialog_title)) },
        text = { Text(stringResource(R.string.cart_clear_dialog_message)) },
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

// Прозрачность закончившегося товара в корзине.
private const val UNAVAILABLE_ALPHA = 0.5f

// Насыщенность фона плашки относительно её цвета акцента.
private const val NOTICE_BACKGROUND_ALPHA = 0.14f
