package krio.systemdesign.shoppingapp.data.source

import android.util.Log
import kotlin.coroutines.cancellation.CancellationException

// Как runCatching, но отмену корутины пропускает дальше: иначе она превратится в обычную ошибку.
internal inline fun <T> suspendRunCatching(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Log.w("SuspendRunCatching", "Операция с базой не удалась", e)
        Result.failure(e)
    }
