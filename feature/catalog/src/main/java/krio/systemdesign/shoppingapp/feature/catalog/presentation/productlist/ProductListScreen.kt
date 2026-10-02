package krio.systemdesign.shoppingapp.feature.catalog.presentation.productlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import krio.systemdesign.shoppingapp.core.ui.components.AppCard
import krio.systemdesign.shoppingapp.core.ui.components.CartQuantityControl
import krio.systemdesign.shoppingapp.core.ui.components.CloseIconButton
import krio.systemdesign.shoppingapp.core.ui.components.ProductImage
import krio.systemdesign.shoppingapp.core.ui.components.ProductImageKey
import krio.systemdesign.shoppingapp.core.ui.components.ShimmerPlaceholder
import krio.systemdesign.shoppingapp.core.ui.components.shimmerShape
import krio.systemdesign.shoppingapp.core.ui.text.asString
import krio.systemdesign.shoppingapp.core.ui.text.formatPrice
import krio.systemdesign.shoppingapp.domain.model.Product
import krio.systemdesign.shoppingapp.feature.catalog.R
import krio.systemdesign.shoppingapp.feature.catalog.presentation.component.OutOfStockButton
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
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        placeholder = { Text(stringResource(R.string.catalog_search_placeholder)) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                CloseIconButton(onClick = onClear)
            }
        },
        singleLine = true,
    )
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
    // Остальные загрузки с нуля (первая, после смены запроса, по «Повторить») показывают индикатор на весь экран.
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
            refresh is LoadState.Error -> ErrorContent(
                message = stringResource(R.string.catalog_load_error),
                onRetry = { products.retry() },
            )
            products.itemCount == 0 -> EmptyContent(
                message = if (searchQuery.isBlank()) {
                    stringResource(R.string.catalog_empty)
                } else {
                    stringResource(R.string.catalog_search_no_results, searchQuery)
                },
            )
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
                        PrependErrorBanner(
                            onRetry = { products.retry() },
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    }
                    ScrollToTopButton(
                        visible = isScrollToTopVisible,
                        onClick = { scope.launch { listState.animateScrollToItem(0) } },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
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
        // Снизу запас под кнопку «Наверх» (её отступ + высота + зазор),
        // чтобы в конце списка она не закрывала последнюю карточку.
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = 16.dp + ButtonDefaults.MinHeight + 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            count = products.itemCount,
            key = products.itemKey { it.id },
        ) { index ->
            val product = products[index]
            if (product == null) {
                ProductListItemPlaceholder(isLoading = prepend !is LoadState.Error)
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
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
    AppCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProductImage(
                    imageUrl = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier.size(88.dp),
                    sharedElementKey = ProductImageKey(product.id),
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = formatPrice(product.price),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
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
}

// Повторяет размеры ProductListItem, чтобы список не прыгал, когда заглушка сменяется товаром.
@Composable
private fun ProductListItemPlaceholder(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier.fillMaxWidth()) {
        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            isAnimating = isLoading,
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .shimmerShape(RoundedCornerShape(12.dp)),
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(20.dp)
                                .shimmerShape(RoundedCornerShape(4.dp)),
                        )
                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .width(96.dp)
                                .height(20.dp)
                                .shimmerShape(RoundedCornerShape(4.dp)),
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ButtonDefaults.MinHeight)
                        .shimmerShape(RoundedCornerShape(ButtonDefaults.MinHeight / 2)),
                )
            }
        }
    }
}

@Composable
private fun PrependErrorBanner(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.catalog_prepend_error),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
            TextButton(
                onClick = onRetry,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                ),
            ) {
                Text(stringResource(R.string.catalog_retry))
            }
        }
    }
}

@Composable
private fun ScrollToTopButton(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
    ) {
        // Высотой как кнопка «В корзину» на карточках (CartQuantityControl).
        ExtendedFloatingActionButton(
            onClick = onClick,
            modifier = Modifier.height(ButtonDefaults.MinHeight),
        ) {
            Text(stringResource(R.string.catalog_scroll_to_top))
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.catalog_retry))
        }
    }
}

// Обёрнут в LazyColumn, потому что потянуть для обновления можно только прокручиваемый экран.
@Composable
private fun EmptyContent(
    message: String,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            Column(
                modifier = Modifier
                    .fillParentMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Inventory2,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
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

