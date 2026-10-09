package krio.systemdesign.shoppingapp.core.network

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import mockwebserver3.MockResponse
import retrofit2.Response
import retrofit2.http.GET

class NetworkCallTest {

    @Test
    fun `successful response returns its body`() = networkTest<TestApi> { server, api ->
        server.enqueue(MockResponse.Builder().body("""{"name":"Mug"}""").build())

        val result = networkCall { api.item() }

        assertThat(result).isEqualTo(NetworkResult.Success(JsonObject(mapOf("name" to JsonPrimitive("Mug")))))
    }

    @Test
    fun `client error returns HttpError with its code`() = networkTest<TestApi> { server, api ->
        server.enqueue(MockResponse.Builder().code(404).build())

        val result = networkCall { api.item() }

        assertThat(result).isInstanceOf<NetworkResult.HttpError>().prop(NetworkResult.HttpError::code).isEqualTo(404)
    }

    @Test
    fun `server error returns HttpError with its code`() = networkTest<TestApi> { server, api ->
        server.enqueue(MockResponse.Builder().code(500).build())

        val result = networkCall { api.item() }

        assertThat(result).isInstanceOf<NetworkResult.HttpError>().prop(NetworkResult.HttpError::code).isEqualTo(500)
    }

    @Test
    fun `malformed JSON returns Failure`() = networkTest<TestApi> { server, api ->
        server.enqueue(MockResponse.Builder().body("""{"name":""").build())

        val result = networkCall { api.item() }

        assertThat(result).isInstanceOf<NetworkResult.Failure>()
    }

    @Test
    fun `success without a body returns Failure`() = networkTest<TestApi> { server, api ->
        server.enqueue(MockResponse.Builder().code(204).build())

        val result = networkCall { api.item() }

        assertThat(result).isInstanceOf<NetworkResult.Failure>()
    }

    @Test
    fun `unreachable server returns Failure`() = networkTest<TestApi> { server, api ->
        server.close()

        val result = networkCall { api.item() }

        assertThat(result).isInstanceOf<NetworkResult.Failure>()
    }

    // A cancelled request must stay cancelled: Failure would let the caller carry on as after a network error.
    @Test
    fun `cancellation is rethrown, not turned into Failure`() = runTest {
        assertFailure { networkCall<JsonObject> { throw CancellationException("Canceled") } }
            .isInstanceOf<CancellationException>()
    }

    private interface TestApi {
        @GET("item")
        suspend fun item(): Response<JsonObject>
    }
}
