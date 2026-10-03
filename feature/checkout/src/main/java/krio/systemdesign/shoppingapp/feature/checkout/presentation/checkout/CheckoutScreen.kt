package krio.systemdesign.shoppingapp.feature.checkout.presentation.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import krio.systemdesign.shoppingapp.core.ui.components.bars.TotalBottomBar
import krio.systemdesign.shoppingapp.core.ui.components.buttons.CloseIconButton
import krio.systemdesign.shoppingapp.core.ui.components.buttons.SingleChoiceButtons
import krio.systemdesign.shoppingapp.core.ui.components.cards.OrderItemRow
import krio.systemdesign.shoppingapp.core.ui.components.cards.OrderTotals
import krio.systemdesign.shoppingapp.core.ui.components.cards.SectionCard
import krio.systemdesign.shoppingapp.core.ui.components.inputs.AppTextField
import krio.systemdesign.shoppingapp.core.ui.components.notices.AppliedPromoCodeNotice
import krio.systemdesign.shoppingapp.core.ui.components.notices.Notice
import krio.systemdesign.shoppingapp.core.ui.components.notices.NoticeStyle
import krio.systemdesign.shoppingapp.core.ui.components.screenstates.EmptyState
import krio.systemdesign.shoppingapp.core.ui.icons.AppIcons
import krio.systemdesign.shoppingapp.core.ui.text.asString
import krio.systemdesign.shoppingapp.core.ui.text.formatPrice
import krio.systemdesign.shoppingapp.core.ui.theme.Spacing
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
                            icon = AppIcons.Info,
                            title = stringResource(R.string.checkout_demo_order_notice),
                            style = NoticeStyle.Neutral,
                        )
                    },
                )
            }
        },
    ) { innerPadding ->
        // Пока корзина не прочитана из базы (доли секунды), ничего не показываем: иначе на миг появилась бы пустая корзина.
        if (uiState.isLoading) return@Scaffold
        if (uiState.isEmpty && !uiState.isSubmitting) {
            EmptyState(
                icon = AppIcons.EmptyCart,
                title = stringResource(R.string.checkout_empty_title),
                message = stringResource(R.string.checkout_empty_message),
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
            .padding(Spacing.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(Spacing.SectionSpacing),
    ) {
        SectionCard(
            icon = Icons.Outlined.ShoppingBag,
            title = stringResource(R.string.checkout_order_items),
        ) {
            uiState.items.forEach { item ->
                OrderItemRow(
                    name = item.name,
                    imageUrl = item.imageUrl,
                    quantity = item.quantity,
                    unitPrice = formatPrice(item.price),
                    total = formatPrice(item.price * item.quantity),
                )
            }
        }

        SectionCard(
            icon = Icons.Outlined.LocationOn,
            title = stringResource(R.string.checkout_delivery_address),
        ) {
            // Улица и квартира в одну строку: квартира короткая, отдельная строка для неё — пустое место.
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppTextField(
                    value = uiState.street,
                    onValueChange = { onEvent(CheckoutEvent.OnStreetChange(it)) },
                    label = stringResource(R.string.checkout_street),
                    enabled = !uiState.isSubmitting,
                    modifier = Modifier.weight(1f),
                )
                AppTextField(
                    value = uiState.apartment,
                    onValueChange = { onEvent(CheckoutEvent.OnApartmentChange(it)) },
                    label = stringResource(R.string.checkout_apartment),
                    enabled = !uiState.isSubmitting,
                    modifier = Modifier.width(APARTMENT_FIELD_WIDTH),
                )
            }
            AppTextField(
                value = uiState.courierComment,
                onValueChange = { onEvent(CheckoutEvent.OnCourierCommentChange(it)) },
                label = stringResource(R.string.checkout_courier_comment),
                enabled = !uiState.isSubmitting,
                singleLine = false,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        SectionCard(
            icon = Icons.Outlined.AccountBalanceWallet,
            title = stringResource(R.string.checkout_payment),
        ) {
            // Переключатель на всю ширину, как выбор темы в настройках.
            SingleChoiceButtons(
                options = PaymentMethod.entries,
                selected = uiState.paymentMethod,
                onSelect = { onEvent(CheckoutEvent.OnPaymentMethodChange(it)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSubmitting,
            ) { method ->
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

        SectionCard(
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
