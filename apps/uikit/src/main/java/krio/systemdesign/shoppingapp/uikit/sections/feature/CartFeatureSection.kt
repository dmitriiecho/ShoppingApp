package krio.systemdesign.shoppingapp.uikit.sections.feature

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Inventory2
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.ProductionQuantityLimits
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Sell
import krio.systemdesign.shoppingapp.feature.cart.ui.CartChangeNotice
import krio.systemdesign.shoppingapp.feature.cart.ui.ClearCartIconButton
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup

@Composable
fun CartFeatureSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        sampleGroup("CartChangeNotice") {
            SampleVariant {
                CartChangeNotice(
                    icon = AppIcons.Sell,
                    title = stringResource(R.string.uikit_sample_price_changes),
                    actionText = stringResource(R.string.uikit_sample_accept),
                )
                CartChangeNotice(
                    icon = AppIcons.Inventory2,
                    title = stringResource(R.string.uikit_sample_item_out_of_stock),
                    actionText = stringResource(R.string.uikit_sample_remove),
                )
                CartChangeNotice(
                    icon = AppIcons.ProductionQuantityLimits,
                    title = stringResource(R.string.uikit_sample_not_enough_stock_items),
                )
            }
        }
        sampleGroup("ClearCartIconButton") {
            SampleVariant {
                ClearCartIconButton(onClick = {})
            }
        }
    }
}
