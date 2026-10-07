package krio.systemdesign.shoppingapp.server.routes

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import krio.systemdesign.shoppingapp.server.TEST_DATA
import krio.systemdesign.shoppingapp.server.dto.ProductDTO
import krio.systemdesign.shoppingapp.server.dto.ProductsPageDTO
import krio.systemdesign.shoppingapp.server.serverTest

class ProductRoutesTest {

    @Test
    fun `catalog is split into pages`() = serverTest { client ->
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
    fun `search by name ignores case`() = serverTest { client ->
        val page = client.get("/products?query=MUG&page=1&pageSize=10").body<ProductsPageDTO>()
        assertEquals(listOf("1", "2"), page.products.map { it.id })
        assertTrue(page.endReached)
    }

    @Test
    fun `search ignores surrounding spaces`() = serverTest { client ->
        val page = client.get("/products?query=%20mug%20&page=1&pageSize=10").body<ProductsPageDTO>()
        assertEquals(listOf("1", "2"), page.products.map { it.id })
    }

    @Test
    fun `invalid page parameters - 400`() = serverTest { client ->
        assertEquals(HttpStatusCode.BadRequest, client.get("/products?page=0&pageSize=2").status)
        assertEquals(HttpStatusCode.BadRequest, client.get("/products?page=1&pageSize=0").status)
        assertEquals(HttpStatusCode.BadRequest, client.get("/products?page=1").status)
    }

    @Test
    fun `product by id`() = serverTest { client ->
        assertEquals(TEST_DATA.products[2], client.get("/products/3").body<ProductDTO>())
        assertEquals(HttpStatusCode.NotFound, client.get("/products/42").status)
    }
}
