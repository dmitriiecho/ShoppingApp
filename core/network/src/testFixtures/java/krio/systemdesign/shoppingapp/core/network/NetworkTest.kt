package krio.systemdesign.shoppingapp.core.network

import java.io.File
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import mockwebserver3.MockWebServer
import retrofit2.Retrofit

// Runs a test against a local MockWebServer, with a client for the API interface T that reads JSON as the app does.
inline fun <reified T : Any> networkTest(
    crossinline block: suspend TestScope.(server: MockWebServer, api: T) -> Unit,
): TestResult = runTest {
    MockWebServer().use { server ->
        server.start()
        val api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(networkJsonConverterFactory)
            .build()
            .create(T::class.java)
        block(server, api)
    }
}

// A sample from server/api-samples: the JSON the app and the server agree on. The server's tests read the same files.
fun apiSample(name: String): JsonElement =
    networkJson.parseToJsonElement(File(System.getProperty("apiSamplesDir"), name).readText())

// A request from server/api-samples/requests.json, as the method and the target: "GET /products/1".
// The server's tests send the same requests.
fun apiRequest(name: String): String = apiSample("requests.json").jsonObject.getValue(name).jsonPrimitive.content
