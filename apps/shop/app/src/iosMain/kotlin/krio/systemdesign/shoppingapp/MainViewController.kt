package krio.systemdesign.shoppingapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import krio.systemdesign.shoppingapp.core.designsystem.components.screenstates.EmptyState
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Storefront
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import platform.UIKit.UIViewController

// The iOS app's entry point: the Swift side (apps/shop/ios) shows this controller full screen.
@Suppress("FunctionName", "unused")
fun MainViewController(): UIViewController = ComposeUIViewController { Placeholder() }

// Until the iOS app gets its dependency graph and shows App().
@Composable
private fun Placeholder(modifier: Modifier = Modifier) {
    ShoppingAppTheme {
        Surface(modifier = modifier.fillMaxSize()) {
            Box(contentAlignment = Alignment.Center) {
                EmptyState(
                    icon = AppIcons.Storefront,
                    title = "ShoppingApp",
                    message = "The shop's screens are moving here step by step",
                )
            }
        }
    }
}
