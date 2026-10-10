package krio.systemdesign.shoppingapp.core.network

import co.touchlab.kermit.Logger
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.HttpRequest
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException

sealed interface NetworkResult<out T> {
    data class Success<T>(val body: T) : NetworkResult<T>

    // The server responded with an error code: 404, 500…
    data class HttpError(
        val code: Int,
        val error: ResponseException,
    ) : NetworkResult<Nothing>

    // No response or it could not be parsed: no network, timeout, invalid JSON.
    data class Failure(val error: Throwable) : NetworkResult<Nothing>
}

// Performs the request and converts any outcome to NetworkResult.
// Coroutine cancellation is rethrown, not turned into Failure, so that cancellation still works.
// Runs on IO: Ktor reads and parses the response in the caller's coroutine, which may be the main thread.
suspend fun <T : Any> networkCall(request: suspend () -> T): NetworkResult<T> = withContext(Dispatchers.IO) {
    try {
        NetworkResult.Success(request())
    } catch (e: ResponseException) {
        val code = e.response.status.value
        val request: HttpRequest = e.response.call.request
        // 4xx is a warning: it can be a normal answer, e.g. 404 for an unknown promo code.
        if (code >= 500) {
            Logger.e { "Server returned an error: $code ${request.method.value} ${request.url}" }
        } else {
            Logger.w { "Server rejected the request: $code ${request.method.value} ${request.url}" }
        }
        NetworkResult.HttpError(code, e)
    } catch (e: CancellationException) {
        throw e
    } catch (e: IOException) {
        // Warning, not error: a lost connection is usual on a phone.
        Logger.w(e) { "Request to the server failed" }
        NetworkResult.Failure(e)
    } catch (e: Exception) {
        // Invalid JSON or an empty body: the server broke the contract.
        Logger.e(e) { "Server response could not be read" }
        NetworkResult.Failure(e)
    }
}
