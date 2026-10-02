package krio.systemdesign.shoppingapp.feature.settings.domain.usecase

import krio.systemdesign.shoppingapp.domain.model.Product
import krio.systemdesign.shoppingapp.domain.usecase.AddToCartUseCase
import javax.inject.Inject

// Кладёт в корзину товар по цене, которой уже нет на сервере, чтобы проверить, как корзина покажет изменение цены.
// Из каталога так не добавить: там цена всегда свежая, с сервера.
class AddPriceChangedProductToCartUseCase @Inject constructor(
    private val addToCart: AddToCartUseCase,
) {
    suspend operator fun invoke(): Result<Unit> = addToCart(PRICE_CHANGED_PRODUCT)

    private companion object {
        // Копия товара "40" из server/data/products.json, но со старой ценой: на сервере он стоит 5400 ($54.00).
        // Сервер при проверке корзины отвечает PriceChanged, пока цены расходятся. Одна штука не больше остатка,
        // поэтому NotEnoughStock не придёт.
        val PRICE_CHANGED_PRODUCT = Product(
            id = "40",
            name = "Cutting Board",
            price = 4900,
            imageUrl = "http://2.56.204.151:8080/images/40.png",
            description = "A rectangular walnut cutting board with rounded corners and a juice groove.",
            availableQuantity = 13,
        )
    }
}
