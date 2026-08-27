package krio.systemdesign.shoppingapp.feature.promo.presentation.promocode

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import krio.systemdesign.shoppingapp.core.ui.components.NavigateBackIconButton
import krio.systemdesign.shoppingapp.feature.promo.presentation.navigation.PromoCodeResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoCodeScreen(
    onBack: () -> Unit,
    onCloseWithResult: (PromoCodeResult) -> Unit,
    viewModel: PromoCodeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Promo") },
                navigationIcon = {
                    NavigateBackIconButton(onClick = onBack)
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            OutlinedTextField(
                value = uiState.promoCode,
                onValueChange = viewModel::onPromoCodeChange,
                label = { Text("Promo code") },
            )
            Button(
                onClick = { onCloseWithResult(PromoCodeResult(uiState.promoCode)) },
                enabled = uiState.promoCode.isNotBlank(),
            ) {
                Text("Apply")
            }
        }
    }
}
