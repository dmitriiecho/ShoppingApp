package krio.systemdesign.shoppingapp.server

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeBytes
import kotlin.test.Test

class ApplicationTest {

    @Test
    fun `product image is served as PNG`() {
        val imagesDir = createTempDirectory("product-images")
        val image = byteArrayOf(1, 2, 3)
        imagesDir.resolve("1.png").writeBytes(image)

        serverTest(imagesDir = imagesDir) { client ->
            val response = client.get("/images/1.png")

            assertThat(response.contentType()?.withoutParameters()).isEqualTo(ContentType.Image.PNG)
            assertThat(response.bodyAsBytes().toList()).isEqualTo(image.toList())
        }
    }

    // The app shows a placeholder for a product whose image is missing.
    @Test
    fun `missing product image is rejected with 404`() = serverTest { client ->
        val response = client.get("/images/1.png")

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
    }
}
