package krio.systemdesign.shoppingapp.feature.promo.presentation.promocode

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import krio.systemdesign.shoppingapp.core.ui.components.AppCard
import krio.systemdesign.shoppingapp.core.ui.components.NavigateBackIconButton
import krio.systemdesign.shoppingapp.core.ui.components.ShimmerPlaceholder
import krio.systemdesign.shoppingapp.core.ui.components.shimmerShape
import krio.systemdesign.shoppingapp.core.ui.text.asString
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
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
            Button(
                onClick = { viewModel.onEvent(PromoCodeEvent.OnApplyClick) },
                enabled = uiState.canApply,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(stringResource(R.string.promo_apply))
                }
            }
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
    AppCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.ConfirmationNumber,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(R.string.promo_available_title),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        text = stringResource(R.string.promo_available_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
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
}

// Два купона-заглушки, пока коды грузятся. Стоят там же, где встанут купоны, и того же размера,
// поэтому карточка не меняет высоту, когда коды загрузились.
@Composable
private fun PromoCodeCouponsPlaceholder(modifier: Modifier = Modifier) {
    ShimmerPlaceholder(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        // Купон — нажимаемый Surface: места он занимает не меньше 48 dp в высоту, а рисуется
                        // по своему содержимому. Заглушка занимает место так же.
                        .minimumInteractiveComponentSize()
                        .size(width = PLACEHOLDER_COUPON_WIDTH, height = PLACEHOLDER_COUPON_HEIGHT)
                        .shimmerShape(RoundedCornerShape(COUPON_CORNER_RADIUS)),
                )
            }
        }
    }
}

// Код в виде купона: пунктирная рамка, моноширинный код и плашка со скидкой.
@Composable
private fun PromoCodeCoupon(
    code: String,
    discountPercent: Int,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(COUPON_CORNER_RADIUS)
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = shape,
        color = colors.primaryContainer,
        contentColor = colors.onPrimaryContainer,
    ) {
        Row(
            modifier = Modifier
                // Рамка рисуется внутри Surface, поверх его фона. Сдвиг на полтолщины — чтобы линия не обрезалась по краю.
                .drawBehind {
                    val strokeWidth = COUPON_BORDER_WIDTH.toPx()
                    val radius = COUPON_CORNER_RADIUS.toPx() - strokeWidth / 2
                    drawRoundRect(
                        color = colors.primary,
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(size.width - strokeWidth, size.height - strokeWidth),
                        cornerRadius = CornerRadius(radius),
                        style = Stroke(
                            width = strokeWidth,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx())),
                        ),
                    )
                }
                .padding(start = 14.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = code,
                style = MaterialTheme.typography.titleSmall,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.promo_discount_percent, discountPercent),
                style = MaterialTheme.typography.labelMedium,
                color = colors.onPrimary,
                modifier = Modifier
                    .background(colors.primary, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }
    }
}

private val COUPON_CORNER_RADIUS = 10.dp
private val COUPON_BORDER_WIDTH = 1.5.dp

// Размер купона с кодом из шести символов, например SALE10: высота — строка кода и отступы 8 dp сверху и снизу.
private val PLACEHOLDER_COUPON_WIDTH = 130.dp
private val PLACEHOLDER_COUPON_HEIGHT = 36.dp
