package krio.systemdesign.shoppingapp.core.network

import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
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
