package krio.systemdesign.shoppingapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import krio.systemdesign.shoppingapp.data.database.dao.CartDao
import krio.systemdesign.shoppingapp.data.database.entity.CartItemEntity

@Database(
    entities = [CartItemEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class ShoppingDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}
