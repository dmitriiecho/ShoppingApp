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

// Значки, смысл которых встречается больше чем на одном экране. В приложении у значка один смысл, а у смысла —
// один значок: экраны берут значок отсюда по смыслу, поэтому одно и то же везде выглядит одинаково.
// Перед тем как взять для нового смысла значок из Icons, проверьте, что его нет здесь и он не занят на экранах.
object AppIcons {
    // Товар закончился: плашка в корзине и пункт настроек, который добавляет такой товар.
    val OutOfStock: ImageVector = Icons.Outlined.Inventory2

    // Цена товара изменилась.
    val PriceChanged: ImageVector = Icons.Outlined.Sell

    // Товара меньше, чем лежит в корзине.
    val NotEnoughStock: ImageVector = Icons.Outlined.ProductionQuantityLimits

    // Ошибка: не загрузилось, промокод не действует.
    val Error: ImageVector = Icons.Outlined.ErrorOutline

    // Пояснение, а не проблема.
    val Info: ImageVector = Icons.Outlined.Info

    // Промокод: купоны на экране промокода.
    val PromoCode: ImageVector = Icons.Outlined.ConfirmationNumber

    // Промокод применён и действует.
    val PromoCodeApplied: ImageVector = Icons.Outlined.CheckCircle

    // Корзина пуста: в корзине и при оформлении заказа.
    val EmptyCart: ImageVector = Icons.Outlined.ShoppingCart

    // В каталоге нет товаров.
    val EmptyCatalog: ImageVector = Icons.Outlined.Storefront

    // Поиск ничего не нашёл.
    val NothingFound: ImageVector = Icons.Outlined.SearchOff

    // Тема: как в системе, светлая, тёмная. В настройках приложения и в каталоге компонентов.
    val SystemTheme: ImageVector = Icons.Outlined.BrightnessAuto
    val LightTheme: ImageVector = Icons.Outlined.LightMode
    val DarkTheme: ImageVector = Icons.Outlined.DarkMode
}
