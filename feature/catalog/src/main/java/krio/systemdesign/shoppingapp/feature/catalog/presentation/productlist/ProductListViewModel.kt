package krio.systemdesign.shoppingapp.feature.catalog.presentation.productlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import krio.systemdesign.shoppingapp.domain.model.Product
import krio.systemdesign.shoppingapp.domain.usecase.AddToCartUseCase
import krio.systemdesign.shoppingapp.domain.usecase.ObserveCartUseCase
import krio.systemdesign.shoppingapp.domain.usecase.RemoveFromCartUseCase
import krio.systemdesign.shoppingapp.domain.usecase.UpdateCartQuantityUseCase
import krio.systemdesign.shoppingapp.feature.catalog.domain.usecase.GetProductsUseCase
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProducts: GetProductsUseCase,
    private val addToCart: AddToCartUseCase,
    private val updateCartQuantity: UpdateCartQuantityUseCase,
    private val removeFromCart: RemoveFromCartUseCase,
    observeCart: ObserveCartUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val searchQuery: StateFlow<String> = savedStateHandle.getStateFlow(LAST_SEARCH_QUERY, "")

    val cartQuantities: StateFlow<ImmutableMap<String, Int>> = observeCart()
        .map { cart -> cart.items.associate { it.productId to it.quantity }.toImmutableMap() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = persistentMapOf(),
        )

    private val _effects = Channel<ProductListEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    val products: Flow<PagingData<Product>> = searchQuery
        .map { it.trim() }
        .debounce { if (it.isEmpty()) 0L else SEARCH_DEBOUNCE_MS }
        .distinctUntilChanged()
        .flatMapLatest { productsPager(it).flow }
        .cachedIn(viewModelScope)

    fun onEvent(event: ProductListEvent) {
        when (event) {
            is ProductListEvent.OnItemClick -> {
                send(ProductListEffect.NavigateToDetails(event.product.id, event.product.name))
            }
            is ProductListEvent.OnAddToCart -> {
                launchCartAction {
                    addToCart(event.product, event.quantity)
                }
            }
            is ProductListEvent.OnUpdateCartQuantity -> {
                launchCartAction {
                    updateCartQuantity(event.productId, event.quantity)
                }
            }
            is ProductListEvent.OnRemoveFromCart -> {
                launchCartAction {
                    removeFromCart(event.productId)
                }
            }
            is ProductListEvent.OnSearchQueryChanged -> {
                savedStateHandle[LAST_SEARCH_QUERY] = event.query
            }
            ProductListEvent.OnClearSearch -> {
                savedStateHandle[LAST_SEARCH_QUERY] = ""
            }
            ProductListEvent.OnBackClick -> {
                send(ProductListEffect.NavigateBack)
            }
        }
    }

    private fun launchCartAction(action: suspend () -> Result<Unit>) {
        viewModelScope.launch {
            action().onFailure { error ->
                send(ProductListEffect.ShowSnackBar(error.message ?: "Не удалось обновить корзину"))
            }
        }
    }

    private fun send(effect: ProductListEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }

    private fun productsPager(query: String): Pager<Int, Product> = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = PAGE_SIZE,
            prefetchDistance = PREFETCH_DISTANCE,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = { ProductPagingSource(getProducts, query) },
    )

    private companion object {
        const val LAST_SEARCH_QUERY = "last_search_query"
        const val SEARCH_DEBOUNCE_MS = 300L
        const val PAGE_SIZE = 10
        const val PREFETCH_DISTANCE = 3
    }
}
