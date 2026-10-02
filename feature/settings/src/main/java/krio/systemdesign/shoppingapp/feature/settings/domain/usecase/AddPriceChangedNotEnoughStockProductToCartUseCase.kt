package krio.systemdesign.shoppingapp.feature.settings.domain.usecase

import krio.systemdesign.shoppingapp.domain.model.Product
import krio.systemdesign.shoppingapp.domain.usecase.AddToCartUseCase
import javax.inject.Inject

// Кладёт в корзину товар сразу с двумя проблемами: по старой цене и в большем количестве, чем есть на сервере.
// Так проверяется, как корзина показывает несколько изменений у одного товара.
class AddPriceChangedNotEnoughStockProductToCartUseCase @Inject constructor(
    private val addToCart: AddToCartUseCase,
) {
    suspend operator fun invoke(): Result<Unit> = addToCart(PRODUCT, QUANTITY)

    private companion object {
        // Больше, чем остаток товара "32" на сервере (5).
        const val QUANTITY = 10

        // Копия товара "32" из server/data/products.json, но со старой ценой: на сервере он стоит 1295 ($12.95).
        // Сервер при проверке корзины сверяет цену и остаток по отдельности, поэтому отвечает и PriceChanged,
        // и NotEnoughStock. Остаток здесь — как будто товар добавили, когда его было столько, сколько положили.
        val PRODUCT = Product(
            id = "32",
            name = "Notebook",
            price = 995,
            imageUrl = "http://2.56.204.151:8080/images/32.png",
            description = "A closed hardcover notebook with a navy linen cover and a ribbon bookmark.",
            availableQuantity = QUANTITY,
        )
    }
}
