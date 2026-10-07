package krio.systemdesign.shoppingapp.shared.domain.model

sealed interface CartValidationResult {
    data object Success : CartValidationResult

    data class Invalid(
        val issues: List<ItemIssue>,
        val isPromoCodeValid: Boolean,
    ) : CartValidationResult

    data class Error(val error: Throwable) : CartValidationResult
}
