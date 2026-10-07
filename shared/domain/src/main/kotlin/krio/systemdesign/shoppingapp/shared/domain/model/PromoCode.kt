package krio.systemdesign.shoppingapp.shared.domain.model

import kotlinx.serialization.Serializable

// kotlinx.serialization, for passing an applied code between screens as a string
// (SavedStateHandle can't hold this pure-Kotlin class).
@Serializable
data class PromoCode(
    val code: String,
    val discountPercent: Int,
) {
    init {
        require(discountPercent in 1..100) { "discountPercent must be in 1..100" }
    }

    fun discountFor(subtotal: Long): Long = subtotal * discountPercent / 100
}
