package krio.systemdesign.shoppingapp.feature.settings.domain.usecase

import krio.systemdesign.shoppingapp.domain.model.Product
import krio.systemdesign.shoppingapp.domain.usecase.AddToCartUseCase
import javax.inject.Inject

// Кладёт в корзину закончившийся товар, чтобы проверить, как корзина с ним справляется.
// Из каталога такой товар не добавить: вместо кнопки там «Закончилось».
class AddUnavailableProductToCartUseCase @Inject constructor(
    private val addToCart: AddToCartUseCase,
) {
    suspend operator fun invoke(): Result<Unit> = addToCart(UNAVAILABLE_PRODUCT)

    private companion object {
        // Копия товара "16" из server/data/products.json. Недоступным его делает сервер при проверке корзины:
        // он смотрит только на id и отвечает Unavailable, пока у товара на сервере остаток 0.
        val UNAVAILABLE_PRODUCT = Product(
            id = "16",
            name = "Hoodie",
            price = 4499,
            imageUrl = "http://2.56.204.151:8080/images/16.png",
            description = "A heather-gray hoodie in thick fleece, with a hood and a kangaroo pocket. " +
                "It keeps its shape after washing.",
            availableQuantity = 0,
        )
    }
}
