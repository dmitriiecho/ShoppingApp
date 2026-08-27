package krio.systemdesign.shoppingapp.feature.cart.presentation.navigation

import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.Serializable

data class CartPromoResult(
    val promoCode: String,
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString().orEmpty())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(promoCode)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<CartPromoResult> {
        override fun createFromParcel(parcel: Parcel): CartPromoResult = CartPromoResult(parcel)
        override fun newArray(size: Int): Array<CartPromoResult?> = arrayOfNulls(size)
    }
}

object CartRoutes {
    @Serializable
    data object Graph

    @Serializable
    internal data object Cart
}

internal object CartResults {
    const val PROMO_RESULT_KEY = "cart_promo_result"
}
