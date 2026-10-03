package krio.systemdesign.shoppingapp.data.database

import android.util.Log
import kotlin.coroutines.cancellation.CancellationException

// Runs a database operation and wraps the outcome in Result; rethrows CancellationException
// so coroutine cancellation still works. The database counterpart of networkCall.
internal inline fun <T> databaseCall(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Log.w("DatabaseCall", "Database operation failed", e)
        Result.failure(e)
    }
