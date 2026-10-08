package krio.systemdesign.shoppingapp.shared.domain.model

fun testProduct(
    id: String = "1",
    price: Long = 1000,
    availableQuantity: Int = 10,
) = Product(
    id = id,
    name = "Product $id",
    price = price,
    imageUrl = "https://example.com/$id.png",
    description = "",
    availableQuantity = availableQuantity,
)

fun testCartItem(
    productId: String = "1",
    price: Long = 1000,
    quantity: Int = 1,
    availableQuantity: Int = 10,
) = CartItem(
    productId = productId,
    name = "Product $productId",
    imageUrl = "https://example.com/$productId.png",
    price = price,
    quantity = quantity,
    availableQuantity = availableQuantity,
)

fun testCart(
    vararg items: CartItem,
    promoCode: PromoCode? = null,
) = Cart(items = items.toList(), promoCode = promoCode)
