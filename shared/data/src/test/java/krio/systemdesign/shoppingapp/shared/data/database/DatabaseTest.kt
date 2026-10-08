package krio.systemdesign.shoppingapp.shared.data.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest

// Runs a test on an empty database in memory, made from the app's ShoppingDatabase. Room needs an Android
// Context, so the test class runs on Robolectric (@RunWith(RobolectricTestRunner::class)).
internal fun databaseTest(block: suspend TestScope.(database: ShoppingDatabase) -> Unit): TestResult = runTest {
    val database = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        ShoppingDatabase::class.java,
    ).build()
    try {
        block(database)
    } finally {
        database.close()
    }
}
