package krio.systemdesign.shoppingapp.core.network

import android.util.Log
import retrofit2.HttpException
import retrofit2.Response
import kotlin.coroutines.cancellation.CancellationException

sealed interface NetworkResult<out T> {
    data class Success<T>(val body: T) : NetworkResult<T>

    // Сервер ответил, но кодом ошибки: 404, 500…
    data class HttpError(val code: Int, val error: HttpException) : NetworkResult<Nothing>

    // Ответа нет или его не удалось разобрать: нет сети, таймаут, кривой JSON.
    data class Failure(val error: Throwable) : NetworkResult<Nothing>
}

// Выполняет запрос и превращает любой исход в NetworkResult. Отмену корутины пропускаем дальше.
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
        Log.w(TAG, "Запрос к серверу не удался", e)
        NetworkResult.Failure(e)
    }

private const val TAG = "NetworkCall"
