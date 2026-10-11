package krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productdetails

import android.content.res.Configuration
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import krio.systemdesign.shoppingapp.core.composeutils.effects.ObserveEffects
import krio.systemdesign.shoppingapp.core.composeutils.text.asString
import krio.systemdesign.shoppingapp.core.composeutils.viewmodel.injectedViewModel
import krio.systemdesign.shoppingapp.core.designsystem.components.buttons.NavigateBackIconButton
import krio.systemdesign.shoppingapp.core.designsystem.components.loading.ShimmerPlaceholder
import krio.systemdesign.shoppingapp.core.designsystem.components.screenstates.EmptyState
import krio.systemdesign.shoppingapp.core.designsystem.components.screenstates.ErrorState
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.LinkOff
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.designsystem.theme.contentBarWidth
import krio.systemdesign.shoppingapp.core.designsystem.theme.contentWidth
import krio.systemdesign.shoppingapp.feature.catalog.impl.R
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productdetails.components.CartControlSection
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productdetails.components.ProductInfoSection
import krio.systemdesign.shoppingapp.shared.ui.product.ProductImage
import krio.systemdesign.shoppingapp.shared.ui.product.ProductImageKey
import krio.systemdesign.shoppingapp.shared.ui.product.ProductImagePlaceholder

@Composable
@PublishedApi
internal fun ProductDetailsScreen(
    onBack: () -> Unit,
    viewModel: ProductDetailsViewModel = injectedViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    ObserveEffects(viewModel.effects) { effect ->
        when (effect) {
            ProductDetailsEffect.NavigateBack -> navigate { onBack() }
            is ProductDetailsEffect.ShowSnackBar -> showSnackbar(snackbarHostState, effect.message.asString(resources))
        }
    }

    ProductDetailsScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
internal fun ProductDetailsScreen(
    uiState: ProductDetailsUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (ProductDetailsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        // A wide screen held sideways: below a square image the details would start off the screen, so they go
        // beside it, and the bar spans both columns.
        val twoColumns = maxWidth > maxHeight && maxWidth >= TWO_COLUMNS_MIN_WIDTH
        ProductDetailsScaffold(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            twoColumns = twoColumns,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun ProductDetailsScaffold(
    uiState: ProductDetailsUiState,
    snackbarHostState: SnackbarHostState,
    twoColumns: Boolean,
    onEvent: (ProductDetailsEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            ProductDetailsTopBar(
                title = uiState.name,
                onBack = { onEvent(ProductDetailsEvent.OnBackClick) },
                modifier = if (twoColumns) Modifier else Modifier.contentBarWidth(),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        // Not found and the error take the whole screen, without the image even when it's known.
        when (uiState.details) {
            ProductDetailsUiState.Details.NotFound -> EmptyState(
                icon = AppIcons.LinkOff,
                message = stringResource(R.string.catalog_product_not_found),
                modifier = contentModifier,
            )
            ProductDetailsUiState.Details.Error -> ErrorState(
                message = stringResource(R.string.catalog_product_load_error),
                onRetry = { onEvent(ProductDetailsEvent.OnRetryClick) },
                modifier = contentModifier,
            )
            ProductDetailsUiState.Details.Loading, is ProductDetailsUiState.Details.Loaded -> if (twoColumns) {
                ProductDetailsTwoColumns(state = uiState, onEvent = onEvent, modifier = contentModifier)
            } else {
                ProductDetailsOneColumn(state = uiState, onEvent = onEvent, modifier = contentModifier)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailsTopBar(
    title: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = { Text(title ?: stringResource(R.string.catalog_product_title)) },
        modifier = modifier,
        navigationIcon = { NavigateBackIconButton(onClick = onBack) },
    )
}

// From the first frame the screen is laid out as if the product had loaded: the image (or its placeholder
// when the URL is unknown) with placeholders beside or below it. So the image has a place to fly to from the list,
// and nothing shifts when the product loads.
@Composable
private fun ProductDetailsOneColumn(
    state: ProductDetailsUiState,
    onEvent: (ProductDetailsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .contentWidth(),
        ) {
            ProductDetailsImage(
                productId = state.productId,
                imageUrl = state.imageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
            )
            ProductInfoSection(
                name = state.name,
                details = state.details,
            )
        }
        ProductCartControl(
            details = state.details,
            onEvent = onEvent,
            modifier = Modifier.contentWidth(),
        )
    }
}

// The image on the left, a square as tall as the screen; the details and the cart buttons take the rest and start
// right after it. On a nearly square screen (an unfolded foldable) the image gives way, so the details keep
// DETAILS_MIN_WIDTH.
@Composable
private fun ProductDetailsTwoColumns(
    state: ProductDetailsUiState,
    onEvent: (ProductDetailsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val imageSize = min(maxHeight, maxWidth - DETAILS_MIN_WIDTH)
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProductDetailsImage(
                productId = state.productId,
                imageUrl = state.imageUrl,
                modifier = Modifier.size(imageSize),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
                ProductInfoSection(
                    name = state.name,
                    details = state.details,
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                )
                ProductCartControl(details = state.details, onEvent = onEvent)
            }
        }
    }
}

@Composable
private fun ProductCartControl(
    details: ProductDetailsUiState.Details,
    onEvent: (ProductDetailsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    CartControlSection(
        details = details,
        onAddToCart = { onEvent(ProductDetailsEvent.OnAddToCartClick) },
        onQuantityChange = { onEvent(ProductDetailsEvent.OnQuantityChange(it)) },
        onRemoveFromCart = { onEvent(ProductDetailsEvent.OnRemoveFromCartClick) },
        modifier = modifier,
    )
}

@Composable
private fun ProductDetailsImage(
    productId: String,
    imageUrl: String?,
    modifier: Modifier = Modifier,
) {
    if (imageUrl != null) {
        ProductImage(
            imageUrl = imageUrl,
            modifier = modifier,
            cornerRadius = 0.dp,
            contentPadding = 32.dp,
            sharedElementKey = ProductImageKey(productId),
        )
    } else {
        ShimmerPlaceholder(modifier = modifier) {
            ProductImagePlaceholder(
                modifier = Modifier.fillMaxSize(),
                cornerRadius = 0.dp,
            )
        }
    }
}

private val TWO_COLUMNS_MIN_WIDTH = 600.dp
private val DETAILS_MIN_WIDTH = 300.dp

private fun previewState(
    details: ProductDetailsUiState.Details,
    knowsImage: Boolean = true,
) = ProductDetailsUiState(
    productId = "1",
    name = if (knowsImage) "Wireless Headphones" else null,
    imageUrl = if (knowsImage) "" else null,
    details = details,
)

private val previewLoaded = ProductDetailsUiState.Details.Loaded(
    price = 14999,
    description = "Over-ear wireless headphones in matte black, with thick soft ear cushions and a padded headband.",
    cartControl = ProductDetailsUiState.CartControl.InStock(quantity = 1, canIncrease = true),
)

@Composable
private fun ProductDetailsScreenPreview(state: ProductDetailsUiState) {
    ShoppingAppTheme {
        ProductDetailsScreen(
            uiState = state,
            snackbarHostState = remember { SnackbarHostState() },
            onEvent = {},
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductDetailsScreenLoadedPreview() {
    ProductDetailsScreenPreview(previewState(previewLoaded))
}

// A small tablet held sideways: the image and the details side by side.
@Preview(name = "Light", widthDp = 960, heightDp = 600)
@Preview(name = "Dark", widthDp = 960, heightDp = 600, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductDetailsScreenLandscapePreview() {
    ProductDetailsScreenPreview(previewState(previewLoaded))
}

// Opened by a deep link: nothing is known yet.
@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductDetailsScreenDeepLinkLoadingPreview() {
    ProductDetailsScreenPreview(previewState(ProductDetailsUiState.Details.Loading, knowsImage = false))
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductDetailsScreenNotFoundPreview() {
    ProductDetailsScreenPreview(previewState(ProductDetailsUiState.Details.NotFound, knowsImage = false))
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductDetailsScreenErrorPreview() {
    ProductDetailsScreenPreview(previewState(ProductDetailsUiState.Details.Error))
}
