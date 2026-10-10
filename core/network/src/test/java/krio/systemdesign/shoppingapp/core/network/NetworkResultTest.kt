package krio.systemdesign.shoppingapp.core.network

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class NetworkResultTest {

    @Test
    fun `successful response returns its body`() = networkTest(::TestApi) { server, api ->
        server.enqueue(body = """{"name":"Mug"}""")

        val result = networkCall { api.item() }

        assertThat(result).isEqualTo(NetworkResult.Success(JsonObject(mapOf("name" to JsonPrimitive("Mug")))))
    }

    @Test
    fun `client error returns HttpError with its code`() = networkTest(::TestApi) { server, api ->
        server.enqueue(code = 404)

        val result = networkCall { api.item() }

        assertThat(result).isInstanceOf<NetworkResult.HttpError>().prop(NetworkResult.HttpError::code).isEqualTo(404)
    }

    @Test
    fun `server error returns HttpError with its code`() = networkTest(::TestApi) { server, api ->
        server.enqueue(code = 500)

        val result = networkCall { api.item() }

        assertThat(result).isInstanceOf<NetworkResult.HttpError>().prop(NetworkResult.HttpError::code).isEqualTo(500)
    }

    @Test
    fun `malformed JSON returns Failure`() = networkTest(::TestApi) { server, api ->
        server.enqueue(body = """{"name":""")

        val result = networkCall { api.item() }

        assertThat(result).isInstanceOf<NetworkResult.Failure>()
    }

    @Test
    fun `success without a body returns Failure`() = networkTest(::TestApi) { server, api ->
        server.enqueue(code = 204)

        val result = networkCall { api.item() }

        assertThat(result).isInstanceOf<NetworkResult.Failure>()
    }

    @Test
    fun `unreachable server returns Failure`() = networkTest(::TestApi) { server, api ->
        server.shutDown()

        val result = networkCall { api.item() }

        assertThat(result).isInstanceOf<NetworkResult.Failure>()
    }

    // A cancelled request must stay cancelled: Failure would let the caller carry on as after a network error.
    @Test
    fun `cancellation is rethrown, not turned into Failure`() = runTest {
        assertFailure { networkCall<JsonObject> { throw CancellationException("Canceled") } }
            .isInstanceOf<CancellationException>()
    }

    private class TestApi(private val client: HttpClient) {
        suspend fun item(): JsonObject = client.get("item").body()
    }
}
