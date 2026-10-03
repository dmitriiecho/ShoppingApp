package krio.systemdesign.shoppingapp.feature.catalog.presentation.productlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import krio.systemdesign.shoppingapp.core.ui.components.buttons.CartQuantityControl
import krio.systemdesign.shoppingapp.core.ui.components.buttons.OutOfStockButton
import krio.systemdesign.shoppingapp.core.ui.components.buttons.ScrollToTopButton
import krio.systemdesign.shoppingapp.core.ui.components.cards.ProductCard
import krio.systemdesign.shoppingapp.core.ui.components.cards.ProductCardPlaceholder
import krio.systemdesign.shoppingapp.core.ui.components.images.ProductImageKey
import krio.systemdesign.shoppingapp.core.ui.components.inputs.SearchField
import krio.systemdesign.shoppingapp.core.ui.components.notices.ErrorBanner
import krio.systemdesign.shoppingapp.core.ui.components.screenstates.EmptyState
import krio.systemdesign.shoppingapp.core.ui.components.screenstates.ErrorState
import krio.systemdesign.shoppingapp.core.ui.icons.AppIcons
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.SearchOff
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.Storefront
import krio.systemdesign.shoppingapp.core.ui.text.asString
import krio.systemdesign.shoppingapp.core.ui.text.formatPrice
import krio.systemdesign.shoppingapp.core.ui.theme.Spacing
import krio.systemdesign.shoppingapp.domain.model.Product
import krio.systemdesign.shoppingapp.feature.catalog.R
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onBack: () -> Unit,
    onOpenProduct: (productId: String, productName: String, imageUrl: String) -> Unit,
    viewModel: ProductListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val products = viewModel.products.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                ProductListEffect.NavigateBack -> onBack()
                is ProductListEffect.NavigateToDetails -> {
                    onOpenProduct(effect.productId, effect.productName, effect.imageUrl)
                }
                is ProductListEffect.ShowSnackBar -> {
                    launch { snackbarHostState.showSnackbar(effect.message.asString(resources)) }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.catalog_title)) },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            SearchField(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onEvent(ProductListEvent.OnSearchQueryChanged(it)) },
                onClear = { viewModel.onEvent(ProductListEvent.OnClearSearch) },
                placeholder = stringResource(R.string.catalog_search_placeholder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.ScreenPadding),
            )
            ProductListBody(
                searchQuery = uiState.searchQuery,
                products = products,
                cartQuantities = uiState.cartQuantities,
                onEvent = viewModel::onEvent,
            )
        }
    }
}

