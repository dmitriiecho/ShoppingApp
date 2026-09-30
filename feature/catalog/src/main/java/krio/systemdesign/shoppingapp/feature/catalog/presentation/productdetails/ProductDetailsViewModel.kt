package krio.systemdesign.shoppingapp.feature.catalog.presentation.productdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import krio.systemdesign.shoppingapp.core.ui.text.UiText
import krio.systemdesign.shoppingapp.domain.model.Product
import krio.systemdesign.shoppingapp.domain.usecase.AddToCartUseCase
import krio.systemdesign.shoppingapp.domain.usecase.ObserveCartUseCase
import krio.systemdesign.shoppingapp.domain.usecase.RemoveFromCartUseCase
import krio.systemdesign.shoppingapp.domain.usecase.UpdateCartQuantityUseCase
import krio.systemdesign.shoppingapp.feature.catalog.R
import krio.systemdesign.shoppingapp.feature.catalog.domain.model.ProductLoadResult
import krio.systemdesign.shoppingapp.feature.catalog.domain.usecase.GetProductUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import krio.systemdesign.shoppingapp.feature.catalog.presentation.navigation.CatalogRoutes
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val getProduct: GetProductUseCase,
    private val addToCart: AddToCartUseCase,
    private val updateCartQuantity: UpdateCartQuantityUseCase,
    private val removeFromCart: RemoveFromCartUseCase,
    observeCart: ObserveCartUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val args = savedStateHandle.toRoute<CatalogRoutes.ProductDetails>()
    private val productId = args.productId
    private val productName = args.productName

    private val productLoad = MutableStateFlow<ProductLoad>(ProductLoad.Loading)

    private val _effects = Channel<ProductDetailsEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    val uiState: StateFlow<ProductDetailsUiState> = combine(
        productLoad,
        observeCart(),
    ) { load, cart ->
        when (load) {
            ProductLoad.Loading -> ProductDetailsUiState.Loading(title = productName)
            is ProductLoad.Success -> ProductDetailsUiState.Content(
                product = load.product,
                cartQuantity = cart.items.firstOrNull { it.productId == productId }?.quantity ?: 0,
            )
            is ProductLoad.Error -> ProductDetailsUiState.Error(
                title = productName,
                message = load.message,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProductDetailsUiState.Loading(title = productName),
    )

    init {
        loadProduct()
    }

    fun onEvent(event: ProductDetailsEvent) {
        when (event) {
            is ProductDetailsEvent.OnAddToCart -> {
                val product = currentProduct() ?: return
                launchCartAction { addToCart(product, event.quantity) }
            }
            is ProductDetailsEvent.OnUpdateCartQuantity -> {
                launchCartAction { updateCartQuantity(productId, event.quantity) }
            }
            ProductDetailsEvent.OnRemoveFromCart -> {
                launchCartAction { removeFromCart(productId) }
            }
            ProductDetailsEvent.OnRetry -> loadProduct()
            ProductDetailsEvent.OnBackClick -> send(ProductDetailsEffect.NavigateBack)
        }
    }

    private fun currentProduct(): Product? =
        (productLoad.value as? ProductLoad.Success)?.product

    private fun loadProduct() {
        viewModelScope.launch {
            productLoad.value = ProductLoad.Loading
            val result = getProduct(productId)
            productLoad.value = when (result) {
                is ProductLoadResult.Success -> ProductLoad.Success(result.product)
                ProductLoadResult.NotFound -> ProductLoad.Error(UiText.Resource(R.string.catalog_product_not_found))
                is ProductLoadResult.Error -> ProductLoad.Error(UiText.Resource(R.string.catalog_product_load_error))
            }
        }
    }

    private fun launchCartAction(action: suspend () -> Result<Unit>) {
        viewModelScope.launch {
            action().onFailure {
                send(ProductDetailsEffect.ShowSnackBar(UiText.Resource(R.string.catalog_cart_update_error)))
            }
        }
    }

    private fun send(effect: ProductDetailsEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }

    private sealed interface ProductLoad {
        data object Loading : ProductLoad
        data class Success(val product: Product) : ProductLoad
        data class Error(val message: UiText) : ProductLoad
    }
}
