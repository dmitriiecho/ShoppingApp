package krio.systemdesign.shoppingapp.feature.checkout.presentation.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import krio.systemdesign.shoppingapp.core.ui.components.CloseIconButton
import krio.systemdesign.shoppingapp.domain.model.CartItem
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onClose: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                CheckoutEffect.Close -> onClose()
                is CheckoutEffect.ShowSnackBar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = { Text("Оформление заказа") },
                actions = {
                    CloseIconButton(onClick = onClose)
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (!uiState.isEmpty || uiState.isSubmitting) {
                CheckoutBottomBar(
                    totalPrice = uiState.totalPrice,
                    canSubmit = uiState.canSubmit,
                    isSubmitting = uiState.isSubmitting,
                    onPlaceOrder = { viewModel.onEvent(CheckoutEvent.OnPlaceOrderClick) },
                )
            }
        },
    ) { innerPadding ->
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
        Text(
            text = "Состав заказа",
            style = MaterialTheme.typography.titleMedium,
        )
        uiState.items.forEach { item ->
            OrderSummaryItem(item = item)
        }

        HorizontalDivider()

        Text(
            text = "Адрес доставки",
            style = MaterialTheme.typography.titleMedium,
        )
        OutlinedTextField(
            value = uiState.street,
            onValueChange = { onEvent(CheckoutEvent.OnStreetChange(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Улица") },
            supportingText = { Text("Обязательно") },
            singleLine = true,
            enabled = !uiState.isSubmitting,
        )
        OutlinedTextField(
            value = uiState.apartment,
            onValueChange = { onEvent(CheckoutEvent.OnApartmentChange(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Квартира") },
            singleLine = true,
            enabled = !uiState.isSubmitting,
        )
        OutlinedTextField(
            value = uiState.courierComment,
            onValueChange = { onEvent(CheckoutEvent.OnCourierCommentChange(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Комментарий курьеру") },
            minLines = 2,
            enabled = !uiState.isSubmitting,
        )

        HorizontalDivider()

        Text(
            text = "Оплата",
            style = MaterialTheme.typography.titleMedium,
        )
        PaymentMethodRow(
            label = "Карта",
            selected = uiState.paymentMethod == PaymentMethod.Card,
            enabled = !uiState.isSubmitting,
            onSelect = { onEvent(CheckoutEvent.OnPaymentMethodChange(PaymentMethod.Card)) },
        )
        PaymentMethodRow(
            label = "Наличные",
            selected = uiState.paymentMethod == PaymentMethod.Cash,
            enabled = !uiState.isSubmitting,
            onSelect = { onEvent(CheckoutEvent.OnPaymentMethodChange(PaymentMethod.Cash)) },
        )
    }
}

@Composable
private fun OrderSummaryItem(
    item: CartItem,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "× ${item.quantity}",
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
}

@Composable
private fun PaymentMethodRow(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onSelect,
                enabled = enabled,
                role = Role.RadioButton,
            )
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            enabled = enabled,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun CheckoutBottomBar(
    totalPrice: Long,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    onPlaceOrder: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 3.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Итого",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = formatPrice(totalPrice),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onPlaceOrder,
                enabled = canSubmit,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text("Оформить заказ")
                }
            }
        }
    }
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
            text = "Корзина пуста",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Добавьте товары из каталога",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun formatPrice(amountMinor: Long): String {
    val format = NumberFormat.getNumberInstance(Locale.forLanguageTag("ru-RU")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return "${format.format(amountMinor / 100.0)} ₽"
}
