package krio.systemdesign.shoppingapp.server.routes

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlinx.serialization.json.decodeFromJsonElement
import krio.systemdesign.shoppingapp.server.apiSample
import krio.systemdesign.shoppingapp.server.dto.ProductDTO
import krio.systemdesign.shoppingapp.server.dto.ProductsPageDTO
import krio.systemdesign.shoppingapp.server.sendApiRequest
import krio.systemdesign.shoppingapp.server.serverJson
import krio.systemdesign.shoppingapp.server.serverTest
import krio.systemdesign.shoppingapp.server.testProduct
import krio.systemdesign.shoppingapp.server.testShopData

class ProductRoutesTest {

    private val threeProducts = testShopData(products = listOf(testProduct("1"), testProduct("2"), testProduct("3")))

    @Test
    fun `page holds the requested number of products in file order`() = serverTest(threeProducts) { client ->
        val page = client.get("/products?query=&page=1&pageSize=2").body<ProductsPageDTO>()

        assertThat(page).isEqualTo(ProductsPageDTO(listOf(testProduct("1"), testProduct("2")), endReached = false))
    }

    @Test
    fun `last page is marked as the end`() = serverTest(threeProducts) { client ->
        val page = client.get("/products?query=&page=2&pageSize=2").body<ProductsPageDTO>()

        assertThat(page).isEqualTo(ProductsPageDTO(listOf(testProduct("3")), endReached = true))
    }

    @Test
    fun `page past the end is empty and marked as the end`() = serverTest(threeProducts) { client ->
        val page = client.get("/products?query=&page=3&pageSize=2").body<ProductsPageDTO>()

        assertThat(page).isEqualTo(ProductsPageDTO(emptyList(), endReached = true))
    }

    @Test
    fun `search finds names containing the query in any case`() = serverTest(
        testShopData(products = listOf(testProduct("1", name = "Red Mug"), testProduct("2", name = "Lamp"))),
    ) { client ->
        val page = client.get("/products?query=MUG&page=1&pageSize=10").body<ProductsPageDTO>()

        assertThat(page.products.map { it.id }).containsExactly("1")
    }

    @Test
    fun `search ignores spaces around the query`() = serverTest(
        testShopData(products = listOf(testProduct("1", name = "Red Mug"), testProduct("2", name = "Lamp"))),
    ) { client ->
        val page = client.get("/products?query=%20mug%20&page=1&pageSize=10").body<ProductsPageDTO>()

        assertThat(page.products.map { it.id }).containsExactly("1")
    }

    // Every case is asked and the wrong answers are listed together, so one run shows all of them.
    @Test
    fun `invalid page parameters are rejected with 400`() = serverTest(threeProducts) { client ->
        val parameters = listOf("page=0&pageSize=2", "page=1&pageSize=0", "page=1&pageSize=101", "page=1", "pageSize=2")

        val statuses = parameters.associateWith { client.get("/products?$it").status }

        assertThat(statuses.filterValues { it != HttpStatusCode.BadRequest }).isEmpty()
    }

    @Test
    fun `product is found by id`() = serverTest(threeProducts) { client ->
        val product = client.get("/products/2").body<ProductDTO>()

        assertThat(product).isEqualTo(testProduct("2"))
    }

    @Test
    fun `unknown product id is rejected with 404`() = serverTest(threeProducts) { client ->
        val response = client.get("/products/42")

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
    }

    // The app reads products as in the samples; the server must write them so.
    @Test
    fun `product is written as in the API sample`() = serverTest(samplePageData()) { client ->
        val response = client.sendApiRequest("product")

        assertThat(serverJson.parseToJsonElement(response.bodyAsText())).isEqualTo(apiSample("product.json"))
    }

    @Test
    fun `page is written as in the API sample`() = serverTest(samplePageData()) { client ->
        val response = client.sendApiRequest("products-page")

        assertThat(serverJson.parseToJsonElement(response.bodyAsText())).isEqualTo(apiSample("products-page.json"))
    }

    // The sample page's products and one more, so that the page isn't the last.
    private fun samplePageData() = testShopData(
        products = serverJson.decodeFromJsonElement<ProductsPageDTO>(apiSample("products-page.json")).products +
            testProduct("3"),
    )
}
