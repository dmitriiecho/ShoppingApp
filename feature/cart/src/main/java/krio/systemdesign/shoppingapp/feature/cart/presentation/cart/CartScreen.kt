package krio.systemdesign.shoppingapp.feature.cart.presentation.cart

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel,
    onBack: () -> Unit,
    onOpenCheckout: () -> Unit,
    onOpenPromo: () -> Unit,
    onOpenProduct: (productId: String, productName: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cart") },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            uiState.appliedPromoCode?.let { promoCode ->
                Text(text = "Promo: $promoCode")
            }
            Button(onClick = onOpenPromo) {
                Text("Promo")
            }
            Button(onClick = onOpenCheckout) {
                Text("Checkout")
            }
            TextButton(onClick = { onOpenProduct("demo", "Demo") }) {
                Text("Open product")
            }
        }
    }
}
