package krio.systemdesign.shoppingapp.feature.catalog.data.api

import krio.systemdesign.shoppingapp.feature.catalog.data.dto.ProductDTO
import krio.systemdesign.shoppingapp.feature.catalog.data.dto.ProductsPageDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductsApi {
    // Список режется на страницы по номеру. Это безопасно благодаря договорённости с сервером:
    // - товары не удаляются: закончившийся товар остаётся в выдаче, в том числе в поиске,
    //   с нулевым остатком;
    // - новые товары получают id больше существующих, а список отсортирован по id по возрастанию,
    //   поэтому новые товары появляются только на новых страницах.
    // Исключение — переименование: товар может появиться или пропасть в середине результатов поиска.
    // Повторы, которые при этом возможны, отбрасывает ProductPagingSource.
    @GET("products")
    suspend fun getProducts(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): Response<ProductsPageDTO>

    @GET("products/{id}")
    suspend fun getProduct(
        @Path("id") id: String,
    ): Response<ProductDTO>
}