@Composable
private fun ProductListBody(
    searchQuery: String,
    products: LazyPagingItems<Product>,
    cartQuantities: ImmutableMap<String, Int>,
    onEvent: (ProductListEvent) -> Unit,
) {
    val refresh = products.loadState.refresh
    // Обновление жестом не убирает список: пока оно идёт, видны прежние товары и индикатор сверху.
    // Остальные загрузки с нуля (первая, после смены запроса, по «Повторить») показывают вместо списка карточки-заглушки.
    var isPullRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(refresh) {
        if (refresh !is LoadState.Loading) isPullRefreshing = false
    }

    PullToRefreshBox(
        isRefreshing = isPullRefreshing && refresh is LoadState.Loading,
        onRefresh = {
            isPullRefreshing = true
            products.refresh()
        },
        modifier = Modifier.fillMaxSize(),
    ) {
        when {
            refresh is LoadState.Loading && !isPullRefreshing -> LoadingContent()
            refresh is LoadState.Error -> ErrorState(
                message = stringResource(R.string.catalog_load_error),
                onRetry = { products.retry() },
                modifier = Modifier.fillMaxSize(),
            )
            products.itemCount == 0 -> if (searchQuery.isBlank()) {
                EmptyContent(
                    icon = AppIcons.Storefront,
                    message = stringResource(R.string.catalog_empty),
                )
            } else {
                EmptyContent(
                    icon = AppIcons.SearchOff,
                    message = stringResource(R.string.catalog_search_no_results, searchQuery),
                )
            }
            else -> {
                val listState = rememberLazyListState()
                val scope = rememberCoroutineScope()
                LaunchedEffect(listState) {
                    snapshotFlow { listState.firstVisibleItemIndex }
                        .collect { onEvent(ProductListEvent.OnFirstVisibleItemChanged(it)) }
                }
                val isPrependErrorVisible by remember(listState, products) {
                    derivedStateOf {
                        products.loadState.prepend is LoadState.Error &&
                            listState.firstVisibleItemIndex < products.itemSnapshotList.placeholdersBefore
                    }
                }
                val isScrollToTopVisible by remember(listState) {
                    derivedStateOf { listState.firstVisibleItemIndex > 0 }
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    ProductList(
                        listState = listState,
                        products = products,
                        cartQuantities = cartQuantities,
                        onEvent = onEvent,
                    )
                    if (isPrependErrorVisible) {
                        ErrorBanner(
                            message = stringResource(R.string.catalog_prepend_error),
                            onRetry = { products.retry() },
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(horizontal = Spacing.ScreenPadding, vertical = 8.dp),
                        )
                    }
                    ScrollToTopButton(
                        visible = isScrollToTopVisible,
                        onClick = { scope.launch { listState.animateScrollToItem(0) } },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(Spacing.ScreenPadding),
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductList(
    listState: LazyListState,
    products: LazyPagingItems<Product>,
    cartQuantities: ImmutableMap<String, Int>,
    onEvent: (ProductListEvent) -> Unit,
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
        ) { index ->
            val product = products[index]
            if (product == null) {
                ProductCardPlaceholder(isLoading = prepend !is LoadState.Error)
                return@items
            }
            ProductListItem(
                product = product,
                quantity = cartQuantities[product.id] ?: 0,
                onClick = { onEvent(ProductListEvent.OnItemClick(product)) },
                onAddToCart = {
                    onEvent(ProductListEvent.OnAddToCart(product))
                },
                onUpdateQuantity = { quantity ->
                    onEvent(ProductListEvent.OnUpdateCartQuantity(product.id, quantity))
                },
                onRemoveFromCart = {
                    onEvent(ProductListEvent.OnRemoveFromCart(product.id))
                },
            )
        }

        when (append) {
            is LoadState.Loading -> {
                item(key = "append_loading") {
                    ProductCardPlaceholder()
                }
            }
            is LoadState.Error -> {
                item(key = "append_error") {
                    AppendError(
                        message = stringResource(R.string.catalog_append_error),
                        onRetry = { products.retry() },
                    )
                }
            }
            is LoadState.NotLoading -> Unit
        }
    }
}

@Composable
private fun ProductListItem(
    product: Product,
    quantity: Int,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    onUpdateQuantity: (Int) -> Unit,
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
                onIncrease = { onUpdateQuantity(quantity + 1) },
                onDecrease = { onUpdateQuantity(quantity - 1) },
                onRemoveAll = onRemoveFromCart,
                modifier = Modifier.fillMaxWidth(),
                canIncrease = quantity < product.availableQuantity,
            )
        } else {
            OutOfStockButton()
        }
    }
}

// Карточки-заглушки там же, где встанет список, поэтому товары появляются на их месте без сдвига.
// Карточек с запасом на весь экран; прокручивать их незачем.
@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PRODUCT_LIST_CONTENT_PADDING,
        verticalArrangement = Arrangement.spacedBy(Spacing.CardSpacing),
        userScrollEnabled = false,
    ) {
        items(LOADING_PLACEHOLDER_COUNT) {
            ProductCardPlaceholder()
        }
    }
}

// Обёрнут в LazyColumn, потому что потянуть для обновления можно только прокручиваемый экран.
@Composable
private fun EmptyContent(
    icon: ImageVector,
    message: String,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            EmptyState(
                icon = icon,
                message = message,
                modifier = Modifier.fillParentMaxSize(),
            )
        }
    }
}

@Composable
private fun AppendError(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error,
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.catalog_retry))
        }
    }
}

// Снизу запас под кнопку «Наверх» (её отступ + высота + зазор),
// чтобы в конце списка она не закрывала последнюю карточку.
private val PRODUCT_LIST_CONTENT_PADDING = PaddingValues(
    start = Spacing.ScreenPadding,
    top = Spacing.ScreenPadding,
    end = Spacing.ScreenPadding,
    bottom = Spacing.ScreenPadding + ButtonDefaults.MinHeight + Spacing.ScreenPadding,
)
private const val LOADING_PLACEHOLDER_COUNT = 8
