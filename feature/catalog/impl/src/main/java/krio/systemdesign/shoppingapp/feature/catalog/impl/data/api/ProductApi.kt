package krio.systemdesign.shoppingapp.feature.catalog.impl.data.api

import dev.zacsweers.metro.Inject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.appendPathSegments
import krio.systemdesign.shoppingapp.feature.catalog.impl.data.dto.ProductDTO
import krio.systemdesign.shoppingapp.feature.catalog.impl.data.dto.ProductsPageDTO

@Inject
internal class ProductApi(private val client: HttpClient) {
    // Paging by page number is safe: the server never removes products (out-of-stock ones stay with zero
    // stock) and appends new ones to the end. A renamed product can still move within search results;
    // ProductPagingSource drops the duplicates this causes.
    suspend fun getProducts(
        query: String,
        page: Int,
        pageSize: Int,
    ): ProductsPageDTO = client.get("products") {
        parameter("query", query)
        parameter("page", page)
        parameter("pageSize", pageSize)
    }.body()

    suspend fun getProduct(id: String): ProductDTO = client.get { url { appendPathSegments("products", id) } }.body()
}
