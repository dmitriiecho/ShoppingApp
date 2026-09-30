package krio.systemdesign.shoppingapp.feature.promo.presentation.promocode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import krio.systemdesign.shoppingapp.core.ui.components.NavigateBackIconButton
import krio.systemdesign.shoppingapp.core.ui.text.asString
import krio.systemdesign.shoppingapp.domain.model.PromoCode
import krio.systemdesign.shoppingapp.feature.promo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoCodeScreen(
    onBack: () -> Unit,
    onCloseWithResult: (PromoCode) -> Unit,
    viewModel: PromoCodeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is PromoCodeEffect.CloseWithResult -> {
                    onCloseWithResult(effect.promoCode)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.promo_code)) },
                navigationIcon = {
                    NavigateBackIconButton(onClick = onBack)
                },
            )
        },
    ) { innerPadding ->
        val error = uiState.error

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = uiState.promoCode,
                onValueChange = { viewModel.onEvent(PromoCodeEvent.OnPromoCodeChange(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.promo_code)) },
                isError = error != null,
                supportingText = if (error != null) {
                    { Text(error.asString()) }
                } else {
                    null
                },
                singleLine = true,
                enabled = !uiState.isLoading,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    autoCorrectEnabled = false,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { viewModel.onEvent(PromoCodeEvent.OnApplyClick) },
                ),
            )
            Button(
                onClick = { viewModel.onEvent(PromoCodeEvent.OnApplyClick) },
                enabled = uiState.canApply,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(stringResource(R.string.promo_apply))
                }
            }
        }
    }
}
