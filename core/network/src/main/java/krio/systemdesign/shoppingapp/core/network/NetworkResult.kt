package krio.systemdesign.shoppingapp.core.network

import android.util.Log
import retrofit2.HttpException
import retrofit2.Response
import kotlin.coroutines.cancellation.CancellationException

sealed interface NetworkResult<out T> {
    data class Success<T>(val body: T) : NetworkResult<T>

    // The server responded with an error code: 404, 500…
    data class HttpError(val code: Int, val error: HttpException) : NetworkResult<Nothing>

    // No response or it could not be parsed: no network, timeout, invalid JSON.
    data class Failure(val error: Throwable) : NetworkResult<Nothing>
}

// Performs the request and converts any outcome to NetworkResult.
// Coroutine cancellation is rethrown, not turned into Failure, so that cancellation still works.
suspend fun <T : Any> networkCall(request: suspend () -> Response<T>): NetworkResult<T> =
    try {
        val response = request()
        if (response.isSuccessful) {
            NetworkResult.Success(requireNotNull(response.body()))
        } else {
            NetworkResult.HttpError(response.code(), HttpException(response))
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Log.w(TAG, "Request to the server failed", e)
        NetworkResult.Failure(e)
    }

private const val TAG = "NetworkCall"
