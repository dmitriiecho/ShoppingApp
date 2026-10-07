package krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productlist

import androidx.paging.PagingSource
import androidx.paging.PagingState
import java.util.concurrent.ConcurrentHashMap
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.usecase.GetProductsUseCase
import krio.systemdesign.shoppingapp.shared.domain.model.Product

internal class ProductPagingSource(
    private val getProducts: GetProductsUseCase,
    private val query: String,
    private val pageSize: Int,
) : PagingSource<Int, Product>() {

    // Ids of products already returned. A renamed product can come twice (see ProductApi.getProducts),
    // and a repeated key crashes the list. Pages above and below load at once, so the set is thread-safe.
    private val loadedIds: MutableSet<String> = ConcurrentHashMap.newKeySet()

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val page = params.key ?: START_PAGE
        return getProducts(query, page, pageSize).fold(
            onSuccess = { result ->
                LoadResult.Page(
                    data = result.products.filter { loadedIds.add(it.id) },
                    prevKey = if (page == START_PAGE) null else page - 1,
                    nextKey = if (result.endReached) null else page + 1,
                    itemsBefore = (page - START_PAGE) * pageSize,
                )
            },
            onFailure = { LoadResult.Error(it) },
        )
    }

    companion object {
        const val START_PAGE = 1
    }
}
