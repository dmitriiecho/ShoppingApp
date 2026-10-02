package krio.systemdesign.shoppingapp.uikit.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.ui.components.EmptyState
import krio.systemdesign.shoppingapp.core.ui.components.ErrorState
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.components.SampleData
import krio.systemdesign.shoppingapp.uikit.components.SampleList
import krio.systemdesign.shoppingapp.uikit.components.SampleVariant
import krio.systemdesign.shoppingapp.uikit.components.sampleGroup
import kotlinx.coroutines.delay

@Composable
fun ScreenStatesSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        sampleGroup("EmptyState") {
            SampleVariant(stringResource(R.string.uikit_variant_with_title)) {
                ScreenArea {
                    EmptyState(
                        icon = Icons.Outlined.ShoppingCart,
                        title = stringResource(R.string.uikit_sample_cart_empty_title),
                        message = stringResource(R.string.uikit_sample_cart_empty_message),
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            SampleVariant(stringResource(R.string.uikit_variant_without_title)) {
                ScreenArea {
                    EmptyState(
                        icon = Icons.Outlined.Inventory2,
                        message = stringResource(R.string.uikit_sample_nothing_found, SAMPLE_SEARCH_QUERY),
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
        sampleGroup("ErrorState") {
            SampleVariant(stringResource(R.string.uikit_variant_retry)) {
                var isRetrying by rememberSaveable { mutableStateOf(false) }
                LaunchedEffect(isRetrying) {
                    if (isRetrying) {
                        delay(SampleData.LOADING_MILLIS)
                        isRetrying = false
                    }
                }
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

// Место размером с небольшой экран: состояния экрана рассчитаны на всё свободное место, и видно, как они его занимают.
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
