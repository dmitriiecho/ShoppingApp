package krio.systemdesign.shoppingapp.uikit.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.icons.AppIcons
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.AccountBalanceWallet
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Add
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.ArrowBack
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.BrightnessAuto
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.ChatBubble
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.CheckCircle
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Close
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.ConfirmationNumber
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Contrast
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.CreditCard
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.DarkMode
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.DeleteFilled
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Downloading
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.EmojiSymbols
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Error
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Feedback
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.HeadphonesFilled
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.HomeFilled
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.HourglassEmpty
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Image
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Info
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Inventory2
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.LightMode
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Link
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.LocationOn
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.OpenInNew
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Palette
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Payments
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.ProductionQuantityLimits
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Receipt
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Remove
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.RemoveShoppingCart
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Search
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.SearchOff
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Sell
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.SettingsFilled
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.ShoppingBag
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.ShoppingCart
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.ShoppingCartFilled
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.SmartButton
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Smartphone
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Storefront
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.TextFields
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.ViewAgenda
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Warning
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.WebAsset
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup

@Composable
fun IconsSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        sampleGroup("AppIcons") {
            SampleVariant {
                appIcons.chunked(ICON_COLUMNS).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { (name, icon) -> IconCell(name, icon, Modifier.weight(1f)) }
                        // Keeps the last row's cells in their columns.
                        repeat(ICON_COLUMNS - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun IconCell(name: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(icon, contentDescription = null)
        Text(
            // A zero-width space between the words, so a long name wraps between them, not mid-word.
            text = name.replace(CAMEL_CASE_BOUNDARY, "\u200B"),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            minLines = 2,
        )
    }
}

// Every icon in icons/symbols. Extension properties can't be listed at runtime, so a new icon is added here by hand.
private val appIcons = listOf(
    "AccountBalanceWallet" to AppIcons.AccountBalanceWallet,
    "Add" to AppIcons.Add,
    "ArrowBack" to AppIcons.ArrowBack,
    "BrightnessAuto" to AppIcons.BrightnessAuto,
    "ChatBubble" to AppIcons.ChatBubble,
    "CheckCircle" to AppIcons.CheckCircle,
    "Close" to AppIcons.Close,
    "ConfirmationNumber" to AppIcons.ConfirmationNumber,
    "Contrast" to AppIcons.Contrast,
    "CreditCard" to AppIcons.CreditCard,
    "DarkMode" to AppIcons.DarkMode,
    "DeleteFilled" to AppIcons.DeleteFilled,
    "Downloading" to AppIcons.Downloading,
    "EmojiSymbols" to AppIcons.EmojiSymbols,
    "Error" to AppIcons.Error,
    "Feedback" to AppIcons.Feedback,
    "HeadphonesFilled" to AppIcons.HeadphonesFilled,
    "HomeFilled" to AppIcons.HomeFilled,
    "HourglassEmpty" to AppIcons.HourglassEmpty,
    "Image" to AppIcons.Image,
    "Info" to AppIcons.Info,
    "Inventory2" to AppIcons.Inventory2,
    "LightMode" to AppIcons.LightMode,
    "Link" to AppIcons.Link,
    "LocationOn" to AppIcons.LocationOn,
    "OpenInNew" to AppIcons.OpenInNew,
    "Palette" to AppIcons.Palette,
    "Payments" to AppIcons.Payments,
    "ProductionQuantityLimits" to AppIcons.ProductionQuantityLimits,
    "Receipt" to AppIcons.Receipt,
    "Remove" to AppIcons.Remove,
    "RemoveShoppingCart" to AppIcons.RemoveShoppingCart,
    "Search" to AppIcons.Search,
    "SearchOff" to AppIcons.SearchOff,
    "Sell" to AppIcons.Sell,
    "SettingsFilled" to AppIcons.SettingsFilled,
    "ShoppingBag" to AppIcons.ShoppingBag,
    "ShoppingCart" to AppIcons.ShoppingCart,
    "ShoppingCartFilled" to AppIcons.ShoppingCartFilled,
    "SmartButton" to AppIcons.SmartButton,
    "Smartphone" to AppIcons.Smartphone,
    "Storefront" to AppIcons.Storefront,
    "TextFields" to AppIcons.TextFields,
    "ViewAgenda" to AppIcons.ViewAgenda,
    "Warning" to AppIcons.Warning,
    "WebAsset" to AppIcons.WebAsset,
)

private const val ICON_COLUMNS = 3
private val CAMEL_CASE_BOUNDARY = Regex("(?<=[a-z0-9])(?=[A-Z])")
