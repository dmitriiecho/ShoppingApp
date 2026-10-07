package krio.systemdesign.shoppingapp.feature.settings.impl.domain.usecase

import javax.inject.Inject
import krio.systemdesign.shoppingapp.core.config.ServerConfig
import krio.systemdesign.shoppingapp.shared.domain.model.Product
import krio.systemdesign.shoppingapp.shared.domain.usecase.AddToCartUseCase

// Adds an item at a price the server no longer has; the catalog always shows the current price.
internal class AddPriceChangedProductToCartUseCase @Inject constructor(private val addToCart: AddToCartUseCase) {
    suspend operator fun invoke(): Result<Unit> = addToCart(PRODUCT)

    private companion object {
        // Copy of product "40" from server/data/products.json with an old price (the server has 5400),
        // so the cart gets PriceChanged. One item is within the stock, so no NotEnoughStock.
        val PRODUCT = Product(
            id = "40",
            name = "Cutting Board",
            price = 4900,
            imageUrl = "${ServerConfig.BASE_URL}images/40.png",
            description = "A rectangular walnut cutting board with rounded corners and a juice groove.",
            availableQuantity = 13,
        )
    }
}
