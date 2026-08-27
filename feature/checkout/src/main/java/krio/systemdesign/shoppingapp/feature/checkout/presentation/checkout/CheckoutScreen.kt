package krio.systemdesign.shoppingapp.feature.checkout.presentation.checkout

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import krio.systemdesign.shoppingapp.core.ui.components.CloseIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onClose: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                actions = {
                    CloseIconButton(onClick = onClose)
                },
            )
        },
    ) { innerPadding ->
        Text(
            text = if (uiState.isSubmitting) "Submitting…" else "Checkout",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        )
    }
}
