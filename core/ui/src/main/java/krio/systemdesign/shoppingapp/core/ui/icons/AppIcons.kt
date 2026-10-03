package krio.systemdesign.shoppingapp.core.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrightnessAuto
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.ProductionQuantityLimits
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.ui.graphics.vector.ImageVector

// Icons shared by several screens.
object AppIcons {
    val OutOfStock: ImageVector = Icons.Outlined.Inventory2
    val PriceChanged: ImageVector = Icons.Outlined.Sell
    val NotEnoughStock: ImageVector = Icons.Outlined.ProductionQuantityLimits
    val Error: ImageVector = Icons.Outlined.ErrorOutline
    val Info: ImageVector = Icons.Outlined.Info
    val PromoCode: ImageVector = Icons.Outlined.ConfirmationNumber
    val PromoCodeApplied: ImageVector = Icons.Outlined.CheckCircle
    val EmptyCart: ImageVector = Icons.Outlined.ShoppingCart
    val EmptyCatalog: ImageVector = Icons.Outlined.Storefront
    val NothingFound: ImageVector = Icons.Outlined.SearchOff
    val SystemTheme: ImageVector = Icons.Outlined.BrightnessAuto
    val LightTheme: ImageVector = Icons.Outlined.LightMode
    val DarkTheme: ImageVector = Icons.Outlined.DarkMode
}
