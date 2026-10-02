package krio.systemdesign.shoppingapp.feature.cart.presentation.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RemoveShoppingCart
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
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import krio.systemdesign.shoppingapp.core.ui.components.AppCard
import krio.systemdesign.shoppingapp.core.ui.components.AppliedPromoCodeNotice
import krio.systemdesign.shoppingapp.core.ui.components.CartQuantityControl
import krio.systemdesign.shoppingapp.core.ui.components.ConfirmationDialog
import krio.systemdesign.shoppingapp.core.ui.components.EmptyState
import krio.systemdesign.shoppingapp.core.ui.components.Notice
import krio.systemdesign.shoppingapp.core.ui.components.NoticeStyle
import krio.systemdesign.shoppingapp.core.ui.components.NoticeWithAction
import krio.systemdesign.shoppingapp.core.ui.components.OrderTotals
import krio.systemdesign.shoppingapp.core.ui.components.TotalBottomBar
import krio.systemdesign.shoppingapp.core.ui.components.ProductCard
import krio.systemdesign.shoppingapp.core.ui.components.ProductImageKey
import krio.systemdesign.shoppingapp.core.ui.icons.AppIcons
import krio.systemdesign.shoppingapp.core.ui.text.asString
import krio.systemdesign.shoppingapp.core.ui.text.formatPrice
import krio.systemdesign.shoppingapp.core.ui.theme.Spacing
import krio.systemdesign.shoppingapp.core.ui.theme.totalsEmphasized
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
    onOpenProduct: (productId: String, productName: String, imageUrl: String) -> Unit,
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
                is CartEffect.NavigateToProduct -> {
                    onOpenProduct(effect.productId, effect.productName, effect.imageUrl)
                }
                is CartEffect.ShowSnackBar -> {
                    launch { snackbarHostState.showSnackbar(effect.message.asString(resources)) }
                }
            }
        }
    }

    if (uiState.isClearCartDialogVisible) {
        ConfirmationDialog(
            title = stringResource(R.string.cart_clear_dialog_title),
            text = stringResource(R.string.cart_clear_dialog_message),
            confirmText = stringResource(R.string.cart_clear_dialog_confirm),
            onConfirm = { viewModel.onEvent(CartEvent.OnClearCartConfirmed) },
            onDismiss = { viewModel.onEvent(CartEvent.OnClearCartDismiss) },
            isDestructive = true,
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
                    enabled = uiState.canCheckout && !uiState.isCheckingOut,
                    isLoading = uiState.isCheckingOut,
                    onAction = { viewModel.onEvent(CartEvent.OnCheckoutClick) },
                    header = if (uiState.priceChangeCount > 0 || uiState.unavailableItemCount > 0 || uiState.notEnoughStockItemCount > 0) {
                        {
                            CartChangesActions(
                                priceChangeCount = uiState.priceChangeCount,
                                unavailableItemCount = uiState.unavailableItemCount,
                                notEnoughStockItemCount = uiState.notEnoughStockItemCount,
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
        // Пока корзина не прочитана из базы (доли секунды при первом открытии), ничего не показываем:
        // иначе на миг появилось бы «Корзина пуста».
        if (uiState.isLoading) return@Scaffold
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
                contentPadding = PaddingValues(Spacing.ScreenPadding),
                verticalArrangement = Arrangement.spacedBy(Spacing.CardSpacing),
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
                            viewModel.onEvent(CartEvent.OnItemClick(item.productId, item.name, item.imageUrl))
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
    ProductCard(
        name = item.name,
        imageUrl = item.imageUrl,
        price = formatPrice(item.price * item.quantity),
        onClick = onClick,
        modifier = modifier,
        unitPrice = formatPrice(item.price),
        sharedElementKey = ProductImageKey(item.productId),
        isDimmed = issues.any { it is ItemIssue.Unavailable },
    ) {
        if (issues.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                issues.forEach { ItemIssueNotice(issue = it) }
            }
        }
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
                .padding(Spacing.CardPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Тот же стиль, что у «Итого» в OrderTotals, иначе заголовок выглядит другим шрифтом.
            Text(
                text = stringResource(R.string.cart_order_total),
                style = MaterialTheme.typography.totalsEmphasized,
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
    Box(modifier = modifier) {
        EmptyState(
            icon = AppIcons.EmptyCart,
            title = stringResource(R.string.cart_empty_title),
            message = stringResource(R.string.cart_empty_message),
            modifier = Modifier.align(Alignment.Center),
        )
        // Промокод переживает удаление товаров: показываем его, чтобы было видно,
        // что он сработает для следующих покупок, и чтобы его можно было убрать.
        if (promoCode != null) {
            CartPromoCode(
                promoCode = promoCode,
                isValid = isPromoCodeValid,
                onRemove = onRemovePromo,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp),
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
        AppliedPromoCodeNotice(
            code = promoCode.code,
            discountPercent = promoCode.discountPercent,
            onRemove = onRemove,
            modifier = modifier,
        )
    } else {
        NoticeWithAction(
            icon = AppIcons.Error,
            title = stringResource(R.string.cart_promo_invalid, promoCode.code),
            subtitle = stringResource(R.string.cart_promo_invalid_hint),
            style = NoticeStyle.Error,
            actionText = stringResource(R.string.cart_promo_remove),
            onAction = onRemove,
            modifier = modifier,
        )
    }
}

// Плашка в карточке товара: что нашла проверка корзины. Все такие плашки красные: любое из этих изменений
// мешает оформить заказ, пока пользователь его не исправит. Различаются они иконкой и текстом.
@Composable
private fun ItemIssueNotice(
    issue: ItemIssue,
    modifier: Modifier = Modifier,
) {
    when (issue) {
        is ItemIssue.Unavailable -> Notice(
            icon = AppIcons.OutOfStock,
            title = stringResource(R.string.cart_item_unavailable),
            style = NoticeStyle.Error,
            modifier = modifier,
        )
        is ItemIssue.PriceChanged -> Notice(
            icon = AppIcons.PriceChanged,
            // Старая цена видна строкой выше, в плашке только новая.
            title = stringResource(R.string.cart_item_price_changed, formatPrice(issue.newPrice)),
            style = NoticeStyle.Error,
            modifier = modifier,
        )
        is ItemIssue.NotEnoughStock -> Notice(
            icon = AppIcons.NotEnoughStock,
            title = stringResource(R.string.cart_item_not_enough_stock, issue.availableQuantity),
            style = NoticeStyle.Error,
            modifier = modifier,
        )
    }
}


// Над «Итого» — по плашке на каждый вид изменений: сколько товаров затронуто и кнопка, которая исправляет все сразу.
// У нехватки остатка кнопки нет: количество пользователь уменьшает сам кнопкой «−».
// Каждая плашка видна, только пока такие изменения не исправлены. Цвета у всех красные, как у плашек в карточках.
@Composable
private fun CartChangesActions(
    priceChangeCount: Int,
    unavailableItemCount: Int,
    notEnoughStockItemCount: Int,
    onAcceptNewPrices: () -> Unit,
    onRemoveUnavailable: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Плашка без кнопки ниже плашки с кнопкой, поэтому высота у всех задана одна — как с кнопкой.
    val noticeModifier = Modifier.heightIn(min = CHANGES_NOTICE_MIN_HEIGHT)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (priceChangeCount > 0) {
            NoticeWithAction(
                icon = AppIcons.PriceChanged,
                title = pluralStringResource(R.plurals.cart_price_changes, priceChangeCount, priceChangeCount),
                style = NoticeStyle.Error,
                actionText = stringResource(R.string.cart_accept_new_prices),
                onAction = onAcceptNewPrices,
                modifier = noticeModifier,
            )
        }
        if (unavailableItemCount > 0) {
            NoticeWithAction(
                icon = AppIcons.OutOfStock,
                title = pluralStringResource(R.plurals.cart_unavailable_items, unavailableItemCount, unavailableItemCount),
                style = NoticeStyle.Error,
                actionText = stringResource(R.string.cart_remove_unavailable),
                onAction = onRemoveUnavailable,
                modifier = noticeModifier,
            )
        }
        if (notEnoughStockItemCount > 0) {
            Notice(
                icon = AppIcons.NotEnoughStock,
                title = pluralStringResource(R.plurals.cart_not_enough_stock_items, notEnoughStockItemCount, notEnoughStockItemCount),
                style = NoticeStyle.Error,
                modifier = noticeModifier,
            )
        }
    }
}


// Насыщенность фона плашки относительно её цвета акцента.

// Высота плашки с кнопкой: кнопка не ниже 48dp, чтобы в неё было легко попасть, плюс отступы плашки по 4dp.
private val CHANGES_NOTICE_MIN_HEIGHT = 56.dp
