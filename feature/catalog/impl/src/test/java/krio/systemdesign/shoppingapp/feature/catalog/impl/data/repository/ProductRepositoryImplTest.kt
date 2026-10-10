package krio.systemdesign.shoppingapp.feature.catalog.impl.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import kotlin.test.Test
import krio.systemdesign.shoppingapp.core.network.apiRequest
import krio.systemdesign.shoppingapp.core.network.apiSample
import krio.systemdesign.shoppingapp.core.network.networkTest
import krio.systemdesign.shoppingapp.feature.catalog.impl.data.api.ProductApi
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.model.ProductLoadResult
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.model.ProductsPage
import krio.systemdesign.shoppingapp.shared.domain.model.Product

class ProductRepositoryImplTest {

    // A link can point to a product the server doesn't have: that is not a failure, retrying won't help.
    @Test
    fun `product the server doesn't have is not found`() = networkTest(::ProductApi) { server, api ->
        server.enqueue(code = 404)

        val result = ProductRepositoryImpl(api).getProduct("42")

        assertThat(result).isEqualTo(ProductLoadResult.NotFound)
    }

    @Test
    fun `server error loading a product is an error`() = networkTest(::ProductApi) { server, api ->
        server.enqueue(code = 500)

        val result = ProductRepositoryImpl(api).getProduct("1")

        assertThat(result).isInstanceOf<ProductLoadResult.Error>()
    }

    @Test
    fun `product is requested as in the API sample`() = networkTest(::ProductApi) { server, api ->
        server.enqueue(body = apiSample("product.json").toString())

        ProductRepositoryImpl(api).getProduct("1")

        val request = server.takeRequest()
        assertThat("${request.method} ${request.target}").isEqualTo(apiRequest("product"))
    }

    @Test
    fun `product from the API sample is read`() = networkTest(::ProductApi) { server, api ->
        server.enqueue(body = apiSample("product.json").toString())

        val result = ProductRepositoryImpl(api).getProduct("1")

        assertThat(result).isEqualTo(ProductLoadResult.Success(redMug))
    }

    @Test
    fun `page is requested as in the API sample`() = networkTest(::ProductApi) { server, api ->
        server.enqueue(body = apiSample("products-page.json").toString())

        ProductRepositoryImpl(api).getProducts(query = "", page = 1, pageSize = 2)

        val request = server.takeRequest()
        assertThat("${request.method} ${request.target}").isEqualTo(apiRequest("products-page"))
    }

    @Test
    fun `page from the API sample is read`() = networkTest(::ProductApi) { server, api ->
        server.enqueue(body = apiSample("products-page.json").toString())

        val result = ProductRepositoryImpl(api).getProducts(query = "", page = 1, pageSize = 2)

        assertThat(result).isEqualTo(Result.success(ProductsPage(listOf(redMug, blueMug), endReached = false)))
    }

    // The products in the samples.
    private val redMug = Product(
        id = "1",
        name = "Red Mug",
        price = 1299,
        imageUrl = "http://localhost:8080/images/1.png",
        description = "A red ceramic mug.",
        availableQuantity = 12,
    )
    private val blueMug = Product(
        id = "2",
        name = "Blue Mug",
        price = 1000,
        imageUrl = "http://localhost:8080/images/2.png",
        description = "A blue ceramic mug.",
        availableQuantity = 2,
    )
}
