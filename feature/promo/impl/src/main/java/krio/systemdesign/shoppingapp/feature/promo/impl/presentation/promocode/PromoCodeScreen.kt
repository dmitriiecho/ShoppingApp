package krio.systemdesign.shoppingapp.feature.promo.impl.presentation.promocode

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import krio.systemdesign.shoppingapp.core.composeutils.effects.ObserveEffects
import krio.systemdesign.shoppingapp.core.composeutils.text.UiText
import krio.systemdesign.shoppingapp.core.composeutils.text.asString
import krio.systemdesign.shoppingapp.core.composeutils.viewmodel.injectedViewModel
import krio.systemdesign.shoppingapp.core.designsystem.components.buttons.LoadingButton
import krio.systemdesign.shoppingapp.core.designsystem.components.buttons.NavigateBackIconButton
import krio.systemdesign.shoppingapp.core.designsystem.components.inputs.AppOutlinedTextField
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.designsystem.theme.Spacing
import krio.systemdesign.shoppingapp.core.designsystem.theme.contentBarWidth
import krio.systemdesign.shoppingapp.core.designsystem.theme.contentWidth
import krio.systemdesign.shoppingapp.feature.promo.impl.R
import krio.systemdesign.shoppingapp.feature.promo.impl.presentation.promocode.components.AvailablePromoCodesHint
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode

@Composable
internal fun PromoCodeScreen(
    onBack: () -> Unit,
    onCloseWithResult: (PromoCode) -> Unit,
    viewModel: PromoCodeViewModel = injectedViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveEffects(viewModel.effects) { effect ->
        when (effect) {
            is PromoCodeEffect.CloseWithResult -> navigate { onCloseWithResult(effect.promoCode) }
            PromoCodeEffect.NavigateBack -> navigate { onBack() }
        }
    }

    PromoCodeScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PromoCodeScreen(
    uiState: PromoCodeUiState,
    onEvent: (PromoCodeEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.promo_code)) },
                modifier = Modifier.contentBarWidth(),
                navigationIcon = {
                    NavigateBackIconButton(onClick = { onEvent(PromoCodeEvent.OnBackClick) })
                },
            )
        },
    ) { innerPadding ->
        PromoCodeContent(
            uiState = uiState,
            onEvent = onEvent,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        )
    }
}

@Composable
private fun PromoCodeContent(
    uiState: PromoCodeUiState,
    onEvent: (PromoCodeEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Scrolls so the hint below the button stays reachable with the keyboard open.
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(Spacing.ScreenPadding)
            .contentWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.SectionSpacing),
    ) {
        AppOutlinedTextField(
            state = uiState.promoCode,
            label = stringResource(R.string.promo_code),
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isChecking,
            error = uiState.checkError?.asString(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                autoCorrectEnabled = false,
                imeAction = ImeAction.Done,
            ),
            onKeyboardAction = { onEvent(PromoCodeEvent.OnApplyClick) },
        )
        LoadingButton(
            text = stringResource(R.string.promo_apply),
            isLoading = uiState.isChecking,
            onClick = { onEvent(PromoCodeEvent.OnApplyClick) },
            enabled = uiState.canApply,
            modifier = Modifier.fillMaxWidth(),
        )
        AvailablePromoCodesHint(
            state = uiState.availableCodes,
            enabled = !uiState.isChecking,
            onCodeClick = { onEvent(PromoCodeEvent.OnAvailablePromoCodeClick(it)) },
            onRetry = { onEvent(PromoCodeEvent.OnRetryAvailableCodesClick) },
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PromoCodeScreenPreview() {
    ShoppingAppTheme {
        PromoCodeScreen(
            uiState = PromoCodeUiState(
                promoCode = TextFieldState("SALE10"),
                availableCodes = PromoCodeUiState.AvailableCodes.Loaded(
                    listOf(PromoCode("SALE10", 10), PromoCode("SALE25", 25)),
                ),
            ),
            onEvent = {},
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PromoCodeScreenNotFoundPreview() {
    ShoppingAppTheme {
        PromoCodeScreen(
            uiState = PromoCodeUiState(
                promoCode = TextFieldState("SALE99"),
                check = PromoCodeUiState.Check.Failed(UiText.Resource(R.string.promo_not_found)),
                availableCodes = PromoCodeUiState.AvailableCodes.Loaded(
                    listOf(PromoCode("SALE10", 10), PromoCode("SALE25", 25)),
                ),
            ),
            onEvent = {},
        )
    }
}
