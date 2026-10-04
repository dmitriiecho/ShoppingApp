package krio.systemdesign.shoppingapp.uikit.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.components.screenstates.EmptyState
import krio.systemdesign.shoppingapp.core.ui.components.screenstates.ErrorState
import krio.systemdesign.shoppingapp.core.ui.icons.AppIcons
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.SearchOff
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.ShoppingCart
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.samples.rememberSampleLoading
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup

@Composable
fun ScreenStatesSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        sampleGroup("EmptyState") {
            SampleVariant(stringResource(R.string.uikit_variant_with_title)) {
                ScreenArea {
                    EmptyState(
                        icon = AppIcons.ShoppingCart,
                        title = stringResource(R.string.uikit_sample_cart_empty_title),
                        message = stringResource(R.string.uikit_sample_cart_empty_message),
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            SampleVariant(stringResource(R.string.uikit_variant_without_title)) {
                ScreenArea {
                    EmptyState(
                        icon = AppIcons.SearchOff,
                        message = stringResource(R.string.uikit_sample_nothing_found, SAMPLE_SEARCH_QUERY),
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
        sampleGroup("ErrorState") {
            SampleVariant(stringResource(R.string.uikit_variant_retry)) {
                var isRetrying by rememberSampleLoading()
                ScreenArea {
                    if (isRetrying) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else {
                        ErrorState(
                            message = stringResource(R.string.uikit_sample_load_error),
                            onRetry = { isRetrying = true },
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }
}

// An area the size of a small screen: screen states fill the free space, and this shows how.
@Composable
private fun ScreenArea(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(SCREEN_AREA_HEIGHT),
        content = content,
    )
}

private val SCREEN_AREA_HEIGHT = 320.dp
private const val SAMPLE_SEARCH_QUERY = "zzzz"
