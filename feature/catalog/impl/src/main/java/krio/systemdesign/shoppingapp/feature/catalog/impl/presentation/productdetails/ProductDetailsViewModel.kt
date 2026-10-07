package krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import krio.systemdesign.shoppingapp.core.composeutils.text.UiText
import krio.systemdesign.shoppingapp.feature.catalog.impl.R
import krio.systemdesign.shoppingapp.feature.catalog.impl.analytics.AddToCartAnalyticsEvent
import krio.systemdesign.shoppingapp.feature.catalog.impl.analytics.ProductViewedAnalyticsEvent
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.model.ProductLoadResult
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.usecase.GetProductUseCase
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.navigation.ProductDetailsRoute
import krio.systemdesign.shoppingapp.shared.analytics.Analytics
import krio.systemdesign.shoppingapp.shared.analytics.event.AnalyticsScreen
import krio.systemdesign.shoppingapp.shared.analytics.event.cart.CartQuantityChangedAnalyticsEvent
import krio.systemdesign.shoppingapp.shared.analytics.event.cart.RemoveFromCartAnalyticsEvent
import krio.systemdesign.shoppingapp.shared.domain.model.Product
import krio.systemdesign.shoppingapp.shared.domain.model.canAddOneMore
import krio.systemdesign.shoppingapp.shared.domain.usecase.AddToCartUseCase
import krio.systemdesign.shoppingapp.shared.domain.usecase.ObserveCartUseCase
import krio.systemdesign.shoppingapp.shared.domain.usecase.RemoveFromCartUseCase
import krio.systemdesign.shoppingapp.shared.domain.usecase.UpdateCartQuantityUseCase
import krio.systemdesign.shoppingapp.shared.ui.product.PRODUCT_IMAGE_TRANSITION_MILLIS

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val getProduct: GetProductUseCase,
    private val addToCart: AddToCartUseCase,
    private val updateCartQuantity: UpdateCartQuantityUseCase,
    private val removeFromCart: RemoveFromCartUseCase,
    observeCart: ObserveCartUseCase,
    private val analytics: Analytics,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Opened by different routes, so the fields are read through ProductDetailsRoute.
    private val productId: String = checkNotNull(savedStateHandle[ProductDetailsRoute::productId.name])
    private val productName: String? = savedStateHandle[ProductDetailsRoute::productName.name]
    private val imageUrl: String? = savedStateHandle[ProductDetailsRoute::imageUrl.name]

    private val productLoad = MutableStateFlow<ProductLoad>(ProductLoad.Loading)

    val uiState: StateFlow<ProductDetailsUiState> = combine(
        productLoad,
        observeCart().map { it.quantityOf(productId) },
    ) { load, cartQuantity ->
        ProductDetailsUiState(
            productId = productId,
            name = load.product?.name ?: productName,
            imageUrl = load.product?.imageUrl ?: imageUrl,
            details = load.toDetails(cartQuantity),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProductDetailsUiState(productId = productId, name = productName, imageUrl = imageUrl),
    )

    private val _effects = Channel<ProductDetailsEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        loadProduct()
    }

    fun onEvent(event: ProductDetailsEvent) {
        when (event) {
            ProductDetailsEvent.OnAddToCartClick -> addProductToCart()
            is ProductDetailsEvent.OnQuantityChange -> changeQuantity(event.quantity)
            ProductDetailsEvent.OnRemoveFromCartClick -> removeProductFromCart()
            ProductDetailsEvent.OnRetryClick -> loadProduct()
            ProductDetailsEvent.OnBackClick -> send(ProductDetailsEffect.NavigateBack)
        }
    }

    private fun addProductToCart() {
        val product = productLoad.value.product ?: return
        launchCartAction(
            action = { addToCart(product) },
            onSuccess = {
                analytics.log(AddToCartAnalyticsEvent(product.id, product.price, AnalyticsScreen.ProductDetails))
            },
        )
    }

    private fun changeQuantity(quantity: Int) {
        launchCartAction(
            action = { updateCartQuantity(productId, quantity) },
            onSuccess = {
                analytics.log(CartQuantityChangedAnalyticsEvent(productId, quantity, AnalyticsScreen.ProductDetails))
            },
        )
    }

    private fun removeProductFromCart() {
        launchCartAction(
            action = { removeFromCart(productId) },
            onSuccess = { analytics.log(RemoveFromCartAnalyticsEvent(productId, AnalyticsScreen.ProductDetails)) },
        )
    }

    private fun loadProduct() {
        viewModelScope.launch {
            productLoad.value = ProductLoad.Loading
            // The details appear no earlier than the screen transition ends, so they don't change the layout under
            // the flying image. The wait runs alongside the load: a slow load waits no extra.
            val result = coroutineScope {
                launch { delay((PRODUCT_IMAGE_TRANSITION_MILLIS).milliseconds) }
                getProduct(productId)
            }
            if (result is ProductLoadResult.Success) {
                analytics.log(ProductViewedAnalyticsEvent(result.product.id, result.product.price))
            }
            productLoad.value = ProductLoad.Finished(result)
        }
    }

    private fun launchCartAction(
        onSuccess: () -> Unit = {},
        action: suspend () -> Result<Unit>,
    ) {
        viewModelScope.launch {
            action()
                .onSuccess { onSuccess() }
                .onFailure {
                    send(ProductDetailsEffect.ShowSnackBar(UiText.Resource(R.string.catalog_cart_update_error)))
                }
        }
    }

    private fun send(effect: ProductDetailsEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }
}

private sealed interface ProductLoad {
    data object Loading : ProductLoad
    data class Finished(val result: ProductLoadResult) : ProductLoad

    // The product once it has loaded, null while loading or if it couldn't be loaded.
    val product: Product?
        get() = ((this as? Finished)?.result as? ProductLoadResult.Success)?.product
}

private fun ProductLoad.toDetails(cartQuantity: Int): ProductDetailsUiState.Details = when (this) {
    ProductLoad.Loading -> ProductDetailsUiState.Details.Loading
    is ProductLoad.Finished -> result.toDetails(cartQuantity)
}

private fun ProductLoadResult.toDetails(cartQuantity: Int): ProductDetailsUiState.Details = when (this) {
    is ProductLoadResult.Success -> product.toLoaded(cartQuantity)
    ProductLoadResult.NotFound -> ProductDetailsUiState.Details.NotFound
    is ProductLoadResult.Error -> ProductDetailsUiState.Details.Error
}

private fun Product.toLoaded(cartQuantity: Int) = ProductDetailsUiState.Details.Loaded(
    price = price,
    description = description,
    cartControl = if (isAvailable) {
        ProductDetailsUiState.CartControl.InStock(
            quantity = cartQuantity,
            canIncrease = canAddOneMore(inCart = cartQuantity, stock = availableQuantity),
        )
    } else {
        ProductDetailsUiState.CartControl.OutOfStock
    },
)
