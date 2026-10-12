package krio.systemdesign.shoppingapp.uikit.sections.feature

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Inventory2
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.ProductionQuantityLimits
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Sell
import krio.systemdesign.shoppingapp.feature.cart.ui.CartChangeNotice
import krio.systemdesign.shoppingapp.feature.cart.ui.ClearCartIconButton
import krio.systemdesign.shoppingapp.uikit.resources.Res
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_accept
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_item_out_of_stock
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_not_enough_stock_items
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_price_changes
import krio.systemdesign.shoppingapp.uikit.resources.uikit_sample_remove
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CartFeatureSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        sampleGroup("CartChangeNotice") {
            SampleVariant {
                CartChangeNotice(
                    icon = AppIcons.Sell,
                    title = stringResource(Res.string.uikit_sample_price_changes),
                    actionText = stringResource(Res.string.uikit_sample_accept),
                )
                CartChangeNotice(
                    icon = AppIcons.Inventory2,
                    title = stringResource(Res.string.uikit_sample_item_out_of_stock),
                    actionText = stringResource(Res.string.uikit_sample_remove),
                )
                CartChangeNotice(
                    icon = AppIcons.ProductionQuantityLimits,
                    title = stringResource(Res.string.uikit_sample_not_enough_stock_items),
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
