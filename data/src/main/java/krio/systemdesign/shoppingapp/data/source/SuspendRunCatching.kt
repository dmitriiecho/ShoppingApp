package krio.systemdesign.shoppingapp.data.source

import android.util.Log
import kotlin.coroutines.cancellation.CancellationException

// Similar to runCatching, but rethrows CancellationException to ensure proper coroutine cancellation.
internal inline fun <T> suspendRunCatching(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Log.w("SuspendRunCatching", "Database operation failed", e)
        Result.failure(e)
    }
