package krio.systemdesign.shoppingapp.core.ui.icons

import androidx.compose.ui.graphics.vector.ImageVector
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.BrightnessAuto
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.CheckCircle
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.ConfirmationNumber
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.DarkMode
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Inventory2
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.LightMode
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.ProductionQuantityLimits
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.SearchOff
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Sell
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.ShoppingCart
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Storefront

// All icons of the app. The icons themselves (AppIcons.Home, AppIcons.Add…) are Material Symbols in icons/symbols,
// one per file: to add one, convert its SVG from fonts.google.com/icons with the Valkyrie plugin.
// Here are names by meaning, so that one thing looks the same on every screen.
object AppIcons {
    val OutOfStock: ImageVector get() = Inventory2
    val PriceChanged: ImageVector get() = Sell
    val NotEnoughStock: ImageVector get() = ProductionQuantityLimits
    val PromoCode: ImageVector get() = ConfirmationNumber
    val PromoCodeApplied: ImageVector get() = CheckCircle
    val EmptyCart: ImageVector get() = ShoppingCart
    val EmptyCatalog: ImageVector get() = Storefront
    val NothingFound: ImageVector get() = SearchOff
    val SystemTheme: ImageVector get() = BrightnessAuto
    val LightTheme: ImageVector get() = LightMode
    val DarkTheme: ImageVector get() = DarkMode
}
