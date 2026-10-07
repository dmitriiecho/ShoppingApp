package krio.systemdesign.shoppingapp.feature.promo.impl.presentation.promocode.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.components.cards.SectionCard
import krio.systemdesign.shoppingapp.core.designsystem.components.loading.ShimmerPlaceholder
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.NoticeStyle
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.NoticeWithAction
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.ConfirmationNumber
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Error
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.designsystem.theme.Spacing
import krio.systemdesign.shoppingapp.feature.promo.impl.R
import krio.systemdesign.shoppingapp.feature.promo.impl.presentation.promocode.PromoCodeUiState
import krio.systemdesign.shoppingapp.feature.promo.ui.PromoCodeCoupon
import krio.systemdesign.shoppingapp.feature.promo.ui.PromoCodeCouponPlaceholder
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode

@Composable
internal fun AvailablePromoCodesHint(
    state: PromoCodeUiState.AvailableCodes,
    enabled: Boolean,
    onCodeClick: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionCard(
        icon = AppIcons.ConfirmationNumber,
        title = stringResource(R.string.promo_available_title),
        subtitle = stringResource(R.string.promo_available_hint),
        modifier = modifier,
    ) {
        // Every state takes the error notice's height, the tallest one, so the card doesn't jump
        // when codes load, fail or turn out empty.
        Box(
            modifier = Modifier.heightIn(min = CONTENT_MIN_HEIGHT),
            contentAlignment = Alignment.CenterStart,
        ) {
            when (state) {
                PromoCodeUiState.AvailableCodes.Loading -> PromoCodeCouponsPlaceholder()
                is PromoCodeUiState.AvailableCodes.Loaded -> if (state.promoCodes.isEmpty()) {
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
                PromoCodeUiState.AvailableCodes.Error -> NoticeWithAction(
                    icon = AppIcons.Error,
                    title = stringResource(R.string.promo_available_error),
                    style = NoticeStyle.Error,
                    actionText = stringResource(R.string.promo_retry),
                    onAction = onRetry,
                )
            }
        }
    }
}

// Two placeholder coupons where the real ones will be and of their size, so nothing shifts when codes load.
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

// A notice with a button: the button's 48 dp plus the notice's 4 dp padding above and below.
private val CONTENT_MIN_HEIGHT = 56.dp

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AvailablePromoCodesHintPreview() {
    val states = listOf(
        PromoCodeUiState.AvailableCodes.Loading,
        PromoCodeUiState.AvailableCodes.Loaded(listOf(PromoCode("SALE10", 10), PromoCode("SALE25", 25))),
        PromoCodeUiState.AvailableCodes.Loaded(emptyList()),
        PromoCodeUiState.AvailableCodes.Error,
    )
    ShoppingAppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(Spacing.ScreenPadding),
                verticalArrangement = Arrangement.spacedBy(Spacing.SectionSpacing),
            ) {
                states.forEach { AvailablePromoCodesHint(state = it, enabled = true, onCodeClick = {}, onRetry = {}) }
            }
        }
    }
}
