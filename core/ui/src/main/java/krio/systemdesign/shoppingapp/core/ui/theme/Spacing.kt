package krio.systemdesign.shoppingapp.core.ui.theme

import androidx.compose.ui.unit.dp

// Отступы, которые повторяются на разных экранах.
object Spacing {
    // От края экрана до содержимого: списки, формы, нижняя панель.
    val ScreenPadding = 16.dp

    // Между карточками в списке, например товарами в каталоге и корзине.
    val CardSpacing = 12.dp

    // Между блоками экрана, например карточками оформления заказа.
    val SectionSpacing = 16.dp

    // Внутри карточки-блока: SectionCard, итоги корзины.
    val CardPadding = 16.dp
}
