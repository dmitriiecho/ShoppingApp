package krio.systemdesign.shoppingapp.feature.settings.impl.domain.usecase

import javax.inject.Inject
import krio.systemdesign.shoppingapp.core.config.ServerConfig
import krio.systemdesign.shoppingapp.shared.domain.model.Product
import krio.systemdesign.shoppingapp.shared.domain.usecase.AddToCartUseCase

// Adds more of an item than is in stock; in the catalog "+" stops at the stock.
class AddNotEnoughStockProductToCartUseCase @Inject constructor(private val addToCart: AddToCartUseCase) {
    suspend operator fun invoke(): Result<Unit> = addToCart(PRODUCT, QUANTITY)

    private companion object {
        // Above the server stock of product "48" (5).
        const val QUANTITY = 10

        // Copy of product "48" from server/data/products.json. The price must match the server's,
        // or the cart gets PriceChanged on top of NotEnoughStock.
        val PRODUCT = Product(
            id = "48",
            name = "Laundry Basket",
            price = 4699,
            imageUrl = "${ServerConfig.BASE_URL}images/48.png",
            description = "A round natural-rattan laundry basket with two side handles.",
            // As if added while that many were in stock.
            availableQuantity = QUANTITY,
        )
    }
}
