package krio.systemdesign.shoppingapp.shared.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import krio.systemdesign.shoppingapp.shared.data.database.entity.AppliedPromoCodeEntity

@Dao
internal interface AppliedPromoCodeDao {
    @Query("SELECT * FROM applied_promo_code LIMIT 1")
    fun observe(): Flow<AppliedPromoCodeEntity?>

    @Upsert
    suspend fun upsert(entity: AppliedPromoCodeEntity)

    @Query("DELETE FROM applied_promo_code")
    suspend fun delete()
}
