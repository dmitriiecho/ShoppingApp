package krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productlist

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.MutableStateFlow
import krio.systemdesign.shoppingapp.core.composeutils.effects.ObserveEffects
import krio.systemdesign.shoppingapp.core.composeutils.text.asString
import krio.systemdesign.shoppingapp.core.designsystem.components.inputs.SearchField
import krio.systemdesign.shoppingapp.core.designsystem.components.screenstates.EmptyState
import krio.systemdesign.shoppingapp.core.designsystem.components.screenstates.ErrorState
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.SearchOff
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Storefront
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.designsystem.theme.Spacing
import krio.systemdesign.shoppingapp.feature.catalog.impl.R
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productlist.components.ProductList
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productlist.components.ProductListPlaceholder
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productlist.components.previewProducts
import krio.systemdesign.shoppingapp.shared.domain.model.Product

@Composable
internal fun ProductListScreen(
    onBack: () -> Unit,
    onOpenProduct: (productId: String, productName: String, imageUrl: String) -> Unit,
    viewModel: ProductListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val products = viewModel.products.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    ObserveEffects(viewModel.effects) { effect ->
        when (effect) {
            ProductListEffect.NavigateBack -> navigate { onBack() }
            is ProductListEffect.NavigateToDetails -> navigate {
                onOpenProduct(effect.productId, effect.productName, effect.imageUrl)
            }
            is ProductListEffect.ShowSnackBar -> showSnackbar(snackbarHostState, effect.message.asString(resources))
        }
    }

    ProductListScreen(
        uiState = uiState,
        products = products,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
    )
}

// The products come apart from uiState: PagingData is a stream that can't be held in a state.
@Composable
internal fun ProductListScreen(
    uiState: ProductListUiState,
    products: LazyPagingItems<Product>,
    snackbarHostState: SnackbarHostState,
    onEvent: (ProductListEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { ProductListTopBar() },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            SearchField(
                state = uiState.searchQuery,
                placeholder = stringResource(R.string.catalog_search_placeholder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.ScreenPadding),
            )
            ProductListContent(
                searchQuery = uiState.searchQuery,
                products = products,
                cartQuantities = uiState.cartQuantities,
                onEvent = onEvent,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductListTopBar() {
    TopAppBar(
        title = { Text(stringResource(R.string.catalog_title)) },
    )
}

@Composable
private fun ProductListContent(
    searchQuery: TextFieldState,
    products: LazyPagingItems<Product>,
    cartQuantities: Map<String, Int>,
    onEvent: (ProductListEvent) -> Unit,
) {
    val refresh = products.loadState.refresh
    // A pull-to-refresh keeps the list: the old products stay, with the indicator on top.
    // Other loads from scratch (the first one, after a new query, on Retry) show placeholder cards instead.
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
            refresh is LoadState.Loading && !isPullRefreshing -> ProductListPlaceholder()
            refresh is LoadState.Error -> ErrorState(
                message = stringResource(R.string.catalog_load_error),
                onRetry = { products.retry() },
                modifier = Modifier.fillMaxSize(),
            )
            products.itemCount == 0 -> ProductListEmpty(searchQuery = searchQuery)
            else -> ProductList(
                products = products,
                cartQuantities = cartQuantities,
                onProductClick = { onEvent(ProductListEvent.OnProductClick(it)) },
                onAddToCart = { onEvent(ProductListEvent.OnAddToCartClick(it)) },
                onQuantityChange = { id, quantity -> onEvent(ProductListEvent.OnQuantityChange(id, quantity)) },
                onRemoveFromCart = { onEvent(ProductListEvent.OnRemoveFromCartClick(it)) },
                onFirstVisibleItemChange = { onEvent(ProductListEvent.OnFirstVisibleItemChange(it)) },
            )
        }
    }
}

// The query is read only here, so typing doesn't redraw a list that has products.
@Composable
private fun ProductListEmpty(searchQuery: TextFieldState) {
    val query = searchQuery.text.toString()
    if (query.isBlank()) {
        EmptyContent(
            icon = AppIcons.Storefront,
            message = stringResource(R.string.catalog_empty),
        )
    } else {
        EmptyContent(
            icon = AppIcons.SearchOff,
            message = stringResource(R.string.catalog_search_no_results, query),
        )
    }
}

// Inside a LazyColumn because only a scrollable can be pulled to refresh.
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
private fun ProductListScreenPreview(
    products: List<Product> = previewProducts,
    refresh: LoadState = LoadState.NotLoading(endOfPaginationReached = true),
    query: String = "",
) {
    val loadStates = LoadStates(
        refresh = refresh,
        prepend = LoadState.NotLoading(endOfPaginationReached = true),
        append = LoadState.NotLoading(endOfPaginationReached = true),
    )
    ShoppingAppTheme {
        ProductListScreen(
            uiState = ProductListUiState(searchQuery = remember { TextFieldState(query) }),
            products = remember { MutableStateFlow(PagingData.from(products, loadStates)) }.collectAsLazyPagingItems(),
            snackbarHostState = remember { SnackbarHostState() },
            onEvent = {},
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductListScreenLoadedPreview() {
    ProductListScreenPreview()
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductListScreenLoadingPreview() {
    ProductListScreenPreview(products = emptyList(), refresh = LoadState.Loading)
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductListScreenNoResultsPreview() {
    ProductListScreenPreview(products = emptyList(), query = "zzz")
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductListScreenErrorPreview() {
    ProductListScreenPreview(products = emptyList(), refresh = LoadState.Error(Throwable()))
}
