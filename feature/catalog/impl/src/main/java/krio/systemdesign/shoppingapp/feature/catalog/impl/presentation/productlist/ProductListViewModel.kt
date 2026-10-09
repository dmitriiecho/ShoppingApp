package krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productlist

import androidx.annotation.StringRes
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
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
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.launch
import krio.systemdesign.shoppingapp.core.composeutils.state.savedTextField
import krio.systemdesign.shoppingapp.core.composeutils.text.UiText
import krio.systemdesign.shoppingapp.feature.catalog.impl.R
import krio.systemdesign.shoppingapp.feature.catalog.impl.analytics.AddToCartAnalyticsEvent
import krio.systemdesign.shoppingapp.feature.catalog.impl.analytics.ProductsSearchedAnalyticsEvent
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.usecase.GetProductsUseCase
import krio.systemdesign.shoppingapp.shared.analytics.Analytics
import krio.systemdesign.shoppingapp.shared.analytics.event.AnalyticsScreen
import krio.systemdesign.shoppingapp.shared.analytics.event.cart.CartQuantityChangedAnalyticsEvent
import krio.systemdesign.shoppingapp.shared.analytics.event.cart.RemoveFromCartAnalyticsEvent
import krio.systemdesign.shoppingapp.shared.domain.model.Product
import krio.systemdesign.shoppingapp.shared.domain.usecase.AddToCartUseCase
import krio.systemdesign.shoppingapp.shared.domain.usecase.ObserveCartUseCase
import krio.systemdesign.shoppingapp.shared.domain.usecase.RemoveFromCartUseCase
import krio.systemdesign.shoppingapp.shared.domain.usecase.UpdateCartQuantityUseCase

@HiltViewModel
internal class ProductListViewModel @Inject constructor(
    private val getProducts: GetProductsUseCase,
    private val addToCart: AddToCartUseCase,
    private val updateCartQuantity: UpdateCartQuantityUseCase,
    private val removeFromCart: RemoveFromCartUseCase,
    observeCart: ObserveCartUseCase,
    private val analytics: Analytics,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Kept in SavedStateHandle, so the typed query survives process death.
    private val searchQuery: TextFieldState = savedStateHandle.savedTextField(KEY_SEARCH_QUERY)

    val uiState: StateFlow<ProductListUiState> = observeCart()
        .map { cart ->
            ProductListUiState(
                searchQuery = searchQuery,
                cartQuantities = cart.items.associate { it.productId to it.quantity },
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProductListUiState(searchQuery = searchQuery),
        )

    // After process death the first list opens at the page the user stopped at;
    // a new search query always starts from the first page.
    @OptIn(ExperimentalCoroutinesApi::class)
    val products: Flow<PagingData<Product>> = snapshotFlow { searchQuery.text.toString() }
        .searchTerms()
        .withIndex()
        // The first query is the one the screen opened with, not a search the user made.
        .onEach { (index, query) ->
            if (index > 0 && query.isNotEmpty()) analytics.log(ProductsSearchedAnalyticsEvent(query.length))
        }
        .flatMapLatest { (index, query) ->
            val initialPage: Int? = if (index == 0) savedStateHandle[KEY_FIRST_VISIBLE_PAGE] else null
            productsPager(query, initialPage).flow
        }
        .cachedIn(viewModelScope)

    private val _effects = Channel<ProductListEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: ProductListEvent) {
        when (event) {
            is ProductListEvent.OnProductClick -> openProduct(event.product)
            is ProductListEvent.OnAddToCartClick -> addProductToCart(event.product)
            is ProductListEvent.OnQuantityChange -> changeQuantity(event.productId, event.quantity)
            is ProductListEvent.OnRemoveFromCartClick -> removeProductFromCart(event.productId)
            is ProductListEvent.OnFirstVisibleItemChange -> saveFirstVisiblePage(event.index)
            ProductListEvent.OnRefreshFailed -> showSnackBar(R.string.catalog_load_error)
            ProductListEvent.OnBackClick -> send(ProductListEffect.NavigateBack)
        }
    }

    private fun openProduct(product: Product) {
        send(
            ProductListEffect.NavigateToDetails(
                productId = product.id,
                productName = product.name,
                imageUrl = product.imageUrl,
            ),
        )
    }

    private fun addProductToCart(product: Product) {
        launchCartAction(
            action = { addToCart(product) },
            onSuccess = {
                analytics.log(AddToCartAnalyticsEvent(product.id, product.price, AnalyticsScreen.CatalogList))
            },
        )
    }

    private fun changeQuantity(
        productId: String,
        quantity: Int,
    ) {
        launchCartAction(
            action = { updateCartQuantity(productId, quantity) },
            onSuccess = {
                analytics.log(CartQuantityChangedAnalyticsEvent(productId, quantity, AnalyticsScreen.CatalogList))
            },
        )
    }

    private fun removeProductFromCart(productId: String) {
        launchCartAction(
            action = { removeFromCart(productId) },
            onSuccess = { analytics.log(RemoveFromCartAnalyticsEvent(productId, AnalyticsScreen.CatalogList)) },
        )
    }

    // The page of the first visible card; the list reopens at it after process death.
    private fun saveFirstVisiblePage(index: Int) {
        savedStateHandle[KEY_FIRST_VISIBLE_PAGE] = index / PAGE_SIZE + ProductPagingSource.START_PAGE
    }

    private fun launchCartAction(
        onSuccess: () -> Unit = {},
        action: suspend () -> Result<Unit>,
    ) {
        viewModelScope.launch {
            action()
                .onSuccess { onSuccess() }
                .onFailure { showSnackBar(R.string.catalog_cart_update_error) }
        }
    }

    private fun showSnackBar(@StringRes message: Int) {
        send(ProductListEffect.ShowSnackBar(UiText.Resource(message)))
    }

    private fun send(effect: ProductListEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }

    private fun productsPager(
        query: String,
        initialPage: Int?,
    ): Pager<Int, Product> = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = PAGE_SIZE,
            prefetchDistance = PREFETCH_DISTANCE,
            // Placeholders keep the places of pages not loaded yet, so the list can reopen in the middle
            // and load upwards.
            enablePlaceholders = true,
        ),
        initialKey = initialPage,
        pagingSourceFactory = { ProductPagingSource(getProducts, query, PAGE_SIZE) },
    )

    companion object {
        private const val KEY_SEARCH_QUERY = "search_query"
        private const val KEY_FIRST_VISIBLE_PAGE = "first_visible_page"
        const val PAGE_SIZE = 10
        private const val PREFETCH_DISTANCE = 3
    }
}

// The server trims the query too; trimming here keeps a typed space from restarting the search
// and makes spaces alone show the whole catalog without the debounce.
@OptIn(FlowPreview::class)
private fun Flow<String>.searchTerms(): Flow<String> = this
    .map { it.trim() }
    .debounce { if (it.isEmpty()) 0L else SEARCH_DEBOUNCE_MS }
    .distinctUntilChanged()

internal const val SEARCH_DEBOUNCE_MS = 300L
