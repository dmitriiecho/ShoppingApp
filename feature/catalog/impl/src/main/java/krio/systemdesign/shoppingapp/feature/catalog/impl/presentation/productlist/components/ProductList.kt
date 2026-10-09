package krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productlist.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import krio.systemdesign.shoppingapp.core.designsystem.components.buttons.ScrollToTopButton
import krio.systemdesign.shoppingapp.core.designsystem.components.notices.ErrorBanner
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.designsystem.theme.Spacing
import krio.systemdesign.shoppingapp.core.designsystem.theme.contentWidth
import krio.systemdesign.shoppingapp.feature.catalog.impl.R
import krio.systemdesign.shoppingapp.feature.catalog.ui.OutOfStockButton
import krio.systemdesign.shoppingapp.shared.domain.model.Product
import krio.systemdesign.shoppingapp.shared.domain.model.canAddOneMore
import krio.systemdesign.shoppingapp.shared.ui.cart.CartQuantityControl
import krio.systemdesign.shoppingapp.shared.ui.product.ProductCard
import krio.systemdesign.shoppingapp.shared.ui.product.ProductCardPlaceholder
import krio.systemdesign.shoppingapp.shared.ui.product.ProductImageKey
import krio.systemdesign.shoppingapp.shared.ui.text.formatPrice

// The product cards, with an error banner on top when the page above failed and "Back to top" at the bottom.
@Composable
internal fun ProductList(
    products: LazyPagingItems<Product>,
    cartQuantities: Map<String, Int>,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onQuantityChange: (productId: String, quantity: Int) -> Unit,
    onRemoveFromCart: (productId: String) -> Unit,
    // Remembered by the screen, so the list reopens near the same place after process death.
    onFirstVisibleItemChange: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    ReportFirstVisibleItem(listState, onFirstVisibleItemChange)
    Box(modifier = modifier.fillMaxSize()) {
        ProductCards(
            listState = listState,
            products = products,
            cartQuantities = cartQuantities,
            onProductClick = onProductClick,
            onAddToCart = onAddToCart,
            onQuantityChange = onQuantityChange,
            onRemoveFromCart = onRemoveFromCart,
        )
        AbovePageErrorBanner(
            listState = listState,
            products = products,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = Spacing.ScreenPadding, vertical = 8.dp)
                .contentWidth(),
        )
        BackToTopButton(
            listState = listState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(Spacing.ScreenPadding),
        )
    }
}

@Composable
private fun ReportFirstVisibleItem(
    listState: LazyListState,
    onChange: (index: Int) -> Unit,
) {
    val currentOnChange by rememberUpdatedState(onChange)
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }.collect { currentOnChange(it) }
    }
}

// Shown once the user scrolls up into the places of a page that failed to load.
@Composable
private fun AbovePageErrorBanner(
    listState: LazyListState,
    products: LazyPagingItems<Product>,
    modifier: Modifier = Modifier,
) {
    val isVisible by remember(listState, products) {
        derivedStateOf {
            products.loadState.prepend is LoadState.Error &&
                listState.firstVisibleItemIndex < products.itemSnapshotList.placeholdersBefore
        }
    }
    if (isVisible) {
        ErrorBanner(
            message = stringResource(R.string.catalog_load_error),
            onRetry = { products.retry() },
            modifier = modifier,
        )
    }
}

@Composable
private fun BackToTopButton(
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val isVisible by remember(listState) {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }
    ScrollToTopButton(
        visible = isVisible,
        onClick = { scope.launch { listState.animateScrollToItem(0) } },
        modifier = modifier,
    )
}

