package krio.systemdesign.shoppingapp.core.ui.components.notices

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import krio.systemdesign.shoppingapp.core.ui.R
import krio.systemdesign.shoppingapp.core.ui.icons.AppIcons

// Применённый промокод — зелёная плашка с галочкой. Одна и та же в корзине и при оформлении заказа.
// onRemove = null — без кнопки «Убрать»: например, при оформлении заказ уже не меняют.
@Composable
fun AppliedPromoCodeNotice(
    code: String,
    discountPercent: Int,
    modifier: Modifier = Modifier,
    onRemove: (() -> Unit)? = null,
) {
    val title = stringResource(R.string.core_ui_applied_promo_code, code, discountPercent)
    if (onRemove != null) {
        NoticeWithAction(
            icon = AppIcons.PromoCodeApplied,
            title = title,
            style = NoticeStyle.Success,
            actionText = stringResource(R.string.core_ui_remove_promo_code),
            onAction = onRemove,
            modifier = modifier,
        )
    } else {
        Notice(
            icon = AppIcons.PromoCodeApplied,
            title = title,
            style = NoticeStyle.Success,
            modifier = modifier,
        )
    }
}
