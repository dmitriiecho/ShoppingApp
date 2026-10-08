package krio.systemdesign.shoppingapp.feature.promo.impl.presentation.promocode

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import java.io.IOException
import kotlin.test.Test
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.TestScope
import krio.systemdesign.shoppingapp.core.composeutils.keepCollecting
import krio.systemdesign.shoppingapp.core.composeutils.text.UiText
import krio.systemdesign.shoppingapp.core.composeutils.typeText
import krio.systemdesign.shoppingapp.core.composeutils.viewModelTest
import krio.systemdesign.shoppingapp.feature.promo.impl.R
import krio.systemdesign.shoppingapp.feature.promo.impl.analytics.PromoCodeRejectedAnalyticsEvent
import krio.systemdesign.shoppingapp.feature.promo.impl.domain.model.PromoCodeCheckResult
import krio.systemdesign.shoppingapp.feature.promo.impl.domain.repository.TestPromoCodeRepository
import krio.systemdesign.shoppingapp.feature.promo.impl.domain.usecase.CheckPromoCodeUseCase
import krio.systemdesign.shoppingapp.feature.promo.impl.domain.usecase.GetPromoCodesUseCase
import krio.systemdesign.shoppingapp.shared.analytics.TestAnalytics
import krio.systemdesign.shoppingapp.shared.analytics.sent
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode

class PromoCodeViewModelTest {

    private val promoCodeRepository = TestPromoCodeRepository()
    private val analytics = TestAnalytics()

    @Test
    fun `existing code closes the screen with the code`() = viewModelTest {
        promoCodeRepository.checkAnswer = { PromoCodeCheckResult.Valid(PromoCode("SALE10", 10)) }
        val viewModel = promoCodeViewModel()
        viewModel.uiState.value.promoCode.typeText("sale10")

        viewModel.effects.test {
            viewModel.onEvent(PromoCodeEvent.OnApplyClick)

            assertThat(awaitItem()).isEqualTo(PromoCodeEffect.CloseWithResult(PromoCode("SALE10", 10)))
        }
    }

    @Test
    fun `blank code is not checked`() = viewModelTest {
        val viewModel = promoCodeViewModel()
        viewModel.uiState.value.promoCode.typeText("  ")

        viewModel.onEvent(PromoCodeEvent.OnApplyClick)

        assertThat(promoCodeRepository.checkedCodes).isEmpty()
    }

    @Test
    fun `second apply during the check is ignored`() = viewModelTest {
        val answer = CompletableDeferred<PromoCodeCheckResult>()
        promoCodeRepository.checkAnswer = { answer.await() }
        val viewModel = promoCodeViewModel()
        viewModel.uiState.value.promoCode.typeText("SALE10")

        viewModel.onEvent(PromoCodeEvent.OnApplyClick)
        viewModel.onEvent(PromoCodeEvent.OnApplyClick)
        answer.complete(PromoCodeCheckResult.NotFound)

        assertThat(promoCodeRepository.checkedCodes).containsExactly("SALE10")
    }

    @Test
    fun `unknown code says it wasn't found`() = viewModelTest {
        val viewModel = promoCodeViewModel()
        viewModel.uiState.value.promoCode.typeText("SALE99")

        viewModel.onEvent(PromoCodeEvent.OnApplyClick)

        assertThat(viewModel.uiState.value.checkError).isEqualTo(UiText.Resource(R.string.promo_not_found))
    }

    @Test
    fun `unknown code is reported as typed`() = viewModelTest {
        val viewModel = promoCodeViewModel()
        viewModel.uiState.value.promoCode.typeText("SALE99")

        viewModel.onEvent(PromoCodeEvent.OnApplyClick)

        assertThat(analytics.sentEvents).containsExactly(PromoCodeRejectedAnalyticsEvent("SALE99").sent())
    }

    @Test
    fun `failed check says the code wasn't checked`() = viewModelTest {
        promoCodeRepository.checkAnswer = { PromoCodeCheckResult.Error(IOException("No network")) }
        val viewModel = promoCodeViewModel()
        viewModel.uiState.value.promoCode.typeText("SALE10")

        viewModel.onEvent(PromoCodeEvent.OnApplyClick)

        assertThat(viewModel.uiState.value.checkError).isEqualTo(UiText.Resource(R.string.promo_check_error))
    }

    @Test
    fun `failed check is not reported`() = viewModelTest {
        promoCodeRepository.checkAnswer = { PromoCodeCheckResult.Error(IOException("No network")) }
        val viewModel = promoCodeViewModel()
        viewModel.uiState.value.promoCode.typeText("SALE10")

        viewModel.onEvent(PromoCodeEvent.OnApplyClick)

        assertThat(analytics.sentEvents).isEmpty()
    }

    @Test
    fun `editing the code clears the error`() = viewModelTest {
        val viewModel = promoCodeViewModel()
        val field = viewModel.uiState.value.promoCode
        field.typeText("SALE99")
        viewModel.onEvent(PromoCodeEvent.OnApplyClick)

        field.typeText("SALE9")

        assertThat(viewModel.uiState.value.checkError).isNull()
    }

    // The field is locked while the code is checked, and the hint must not change it either.
    @Test
    fun `hint doesn't change the code during the check`() = viewModelTest {
        val answer = CompletableDeferred<PromoCodeCheckResult>()
        promoCodeRepository.checkAnswer = { answer.await() }
        val viewModel = promoCodeViewModel()
        val field = viewModel.uiState.value.promoCode
        field.typeText("SALE10")
        viewModel.onEvent(PromoCodeEvent.OnApplyClick)

        viewModel.onEvent(PromoCodeEvent.OnAvailablePromoCodeClick("SALE25"))

        assertThat(field.text.toString()).isEqualTo("SALE10")
    }

    @Test
    fun `available codes are shown once loaded`() = viewModelTest {
        promoCodeRepository.promoCodesAnswer = { Result.success(listOf(PromoCode("SALE10", 10))) }

        val viewModel = promoCodeViewModel()

        assertThat(viewModel.uiState.value.availableCodes)
            .isEqualTo(PromoCodeUiState.AvailableCodes.Loaded(listOf(PromoCode("SALE10", 10))))
    }

    @Test
    fun `codes that failed to load show an error`() = viewModelTest {
        promoCodeRepository.promoCodesAnswer = { Result.failure(IOException("No network")) }

        val viewModel = promoCodeViewModel()

        assertThat(viewModel.uiState.value.availableCodes).isEqualTo(PromoCodeUiState.AvailableCodes.Error)
    }

    @Test
    fun `retry loads the codes again`() = viewModelTest {
        promoCodeRepository.promoCodesAnswer = { Result.failure(IOException("No network")) }
        val viewModel = promoCodeViewModel()
        promoCodeRepository.promoCodesAnswer = { Result.success(listOf(PromoCode("SALE10", 10))) }

        viewModel.onEvent(PromoCodeEvent.OnRetryAvailableCodesClick)

        assertThat(viewModel.uiState.value.availableCodes)
            .isEqualTo(PromoCodeUiState.AvailableCodes.Loaded(listOf(PromoCode("SALE10", 10))))
    }

    private fun TestScope.promoCodeViewModel() = PromoCodeViewModel(
        checkPromoCode = CheckPromoCodeUseCase(promoCodeRepository),
        getPromoCodes = GetPromoCodesUseCase(promoCodeRepository),
        analytics = analytics.analytics,
        savedStateHandle = SavedStateHandle(),
    ).also { keepCollecting(it.uiState) }
}
