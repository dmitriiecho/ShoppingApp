package krio.systemdesign.shoppingapp.domain.model

data class PromoCode(
    val code: String,
    val discountPercent: Int,
) {
    init {
        require(discountPercent in 1..100) { "discountPercent must be in 1..100" }
    }

    fun discountFor(subtotal: Long): Long = subtotal * discountPercent / 100
}
