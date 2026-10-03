package krio.systemdesign.shoppingapp.feature.promo.presentation.promocode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import krio.systemdesign.shoppingapp.core.ui.components.buttons.LoadingButton
import krio.systemdesign.shoppingapp.core.ui.components.buttons.NavigateBackIconButton
import krio.systemdesign.shoppingapp.core.ui.components.cards.PromoCodeCoupon
import krio.systemdesign.shoppingapp.core.ui.components.cards.PromoCodeCouponPlaceholder
import krio.systemdesign.shoppingapp.core.ui.components.cards.SectionCard
import krio.systemdesign.shoppingapp.core.ui.components.loading.ShimmerPlaceholder
import krio.systemdesign.shoppingapp.core.ui.icons.AppIcons
import krio.systemdesign.shoppingapp.core.ui.text.asString
import krio.systemdesign.shoppingapp.core.ui.theme.Spacing
import krio.systemdesign.shoppingapp.domain.model.PromoCode
import krio.systemdesign.shoppingapp.feature.promo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoCodeScreen(
    onBack: () -> Unit,
    onCloseWithResult: (PromoCode) -> Unit,
    viewModel: PromoCodeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is PromoCodeEffect.CloseWithResult -> {
                    onCloseWithResult(effect.promoCode)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.promo_code)) },
                navigationIcon = {
                    NavigateBackIconButton(onClick = onBack)
                },
            )
        },
    ) { innerPadding ->
        val error = uiState.error

        // Прокрутка — чтобы подсказку под кнопкой можно было достать, когда открыта клавиатура.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(Spacing.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.SectionSpacing),
        ) {
            OutlinedTextField(
                value = uiState.promoCode,
                onValueChange = { viewModel.onEvent(PromoCodeEvent.OnPromoCodeChange(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.promo_code)) },
                isError = error != null,
                supportingText = if (error != null) {
                    { Text(error.asString()) }
                } else {
                    null
                },
                singleLine = true,
                enabled = !uiState.isLoading,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    autoCorrectEnabled = false,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { viewModel.onEvent(PromoCodeEvent.OnApplyClick) },
                ),
            )
            LoadingButton(
                text = stringResource(R.string.promo_apply),
                isLoading = uiState.isLoading,
                onClick = { viewModel.onEvent(PromoCodeEvent.OnApplyClick) },
                enabled = uiState.canApply,
                modifier = Modifier.fillMaxWidth(),
            )
            AvailablePromoCodesHint(
                state = uiState.availablePromoCodes,
                enabled = !uiState.isLoading,
                onCodeClick = { viewModel.onEvent(PromoCodeEvent.OnAvailablePromoCodeClick(it)) },
                onRetry = { viewModel.onEvent(PromoCodeEvent.OnRetryAvailablePromoCodes) },
            )
        }
    }
}

// Карточка со всеми кодами сервера: нажатие на код подставляет его в поле.
@Composable
private fun AvailablePromoCodesHint(
    state: AvailablePromoCodes,
    enabled: Boolean,
    onCodeClick: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionCard(
        icon = AppIcons.PromoCode,
        title = stringResource(R.string.promo_available_title),
        subtitle = stringResource(R.string.promo_available_hint),
        modifier = modifier,
    ) {
        when (state) {
            AvailablePromoCodes.Loading -> PromoCodeCouponsPlaceholder()
            is AvailablePromoCodes.Content -> if (state.promoCodes.isEmpty()) {
                Text(
                    text = stringResource(R.string.promo_available_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    state.promoCodes.forEach { promoCode ->
                        PromoCodeCoupon(
                            code = promoCode.code,
                            discountPercent = promoCode.discountPercent,
                            enabled = enabled,
                            onClick = { onCodeClick(promoCode.code) },
                        )
                    }
                }
            }
            AvailablePromoCodes.Error -> Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.promo_available_error),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = onRetry) {
                    Text(stringResource(R.string.promo_retry))
                }
            }
        }
    }
}

// Два купона-заглушки, пока коды грузятся. Стоят там же, где встанут купоны, и того же размера,
// поэтому карточка не меняет высоту, когда коды загрузились.
@Composable
private fun PromoCodeCouponsPlaceholder(modifier: Modifier = Modifier) {
    ShimmerPlaceholder(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(2) {
                PromoCodeCouponPlaceholder()
            }
        }
    }
}
