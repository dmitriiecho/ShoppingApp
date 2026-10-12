package krio.systemdesign.shoppingapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import krio.systemdesign.shoppingapp.core.designsystem.components.screenstates.EmptyState
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.Storefront
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme

// A placeholder until the shop's screens move to the shared code, drawn with the design system.
@Composable
fun App(modifier: Modifier = Modifier) {
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
