package krio.systemdesign.shoppingapp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "applied_promo_code")
data class AppliedPromoCodeEntity(
    @PrimaryKey val id: Int = SINGLE_ROW_ID,
    val code: String,
    val discountPercent: Int,
) {
    companion object {
        const val SINGLE_ROW_ID = 0
    }
}
