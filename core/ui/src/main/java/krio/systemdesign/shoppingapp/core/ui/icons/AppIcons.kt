package krio.systemdesign.shoppingapp.core.ui.icons

import androidx.compose.ui.graphics.vector.ImageVector
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.BrightnessAuto
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.CheckCircle
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.ConfirmationNumber
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.DarkMode
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Error
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Info
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Inventory2
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.LightMode
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.MaterialSymbols
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.ProductionQuantityLimits
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.SearchOff
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Sell
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.ShoppingCart
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Storefront

// Icons shared by several screens.
object AppIcons {
    val OutOfStock: ImageVector = MaterialSymbols.Inventory2
    val PriceChanged: ImageVector = MaterialSymbols.Sell
    val NotEnoughStock: ImageVector = MaterialSymbols.ProductionQuantityLimits
    val Error: ImageVector = MaterialSymbols.Error
    val Info: ImageVector = MaterialSymbols.Info
    val PromoCode: ImageVector = MaterialSymbols.ConfirmationNumber
    val PromoCodeApplied: ImageVector = MaterialSymbols.CheckCircle
    val EmptyCart: ImageVector = MaterialSymbols.ShoppingCart
    val EmptyCatalog: ImageVector = MaterialSymbols.Storefront
    val NothingFound: ImageVector = MaterialSymbols.SearchOff
    val SystemTheme: ImageVector = MaterialSymbols.BrightnessAuto
    val LightTheme: ImageVector = MaterialSymbols.LightMode
    val DarkTheme: ImageVector = MaterialSymbols.DarkMode
}
