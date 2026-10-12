package krio.systemdesign.shoppingapp.crossfeature.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import krio.systemdesign.shoppingapp.crossfeature.data.database.dao.AppliedPromoCodeDao
import krio.systemdesign.shoppingapp.crossfeature.data.database.dao.CartItemDao
import krio.systemdesign.shoppingapp.crossfeature.data.database.entity.AppliedPromoCodeEntity
import krio.systemdesign.shoppingapp.crossfeature.data.database.entity.CartItemEntity

@Database(
    entities = [CartItemEntity::class, AppliedPromoCodeEntity::class],
    version = 1,
    exportSchema = true,
)
@ConstructedBy(ShoppingDatabaseConstructor::class)
internal abstract class ShoppingDatabase : RoomDatabase() {
    abstract fun cartItemDao(): CartItemDao

    abstract fun appliedPromoCodeDao(): AppliedPromoCodeDao
}

// Creates the database without reflection, which Kotlin/Native lacks. Room generates the actual object for each
// platform.
@Suppress("KotlinNoActualForExpect")
internal expect object ShoppingDatabaseConstructor : RoomDatabaseConstructor<ShoppingDatabase> {
    override fun initialize(): ShoppingDatabase
}
