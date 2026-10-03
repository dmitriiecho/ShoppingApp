package krio.systemdesign.shoppingapp.core.ui.components.notices

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.R
import krio.systemdesign.shoppingapp.core.ui.icons.AppIcons
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.CheckCircle
import krio.systemdesign.shoppingapp.core.ui.theme.ShoppingAppTheme

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
            icon = AppIcons.CheckCircle,
            title = title,
            style = NoticeStyle.Success,
            actionText = stringResource(R.string.core_ui_remove_promo_code),
            onAction = onRemove,
            modifier = modifier,
        )
    } else {
        Notice(
            icon = AppIcons.CheckCircle,
            title = title,
            style = NoticeStyle.Success,
            modifier = modifier,
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AppliedPromoCodeNoticePreview() {
    ShoppingAppTheme {
        Surface(color = ShoppingAppTheme.colors.cardContainer) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AppliedPromoCodeNotice(
                    code = "SALE10",
                    discountPercent = 10,
                    onRemove = {},
                )
                AppliedPromoCodeNotice(
                    code = "SALE25",
                    discountPercent = 25,
                )
            }
        }
    }
}
