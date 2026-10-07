package krio.systemdesign.shoppingapp.shared.data.database

import kotlin.coroutines.cancellation.CancellationException
import timber.log.Timber

// Runs a database operation and wraps the outcome in Result; rethrows CancellationException
// so coroutine cancellation still works. The database counterpart of networkCall.
internal inline fun <T> databaseCall(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Timber.e(e, "Database operation failed")
    Result.failure(e)
}
