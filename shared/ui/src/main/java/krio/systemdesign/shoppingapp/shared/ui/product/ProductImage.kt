package krio.systemdesign.shoppingapp.shared.ui.product

import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import coil3.compose.LocalPlatformContext
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import krio.systemdesign.shoppingapp.core.designsystem.components.loading.ShimmerPlaceholder
import krio.systemdesign.shoppingapp.core.designsystem.components.loading.shimmerShape
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.HeadphonesFilled
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShapeRadius
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme

@Composable
fun ProductImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = ShapeRadius.Medium,
    contentPadding: Dp = 2.dp,
    // Tiles with the same key on two screens fly from one to the other during navigation
    // (see LocalSharedTransitionScope).
    sharedElementKey: ProductImageKey? = null,
) {
    val shared = productImageSharedElement(sharedElementKey, cornerRadius)
    var isLoading by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .then(shared.tile)
            .clip(RoundedCornerShape(shared.cornerRadius))
            // While loading, the same placeholder as everywhere else; the tile comes with the image.
            .then(if (isLoading) Modifier else Modifier.background(productImageTile())),
    ) {
        if (isLoading) {
            ShimmerPlaceholder(modifier = Modifier.matchParentSize()) {
                ProductImagePlaceholder(
                    modifier = Modifier.fillMaxSize(),
                    cornerRadius = shared.cornerRadius,
                )
            }
        }
        ProductPhoto(
            imageUrl = imageUrl,
            contentDescription = contentDescription,
            onLoadingChange = { isLoading = it },
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .then(shared.image),
        )
    }
}

// The photo loaded by Coil. Tells the tile whether there's nothing to show yet.
@Composable
private fun ProductPhoto(
    imageUrl: String,
    contentDescription: String?,
    onLoadingChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val errorPainter = rememberProductImageErrorPainter()
    val context = LocalPlatformContext.current
    val request = remember(context, imageUrl) {
        ImageRequest.Builder(context)
            .data(imageUrl)
            // Show the cached image while the right size loads, e.g. the list image on the details screen,
            // instead of an empty tile.
            .placeholderMemoryCacheKey(imageUrl)
            .build()
    }
    AsyncImage(
        model = request,
        contentDescription = contentDescription,
        modifier = modifier,
        // A URL that has failed shows the error picture from the first frame while it's retried (see FailedImageUrls).
        placeholder = if (imageUrl in FailedImageUrls) errorPainter else null,
        error = errorPainter,
        // Nothing to show only if there's neither a cached image nor the error picture to start with.
        onLoading = { onLoadingChange(it.painter == null) },
        onSuccess = {
            onLoadingChange(false)
            FailedImageUrls.remove(imageUrl)
        },
        onError = {
            onLoadingChange(false)
            FailedImageUrls.add(imageUrl)
        },
        contentScale = ContentScale.Fit,
    )
}

// The product image's shape while its URL isn't known yet (a product opened by a deep link).
// Goes inside ShimmerPlaceholder, like the other placeholders.
@Composable
fun ProductImagePlaceholder(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = ShapeRadius.Medium,
) {
    Box(modifier = modifier.shimmerShape(RoundedCornerShape(cornerRadius)))
}

@OptIn(ExperimentalCoilApi::class)
@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProductImagePreview() {
    val loadedImage = rememberVectorPainter(AppIcons.HeadphonesFilled)
    ShoppingAppTheme {
        Surface {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf(
                    AsyncImagePreviewHandler { _, request ->
                        AsyncImagePainter.State.Success(
                            painter = loadedImage,
                            result = SuccessResult(image = ColorDrawable().asImage(), request = request),
                        )
                    },
                    AsyncImagePreviewHandler { _, request ->
                        AsyncImagePainter.State.Error(
                            painter = null,
                            result = ErrorResult(image = null, request = request, throwable = Throwable()),
                        )
                    },
                ).forEach { previewHandler ->
                    CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
                        ProductImage(
                            imageUrl = "",
                            contentDescription = "Wireless Headphones",
                            modifier = Modifier.size(88.dp),
                        )
                    }
                }
            }
        }
    }
}
