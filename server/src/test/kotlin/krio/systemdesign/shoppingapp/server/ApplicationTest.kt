package krio.systemdesign.shoppingapp.server

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import krio.systemdesign.shoppingapp.server.data.ShopData

class ApplicationTest {

    @Test
    fun `hosted images are in data and are served`() = testApplication {
        val dataDir = Path("data")
        val data = ShopData.load(dataDir)
        val hosted = data.products.filter { "/images/" in it.imageUrl }
        assertEquals(50, hosted.size)
        hosted.forEach { product ->
            val fileName = product.imageUrl.substringAfterLast('/').substringBefore('?')
            assertEquals("${product.id}.png", fileName)
            val file = dataDir.resolve("images/$fileName")
            // The hub has no file on purpose: the app requests it and shows a placeholder.
            if (product.id == "3") assertFalse(file.exists(), fileName) else assertTrue(file.exists(), fileName)
        }

        application { module(data, dataDir.resolve("images")) }
        val response = createClient { }.get("/images/1.png")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(ContentType.Image.PNG, response.contentType()?.withoutParameters())
        assertTrue(response.bodyAsBytes().isNotEmpty())
    }
}
