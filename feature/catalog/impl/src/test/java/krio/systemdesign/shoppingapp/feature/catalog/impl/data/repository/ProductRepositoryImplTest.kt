package krio.systemdesign.shoppingapp.feature.catalog.impl.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import kotlin.test.Test
import krio.systemdesign.shoppingapp.core.network.networkTest
import krio.systemdesign.shoppingapp.feature.catalog.impl.data.api.ProductApi
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.model.ProductLoadResult
import mockwebserver3.MockResponse

class ProductRepositoryImplTest {

    // A link can point to a product the server doesn't have: that is not a failure, retrying won't help.
    @Test
    fun `product the server doesn't have is not found`() = networkTest<ProductApi> { server, api ->
        server.enqueue(MockResponse.Builder().code(404).build())

        val result = ProductRepositoryImpl(api).getProduct("42")

        assertThat(result).isEqualTo(ProductLoadResult.NotFound)
    }

    @Test
    fun `server error loading a product is an error`() = networkTest<ProductApi> { server, api ->
        server.enqueue(MockResponse.Builder().code(500).build())

        val result = ProductRepositoryImpl(api).getProduct("1")

        assertThat(result).isInstanceOf<ProductLoadResult.Error>()
    }
}
