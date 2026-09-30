package krio.systemdesign.shoppingapp.feature.catalog.data.api

import krio.systemdesign.shoppingapp.feature.catalog.data.dto.ProductDTO
import krio.systemdesign.shoppingapp.feature.catalog.data.dto.ProductsPageDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductsApi {
    @GET("products")
    suspend fun getProducts(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): ProductsPageDTO

    @GET("products/{id}")
    suspend fun getProduct(
        @Path("id") id: String,
    ): ProductDTO
}
