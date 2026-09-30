package krio.systemdesign.shoppingapp.feature.cart.presentation.navigation

import android.os.Parcel
import android.os.Parcelable
import krio.systemdesign.shoppingapp.domain.model.PromoCode
import kotlinx.serialization.Serializable

data class CartPromoResult(
    val promoCode: String,
    val discountPercent: Int,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        promoCode = parcel.readString().orEmpty(),
        discountPercent = parcel.readInt(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(promoCode)
        parcel.writeInt(discountPercent)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<CartPromoResult> {
        override fun createFromParcel(parcel: Parcel): CartPromoResult = CartPromoResult(parcel)
        override fun newArray(size: Int): Array<CartPromoResult?> = arrayOfNulls(size)
    }
}

fun PromoCode.toCartPromoResult(): CartPromoResult = CartPromoResult(
    promoCode = code,
    discountPercent = discountPercent,
)

internal fun CartPromoResult.toPromoCode(): PromoCode = PromoCode(
    code = promoCode,
    discountPercent = discountPercent,
)

object CartRoutes {
    @Serializable
    data object Graph

    @Serializable
    internal data object Cart
}

internal object CartResults {
    const val PROMO_RESULT_KEY = "cart_promo_result"
}
