package krio.systemdesign.shoppingapp.core.ui.components

// Ключ общего элемента «картинка товара» (sharedElementKey у ProductImage): с ним картинка перелетает
// из списка каталога или корзины на карточку товара и обратно.
data class ProductImageKey(val productId: String)
