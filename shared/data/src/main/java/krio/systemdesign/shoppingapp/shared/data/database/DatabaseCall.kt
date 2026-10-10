package krio.systemdesign.shoppingapp.shared.data.database

import android.database.sqlite.SQLiteException
import co.touchlab.kermit.Logger

// Runs a database operation and wraps a database failure (a full disk, a corrupted file) in Result.
// Only SQLiteException: anything else, such as a failed require, is a bug and must not pass for a database error.
// The database counterpart of networkCall.
internal inline fun <T> databaseCall(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: SQLiteException) {
    Logger.e(e) { "Database operation failed" }
    Result.failure(e)
}