// The cards themselves, with the next page's placeholder or error at the end.
@Composable
private fun ProductCards(
    listState: LazyListState,
    products: LazyPagingItems<Product>,
    cartQuantities: Map<String, Int>,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onQuantityChange: (productId: String, quantity: Int) -> Unit,
    onRemoveFromCart: (productId: String) -> Unit,
) {
    val prepend = products.loadState.prepend
    val append = products.loadState.append

    LazyColumn(
        state = listState,
        contentPadding = PRODUCT_LIST_CONTENT_PADDING,
        verticalArrangement = Arrangement.spacedBy(Spacing.CardSpacing),
    ) {
        items(
            count = products.itemCount,
            key = products.itemKey { it.id },
            // Paging gives not-loaded places their own type, so a product card is reused only for a product card.
            contentType = products.itemContentType { PRODUCT_CONTENT_TYPE },
        ) { index ->
            val product = products[index]
            if (product == null) {
                // The places above stop shimmering once their page has failed: they won't load until Retry.
                ProductCardPlaceholder(
                    modifier = Modifier.contentWidth(),
                    isAnimating = prepend !is LoadState.Error,
                )
                return@items
            }
            ProductListItem(
                product = product,
                quantity = cartQuantities[product.id] ?: 0,
                onClick = { onProductClick(product) },
                onAddToCart = { onAddToCart(product) },
                onQuantityChange = { quantity -> onQuantityChange(product.id, quantity) },
                onRemoveFromCart = { onRemoveFromCart(product.id) },
                modifier = Modifier.contentWidth(),
            )
        }

        when (append) {
            is LoadState.Loading -> item(key = "append_loading") {
                ProductCardPlaceholder(modifier = Modifier.contentWidth())
            }
            is LoadState.Error -> item(key = "append_error") {
                ErrorBanner(
                    message = stringResource(R.string.catalog_load_error),
                    onRetry = { products.retry() },
                    modifier = Modifier.contentWidth(),
                )
            }
            is LoadState.NotLoading -> Unit
        }
    }
}

// Placeholder cards where the list will be, so the products appear in their place without a shift.
// Enough of them for a whole screen; there's nothing to scroll.
@Composable
internal fun ProductListPlaceholder(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PRODUCT_LIST_CONTENT_PADDING,
        verticalArrangement = Arrangement.spacedBy(Spacing.CardSpacing),
        userScrollEnabled = false,
    ) {
        items(PLACEHOLDER_COUNT) {
            ProductCardPlaceholder(modifier = Modifier.contentWidth())
        }
    }
}

@Composable
private fun ProductListItem(
    product: Product,
    quantity: Int,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    onQuantityChange: (Int) -> Unit,
    onRemoveFromCart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ProductCard(
        name = product.name,
        imageUrl = product.imageUrl,
        price = formatPrice(product.price),
        onClick = onClick,
        modifier = modifier,
        sharedElementKey = ProductImageKey(product.id),
    ) {
        if (product.isAvailable) {
            CartQuantityControl(
                quantity = quantity,
                onAdd = onAddToCart,
                onIncrease = { onQuantityChange(quantity + 1) },
                onDecrease = { onQuantityChange(quantity - 1) },
                onRemoveAll = onRemoveFromCart,
                modifier = Modifier.fillMaxWidth(),
                canIncrease = canAddOneMore(inCart = quantity, stock = product.availableQuantity),
            )
        } else {
            OutOfStockButton()
        }
    }
}

// Room at the bottom for the "Back to top" button (its padding, height and a gap),
// so at the end of the list it doesn't cover the last card.
private val PRODUCT_LIST_CONTENT_PADDING = PaddingValues(
    start = Spacing.ScreenPadding,
    top = Spacing.ScreenPadding,
    end = Spacing.ScreenPadding,
    bottom = Spacing.ScreenPadding + ButtonDefaults.MinHeight + Spacing.ScreenPadding,
)
private const val PLACEHOLDER_COUNT = 8
private const val PRODUCT_CONTENT_TYPE = "product"

internal val previewProducts = listOf(
    Product("1", "Wireless Headphones", 14999, "", "", availableQuantity = 10),
    Product("2", "Mechanical Keyboard", 10995, "", "", availableQuantity = 1),
    Product("3", "USB-C Hub", 2999, "", "", availableQuantity = 0),
)

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductListPreview() {
    ShoppingAppTheme {
        Surface {
            ProductList(
                products = remember { MutableStateFlow(PagingData.from(previewProducts)) }.collectAsLazyPagingItems(),
                // In the cart: one keyboard, the whole stock, so its "+" is off.
                cartQuantities = mapOf("2" to 1),
                onProductClick = {},
                onAddToCart = {},
                onQuantityChange = { _, _ -> },
                onRemoveFromCart = {},
                onFirstVisibleItemChange = {},
            )
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductListPlaceholderPreview() {
    ShoppingAppTheme {
        Surface {
            ProductListPlaceholder()
        }
    }
}
