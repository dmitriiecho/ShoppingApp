package krio.systemdesign.shoppingapp.feature.settings.impl.domain.usecase

import javax.inject.Inject
import krio.systemdesign.shoppingapp.core.config.ServerConfig
import krio.systemdesign.shoppingapp.shared.domain.model.Product
import krio.systemdesign.shoppingapp.shared.domain.usecase.AddToCartUseCase

// Adds an item with an old price and more of it than is in stock: two changes on one cart item.
internal class AddPriceChangedNotEnoughStockProductToCartUseCase @Inject constructor(
    private val addToCart: AddToCartUseCase,
) {
    suspend operator fun invoke(): Result<Unit> = addToCart(PRODUCT, QUANTITY)

    private companion object {
        // Above the server stock of product "32" (5).
        const val QUANTITY = 10

        // Copy of product "32" from server/data/products.json with an old price (the server has 1295).
        // The server checks price and stock separately, so the cart gets both PriceChanged and NotEnoughStock.
        val PRODUCT = Product(
            id = "32",
            name = "Notebook",
            price = 995,
            imageUrl = "${ServerConfig.BASE_URL}images/32.png",
            description = "A closed hardcover notebook with a navy linen cover and a ribbon bookmark.",
            // As if added while that many were in stock.
            availableQuantity = QUANTITY,
        )
    }
}
