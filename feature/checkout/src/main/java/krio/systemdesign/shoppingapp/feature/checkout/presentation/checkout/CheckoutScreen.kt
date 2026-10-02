package krio.systemdesign.shoppingapp.feature.checkout.presentation.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import krio.systemdesign.shoppingapp.core.ui.components.AppCard
import krio.systemdesign.shoppingapp.core.ui.components.AppliedPromoCodeNotice
import krio.systemdesign.shoppingapp.core.ui.components.CloseIconButton
import krio.systemdesign.shoppingapp.core.ui.components.Notice
import krio.systemdesign.shoppingapp.core.ui.components.OrderTotals
import krio.systemdesign.shoppingapp.core.ui.components.ProductImage
import krio.systemdesign.shoppingapp.core.ui.components.TotalBottomBar
import krio.systemdesign.shoppingapp.core.ui.text.asString
import krio.systemdesign.shoppingapp.core.ui.text.formatPrice
import krio.systemdesign.shoppingapp.domain.model.CartItem
import krio.systemdesign.shoppingapp.feature.checkout.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onClose: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                CheckoutEffect.Close -> onClose()
                is CheckoutEffect.ShowSnackBar -> {
                    launch { snackbarHostState.showSnackbar(effect.message.asString(resources)) }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.checkout_title)) },
                actions = {
                    CloseIconButton(onClick = onClose)
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (!uiState.isEmpty || uiState.isSubmitting) {
                TotalBottomBar(
                    total = formatPrice(uiState.totalPrice),
                    actionText = stringResource(R.string.checkout_place_order),
                    enabled = uiState.canSubmit,
                    isLoading = uiState.isSubmitting,
                    onAction = { viewModel.onEvent(CheckoutEvent.OnPlaceOrderClick) },
                    header = {
                        // Приложение демонстрационное: заказ никуда не уходит, оформление только сбрасывает корзину.
                        // Серая плашка, а не цветная: это пояснение, а не предупреждение о проблеме.
                        Notice(
                            icon = Icons.Outlined.Info,
                            title = stringResource(R.string.checkout_demo_order_notice),
                            accentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                )
            }
        },
    ) { innerPadding ->
        // Пока корзина не прочитана из базы (доли секунды), ничего не показываем: иначе на миг появилась бы пустая корзина.
        if (uiState.isLoading) return@Scaffold
        if (uiState.isEmpty && !uiState.isSubmitting) {
            EmptyCheckout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        } else {
            CheckoutContent(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        }
    }
}

// Экран собран из карточек, как остальные: у каждого блока свой заголовок с иконкой.
@Composable
private fun CheckoutContent(
    uiState: CheckoutUiState,
    onEvent: (CheckoutEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CheckoutSection(
            icon = Icons.Outlined.ShoppingBag,
            title = stringResource(R.string.checkout_order_items),
        ) {
            uiState.items.forEach { item ->
                OrderSummaryItem(item = item)
            }
        }

        CheckoutSection(
            icon = Icons.Outlined.LocationOn,
            title = stringResource(R.string.checkout_delivery_address),
        ) {
            // Улица и квартира в одну строку: квартира короткая, отдельная строка для неё — пустое место.
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CheckoutTextField(
                    value = uiState.street,
                    onValueChange = { onEvent(CheckoutEvent.OnStreetChange(it)) },
                    label = stringResource(R.string.checkout_street),
                    enabled = !uiState.isSubmitting,
                    modifier = Modifier.weight(1f),
                )
                CheckoutTextField(
                    value = uiState.apartment,
                    onValueChange = { onEvent(CheckoutEvent.OnApartmentChange(it)) },
                    label = stringResource(R.string.checkout_apartment),
                    enabled = !uiState.isSubmitting,
                    modifier = Modifier.width(APARTMENT_FIELD_WIDTH),
                )
            }
            CheckoutTextField(
                value = uiState.courierComment,
                onValueChange = { onEvent(CheckoutEvent.OnCourierCommentChange(it)) },
                label = stringResource(R.string.checkout_courier_comment),
                enabled = !uiState.isSubmitting,
                singleLine = false,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        CheckoutSection(
            icon = Icons.Outlined.AccountBalanceWallet,
            title = stringResource(R.string.checkout_payment),
        ) {
            PaymentMethodButtons(
                selected = uiState.paymentMethod,
                enabled = !uiState.isSubmitting,
                onSelect = { onEvent(CheckoutEvent.OnPaymentMethodChange(it)) },
            )
        }

        CheckoutSection(
            icon = Icons.Outlined.Receipt,
            title = stringResource(R.string.checkout_order_total),
        ) {
            // Как в корзине: плашка промокода, под ней линия, потом суммы.
            val promoCode = uiState.promoCode
            if (promoCode != null) {
                AppliedPromoCodeNotice(
                    code = promoCode.code,
                    discountPercent = promoCode.discountPercent,
                )
                HorizontalDivider()
            }
            OrderTotals(
                subtotal = formatPrice(uiState.subtotal),
                total = formatPrice(uiState.totalPrice),
                discount = if (promoCode != null) "−${formatPrice(uiState.discount)}" else null,
            )
        }
    }
}

// Поле ввода внутри карточки: залитое, без рамки и со скруглением, как у плашек и карточек.
// Обычное OutlinedTextField с острыми углами и рамкой в скруглённой карточке выглядит чужим.
@Composable
private fun CheckoutTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(label) },
        enabled = enabled,
        singleLine = singleLine,
        // Многострочное поле растёт вместе с текстом, но не больше трёх строк, дальше прокручивается.
        maxLines = if (singleLine) 1 else 3,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
    )
}

