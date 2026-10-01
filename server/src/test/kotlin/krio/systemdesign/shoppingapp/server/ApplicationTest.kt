package krio.systemdesign.shoppingapp.server

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import krio.systemdesign.shoppingapp.server.data.ShopData
import krio.systemdesign.shoppingapp.server.dto.CartItemDTO
import krio.systemdesign.shoppingapp.server.dto.CartValidationRequestDTO
import krio.systemdesign.shoppingapp.server.dto.CartValidationResponseDTO
import krio.systemdesign.shoppingapp.server.dto.ItemIssueDTO
import krio.systemdesign.shoppingapp.server.dto.ProductDTO
import krio.systemdesign.shoppingapp.server.dto.ProductsPageDTO
import krio.systemdesign.shoppingapp.server.dto.PromoCodeDTO
import kotlin.io.path.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.exists
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ApplicationTest {

    @Test
    fun `каталог режется на страницы`() = serverTest { client ->
        val first = client.get("/products?query=&page=1&pageSize=2").body<ProductsPageDTO>()
        assertEquals(listOf("1", "2"), first.products.map { it.id })
        assertFalse(first.endReached)

        val last = client.get("/products?query=&page=2&pageSize=2").body<ProductsPageDTO>()
        assertEquals(listOf("3"), last.products.map { it.id })
        assertTrue(last.endReached)

        val beyond = client.get("/products?query=&page=3&pageSize=2").body<ProductsPageDTO>()
        assertEquals(emptyList(), beyond.products)
        assertTrue(beyond.endReached)
    }

    @Test
    fun `поиск по названию без учёта регистра`() = serverTest { client ->
        val page = client.get("/products?query=MUG&page=1&pageSize=10").body<ProductsPageDTO>()
        assertEquals(listOf("1", "2"), page.products.map { it.id })
        assertTrue(page.endReached)
    }

    @Test
    fun `неправильные параметры страницы - 400`() = serverTest { client ->
        assertEquals(HttpStatusCode.BadRequest, client.get("/products?page=0&pageSize=2").status)
        assertEquals(HttpStatusCode.BadRequest, client.get("/products?page=1&pageSize=0").status)
        assertEquals(HttpStatusCode.BadRequest, client.get("/products?page=1").status)
    }

    @Test
    fun `товар по id`() = serverTest { client ->
        assertEquals(TEST_DATA.products[2], client.get("/products/3").body<ProductDTO>())
        assertEquals(HttpStatusCode.NotFound, client.get("/products/42").status)
    }

    @Test
    fun `промокод без учёта регистра`() = serverTest { client ->
        assertEquals(PromoCodeDTO("SALE10", 10), client.get("/promo-codes/SALE10").body<PromoCodeDTO>())
        assertEquals(PromoCodeDTO("SALE10", 10), client.get("/promo-codes/sale10").body<PromoCodeDTO>())
        assertEquals(HttpStatusCode.NotFound, client.get("/promo-codes/SALE11").status)
    }

    @Test
    fun `товары в корзине пока не проверяются`() = serverTest { client ->
        val response = client.validateCart(promoCode = null, items = listOf(CartItemDTO("3", price = 1, quantity = 2)))
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(CartValidationResponseDTO(issues = emptyList(), promoCodeValid = true), response.body())
    }

    @Test
    fun `промокод в корзине проверяется без учёта регистра`() = serverTest { client ->
        suspend fun promoCodeValid(code: String?) =
            client.validateCart(promoCode = code).body<CartValidationResponseDTO>().promoCodeValid

        assertTrue(promoCodeValid("SALE10"))
        assertTrue(promoCodeValid("sale10"))
        assertFalse(promoCodeValid("SALE99"))
        // Нет промокода — нечего проверять.
        assertTrue(promoCodeValid(null))
    }

    @Test
    fun `корзина не того формата - 400`() = serverTest { client ->
        val response = client.post("/cart/validate") {
            contentType(ContentType.Application.Json)
            setBody("""{"items": "oops"}""")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `формат проблем в корзине`() {
        val response = CartValidationResponseDTO(
            issues = listOf(ItemIssueDTO.Unavailable("3"), ItemIssueDTO.PriceChanged("1", newPrice = 900)),
            promoCodeValid = false,
        )
        assertEquals(
            """{"issues":[{"type":"unavailable","productId":"3"},{"type":"priceChanged","productId":"1","newPrice":900}],"promoCodeValid":false}""",
            Json.encodeToString(response),
        )
    }

    @Test
    fun `файлы из папки data читаются`() {
        val data = ShopData.load(Path("data"))
        assertTrue(data.products.isNotEmpty())
        assertTrue(data.promoCodes.isNotEmpty())
    }

    @Test
    fun `свои картинки лежат в data и отдаются`() = testApplication {
        val dataDir = Path("data")
        val data = ShopData.load(dataDir)
        val hosted = data.products.filter { "/images/" in it.imageUrl }
        assertEquals(50, hosted.size)
        hosted.forEach { product ->
            val fileName = product.imageUrl.substringAfterLast('/').substringBefore('?')
            assertEquals("${product.id}.png", fileName)
            assertTrue(dataDir.resolve("images/$fileName").exists(), fileName)
        }

        application { module(data, dataDir.resolve("images")) }
        val response = createClient { }.get("/images/1.png")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(ContentType.Image.PNG, response.contentType()?.withoutParameters())
        assertTrue(response.bodyAsBytes().isNotEmpty())
    }

    @Test
    fun `данные с ошибками не принимаются`() {
        assertFailsWith<IllegalArgumentException> {
            ShopData(products = TEST_DATA.products + TEST_DATA.products[0], promoCodes = emptyList())
        }
        assertFailsWith<IllegalArgumentException> {
            ShopData(products = emptyList(), promoCodes = listOf(PromoCodeDTO("FREE", 0)))
        }
        assertFailsWith<IllegalArgumentException> {
            ShopData(products = emptyList(), promoCodes = listOf(PromoCodeDTO("SALE10", 10), PromoCodeDTO("sale10", 20)))
        }
    }

    private fun serverTest(block: suspend (HttpClient) -> Unit) = testApplication {
        application { module(TEST_DATA, createTempDirectory("product-images")) }
        block(createClient { install(ContentNegotiation) { json() } })
    }

    private suspend fun HttpClient.validateCart(
        promoCode: String?,
        items: List<CartItemDTO> = listOf(CartItemDTO("1", price = 1000, quantity = 1)),
    ): HttpResponse = post("/cart/validate") {
        contentType(ContentType.Application.Json)
        setBody(CartValidationRequestDTO(items = items, promoCode = promoCode))
    }

    private companion object {
        val TEST_DATA = ShopData(
            products = listOf(
                product(id = "1", name = "Red Mug"),
                product(id = "2", name = "Blue Mug"),
                product(id = "3", name = "Lamp", available = false),
            ),
            promoCodes = listOf(PromoCodeDTO("SALE10", 10)),
        )

        fun product(id: String, name: String, available: Boolean = true) = ProductDTO(
            id = id,
            name = name,
            price = 1000,
            imageUrl = "https://picsum.photos/seed/$id/400/400",
            description = "",
            available = available,
        )
    }
}
