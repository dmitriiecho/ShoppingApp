package krio.systemdesign.shoppingapp.core.network

import io.ktor.client.HttpClient
import java.io.File
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

// Runs a test against a TestServer, with an API built on a client that has the app's settings (serverApi).
fun <T> networkTest(
    createApi: (HttpClient) -> T,
    block: suspend TestScope.(server: TestServer, api: T) -> Unit,
): TestResult = runTest {
    val server = TestServer()
    HttpClient(server.engine) { serverApi(TestServer.BASE_URL) }.use { client ->
        block(server, createApi(client))
    }
}

// A sample from server/api-samples: the JSON the app and the server agree on. The server's tests read the same files.
fun apiSample(name: String): JsonElement =
    networkJson.parseToJsonElement(File(System.getProperty("apiSamplesDir"), name).readText())

// A request from server/api-samples/requests.json, as the method and the target: "GET /products/1".
// The server's tests send the same requests.
fun apiRequest(name: String): String = apiSample("requests.json").jsonObject.getValue(name).jsonPrimitive.content
