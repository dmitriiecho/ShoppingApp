package krio.systemdesign.shoppingapp.core.network

import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber

sealed interface NetworkResult<out T> {
    data class Success<T>(val body: T) : NetworkResult<T>

    // The server responded with an error code: 404, 500…
    data class HttpError(
        val code: Int,
        val error: HttpException,
    ) : NetworkResult<Nothing>

    // No response or it could not be parsed: no network, timeout, invalid JSON.
    data class Failure(val error: Throwable) : NetworkResult<Nothing>
}

// Performs the request and converts any outcome to NetworkResult.
// Coroutine cancellation is rethrown, not turned into Failure, so that cancellation still works.
suspend fun <T : Any> networkCall(request: suspend () -> Response<T>): NetworkResult<T> = try {
    val response = request()
    if (response.isSuccessful) {
        NetworkResult.Success(requireNotNull(response.body()))
    } else {
        val request = response.raw().request
        // 4xx is a warning: it can be a normal answer, e.g. 404 for an unknown promo code.
        if (response.code() >= 500) {
            Timber.e("Server returned an error: %d %s %s", response.code(), request.method, request.url)
        } else {
            Timber.w("Server rejected the request: %d %s %s", response.code(), request.method, request.url)
        }
        NetworkResult.HttpError(response.code(), HttpException(response))
    }
} catch (e: CancellationException) {
    throw e
} catch (e: IOException) {
    // Warning, not error: a lost connection is usual on a phone.
    Timber.w(e, "Request to the server failed")
    NetworkResult.Failure(e)
} catch (e: Exception) {
    // Invalid JSON or an empty body: the server broke the contract.
    Timber.e(e, "Server response could not be read")
    NetworkResult.Failure(e)
}