// Блок экрана: карточка с заголовком, как карточка с промокодами на экране промокода.
@Composable
private fun CheckoutSection(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    AppCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
            content()
        }
    }
}

// Строка товара: картинка, название, цена за штуку и количество, а справа сумма — как в корзине,
// только без кнопок: здесь заказ уже не меняют.
@Composable
private fun OrderSummaryItem(
    item: CartItem,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProductImage(
            imageUrl = item.imageUrl,
            contentDescription = item.name,
            modifier = Modifier.size(56.dp),
            cornerRadius = 8.dp,
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${item.quantity} × ${formatPrice(item.price)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = formatPrice(item.price * item.quantity),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

// Способ оплаты — переключатель на всю ширину, как выбор темы в настройках.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentMethodButtons(
    selected: PaymentMethod,
    enabled: Boolean,
    onSelect: (PaymentMethod) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Выбранный вариант того же цвета, что и выбранная вкладка в нижней панели.
    val colors = SegmentedButtonDefaults.colors(
        activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
        activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        PaymentMethod.entries.forEachIndexed { index, method ->
            SegmentedButton(
                selected = method == selected,
                onClick = { onSelect(method) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = PaymentMethod.entries.size),
                enabled = enabled,
                colors = colors,
                icon = {},
            ) {
                // Содержимое кнопки само в ряд не выстраивается: без Row иконка и текст лягут друг на друга.
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = method.icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(method.titleRes))
                }
            }
        }
    }
}

private val APARTMENT_FIELD_WIDTH = 120.dp

private val PaymentMethod.titleRes: Int
    get() = when (this) {
        PaymentMethod.Card -> R.string.checkout_payment_card
        PaymentMethod.Cash -> R.string.checkout_payment_cash
    }

private val PaymentMethod.icon: ImageVector
    get() = when (this) {
        PaymentMethod.Card -> Icons.Outlined.CreditCard
        PaymentMethod.Cash -> Icons.Outlined.Payments
    }

@Composable
private fun EmptyCheckout(modifier: Modifier = Modifier) {
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
            text = stringResource(R.string.checkout_empty_title),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.checkout_empty_message),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
