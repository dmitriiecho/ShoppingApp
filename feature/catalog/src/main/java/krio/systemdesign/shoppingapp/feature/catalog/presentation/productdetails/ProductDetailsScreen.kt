package krio.systemdesign.shoppingapp.feature.catalog.presentation.productdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import krio.systemdesign.shoppingapp.core.ui.components.CartQuantityControl
import krio.systemdesign.shoppingapp.core.ui.components.NavigateBackIconButton
import krio.systemdesign.shoppingapp.core.ui.components.ProductImage
import krio.systemdesign.shoppingapp.core.ui.components.ProductImageKey
import krio.systemdesign.shoppingapp.core.ui.components.ShimmerPlaceholder
import krio.systemdesign.shoppingapp.core.ui.components.shimmerShape
import krio.systemdesign.shoppingapp.core.ui.text.asString
import krio.systemdesign.shoppingapp.core.ui.text.formatPrice
import krio.systemdesign.shoppingapp.domain.model.Product
import krio.systemdesign.shoppingapp.feature.catalog.R
import krio.systemdesign.shoppingapp.feature.catalog.presentation.component.OutOfStockButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    onBack: () -> Unit,
    viewModel: ProductDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                ProductDetailsEffect.NavigateBack -> onBack()
                is ProductDetailsEffect.ShowSnackBar -> {
                    launch { snackbarHostState.showSnackbar(effect.message.asString(resources)) }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.title.ifEmpty { stringResource(R.string.catalog_product_title) }) },
                navigationIcon = {
                    NavigateBackIconButton(
                        onClick = { viewModel.onEvent(ProductDetailsEvent.OnBackClick) },
                    )
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        val state = uiState
        // Экран с первого кадра выглядит как с загруженным товаром: картинка на своём месте (или заглушка на её месте,
        // если картинка неизвестна), а под ней заглушки описания или ошибка. Так картинке есть куда перелететь
        // из списка, и ничего не сдвигается, когда товар загрузится.
        if (state is ProductDetailsUiState.Error && state.imageUrl.isEmpty()) {
            ErrorContent(
                message = state.message.asString(),
                onRetry = { viewModel.onEvent(ProductDetailsEvent.OnRetry) },
                modifier = contentModifier,
            )
        } else {
            ProductDetailsContent(
                state = state,
                onEvent = viewModel::onEvent,
                modifier = contentModifier,
            )
        }
    }
}

// Картинка товара сверху и, в зависимости от состояния, описание товара, заглушки или ошибка под ней.
@Composable
private fun ProductDetailsContent(
    state: ProductDetailsUiState,
    onEvent: (ProductDetailsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            val imageModifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
            if (state.imageUrl.isNotEmpty()) {
                ProductImage(
                    imageUrl = state.imageUrl,
                    contentDescription = state.title,
                    modifier = imageModifier,
                    cornerRadius = 0.dp,
                    contentPadding = 32.dp,
                    sharedElementKey = ProductImageKey(state.productId),
                )
            } else {
                ShimmerPlaceholder(modifier = imageModifier) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .shimmerShape(RectangleShape),
                    )
                }
            }
            when (state) {
                is ProductDetailsUiState.Content -> ProductInfo(product = state.product)
                is ProductDetailsUiState.Loading -> ProductInfoPlaceholder()
                is ProductDetailsUiState.Error -> ErrorContent(
                    message = state.message.asString(),
                    onRetry = { onEvent(ProductDetailsEvent.OnRetry) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        val cartControlModifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        when (state) {
            is ProductDetailsUiState.Content -> CartControl(
                product = state.product,
                quantity = state.cartQuantity,
                onEvent = onEvent,
                modifier = cartControlModifier,
            )
            is ProductDetailsUiState.Loading -> ShimmerPlaceholder(modifier = cartControlModifier) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ButtonDefaults.MinHeight)
                        .shimmerShape(RoundedCornerShape(ButtonDefaults.MinHeight / 2)),
                )
            }
            is ProductDetailsUiState.Error -> Unit
        }
    }
}

@Composable
private fun ProductInfo(
    product: Product,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = product.name,
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = formatPrice(product.price),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        if (product.description.isNotBlank()) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CartControl(
    product: Product,
    quantity: Int,
    onEvent: (ProductDetailsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (product.isAvailable) {
        CartQuantityControl(
            quantity = quantity,
            onAdd = { onEvent(ProductDetailsEvent.OnAddToCart()) },
            onIncrease = { onEvent(ProductDetailsEvent.OnUpdateCartQuantity(quantity + 1)) },
            onDecrease = { onEvent(ProductDetailsEvent.OnUpdateCartQuantity(quantity - 1)) },
            onRemoveAll = { onEvent(ProductDetailsEvent.OnRemoveFromCart) },
            modifier = modifier,
            canIncrease = quantity < product.availableQuantity,
        )
    } else {
        OutOfStockButton(modifier = modifier)
    }
}

// Повторяет раскладку ProductInfo: название, цена и несколько строк описания.
@Composable
private fun ProductInfoPlaceholder(modifier: Modifier = Modifier) {
    ShimmerPlaceholder(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(28.dp)
                    .shimmerShape(RoundedCornerShape(4.dp)),
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(28.dp)
                    .shimmerShape(RoundedCornerShape(4.dp)),
            )
            Spacer(Modifier.height(16.dp))
            listOf(1f, 1f, 0.6f).forEach { widthFraction ->
                Box(
                    modifier = Modifier
                        .padding(vertical = 3.dp)
                        .fillMaxWidth(widthFraction)
                        .height(18.dp)
                        .shimmerShape(RoundedCornerShape(4.dp)),
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(24.dp),
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
