package krio.systemdesign.shoppingapp.feature.settings.domain.usecase

import krio.systemdesign.shoppingapp.domain.model.Product
import krio.systemdesign.shoppingapp.domain.usecase.AddToCartUseCase
import javax.inject.Inject

// Кладёт в корзину товар в большем количестве, чем есть на сервере, чтобы проверить, как корзина с этим справляется.
// Из каталога столько не добавить: «+» там останавливается на остатке.
class AddNotEnoughStockProductToCartUseCase @Inject constructor(
    private val addToCart: AddToCartUseCase,
) {
    suspend operator fun invoke(): Result<Unit> = addToCart(NOT_ENOUGH_STOCK_PRODUCT, QUANTITY)

    private companion object {
        // Больше, чем остаток товара "48" на сервере (5).
        const val QUANTITY = 10

        // Копия товара "48" из server/data/products.json. Сервер при проверке корзины отвечает NotEnoughStock,
        // пока в корзине больше его остатка. Цена должна совпадать с серверной, иначе придёт ещё и PriceChanged.
        // Остаток здесь — как будто товар добавили, когда его было столько же, сколько положили в корзину.
        val NOT_ENOUGH_STOCK_PRODUCT = Product(
            id = "48",
            name = "Laundry Basket",
            price = 4699,
            imageUrl = "http://2.56.204.151:8080/images/48.png",
            description = "A round natural-rattan laundry basket with two side handles.",
            availableQuantity = QUANTITY,
        )
    }
}
