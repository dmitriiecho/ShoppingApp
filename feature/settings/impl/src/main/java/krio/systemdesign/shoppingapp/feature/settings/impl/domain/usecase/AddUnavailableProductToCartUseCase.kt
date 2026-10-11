package krio.systemdesign.shoppingapp.feature.settings.impl.domain.usecase

import dev.zacsweers.metro.Inject
import krio.systemdesign.shoppingapp.core.config.ServerConfig
import krio.systemdesign.shoppingapp.shared.domain.model.Product
import krio.systemdesign.shoppingapp.shared.domain.usecase.AddToCartUseCase

// Adds an out-of-stock item; the catalog can't, it shows "Out of stock" instead of the button.
@Inject
internal class AddUnavailableProductToCartUseCase(private val addToCart: AddToCartUseCase) {
    suspend operator fun invoke(): Result<Unit> = addToCart(PRODUCT)

    private companion object {
        // Copy of product "16" from server/data/products.json. The server checks it by id
        // and answers Unavailable while its stock is 0.
        val PRODUCT = Product(
            id = "16",
            name = "Hoodie",
            price = 4499,
            imageUrl = "${ServerConfig.BASE_URL}images/16.png",
            description = "A heather-gray hoodie in thick fleece, with a hood and a kangaroo pocket. " +
                "It keeps its shape after washing.",
            availableQuantity = 0,
        )
    }
}
