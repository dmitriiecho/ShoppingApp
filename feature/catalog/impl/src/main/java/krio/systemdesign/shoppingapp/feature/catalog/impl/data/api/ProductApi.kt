package krio.systemdesign.shoppingapp.feature.catalog.impl.data.api

import krio.systemdesign.shoppingapp.feature.catalog.impl.data.dto.ProductDTO
import krio.systemdesign.shoppingapp.feature.catalog.impl.data.dto.ProductsPageDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface ProductApi {
    // Paging by page number is safe: the server never removes products (out-of-stock ones stay with zero
    // stock) and appends new ones to the end. A renamed product can still move within search results;
    // ProductPagingSource drops the duplicates this causes.
    @GET("products")
    suspend fun getProducts(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): Response<ProductsPageDTO>

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: String): Response<ProductDTO>
}
