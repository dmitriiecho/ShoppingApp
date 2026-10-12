package krio.systemdesign.shoppingapp.crossfeature.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import krio.systemdesign.shoppingapp.crossfeature.domain.model.PromoCode

@Entity(tableName = "applied_promo_code")
internal data class AppliedPromoCodeEntity(
    @PrimaryKey val id: Int = SINGLE_ROW_ID,
    val code: String,
    val discountPercent: Int,
) {
    companion object {
        const val SINGLE_ROW_ID = 0
    }
}

internal fun AppliedPromoCodeEntity.toDomain(): PromoCode = PromoCode(
    code = code,
    discountPercent = discountPercent,
)

internal fun PromoCode.toEntity(): AppliedPromoCodeEntity = AppliedPromoCodeEntity(
    code = code,
    discountPercent = discountPercent,
)
