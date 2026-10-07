package krio.systemdesign.shoppingapp.shared.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import krio.systemdesign.shoppingapp.shared.data.database.dao.AppliedPromoCodeDao
import krio.systemdesign.shoppingapp.shared.data.database.dao.CartItemDao
import krio.systemdesign.shoppingapp.shared.data.database.entity.AppliedPromoCodeEntity
import krio.systemdesign.shoppingapp.shared.data.database.entity.CartItemEntity

@Database(
    entities = [CartItemEntity::class, AppliedPromoCodeEntity::class],
    version = 1,
    exportSchema = true,
)
internal abstract class ShoppingDatabase : RoomDatabase() {
    abstract fun cartItemDao(): CartItemDao

    abstract fun appliedPromoCodeDao(): AppliedPromoCodeDao
}
