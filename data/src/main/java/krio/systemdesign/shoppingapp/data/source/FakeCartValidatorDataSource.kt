package krio.systemdesign.shoppingapp.data.source

import krio.systemdesign.shoppingapp.domain.model.Cart
import krio.systemdesign.shoppingapp.domain.model.CartValidationResult
import javax.inject.Inject

// Сейчас не используется: корзину проверяет наш сервер (server/), см. NetworkCartValidatorDataSource.
internal class FakeCartValidatorDataSource @Inject constructor() : CartValidatorDataSource {
    override suspend fun validate(cart: Cart): CartValidationResult = CartValidationResult.Success
}
