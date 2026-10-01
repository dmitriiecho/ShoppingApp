package krio.systemdesign.shoppingapp.domain.model

data class CartItem(
    val productId: String,
    val name: String,
    val imageUrl: String,
    val price: Long,
    val quantity: Int,
    // Сколько штук можно было заказать, когда товар добавили из каталога. Может устареть:
    // настоящий остаток сообщает проверка корзины (ItemIssue.NotEnoughStock и Unavailable).
    val availableQuantity: Int,
)
