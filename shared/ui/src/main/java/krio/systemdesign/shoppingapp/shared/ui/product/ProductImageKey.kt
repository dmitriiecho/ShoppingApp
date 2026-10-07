package krio.systemdesign.shoppingapp.shared.ui.product

// Key for ProductImage.sharedElementKey.
// Equal product ids on the list and the details screen make that picture fly between them.
data class ProductImageKey(val productId: String)
