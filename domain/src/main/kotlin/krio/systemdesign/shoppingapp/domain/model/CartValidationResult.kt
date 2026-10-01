package krio.systemdesign.shoppingapp.domain.model

sealed interface CartValidationResult {
    data object Success : CartValidationResult
    data class Invalid(
        // Изменения в товарах корзины.
        val issues: List<ItemIssue>,
        // false — применённого промокода больше нет на сервере.
        val isPromoCodeValid: Boolean,
    ) : CartValidationResult
    data class Error(val error: Throwable) : CartValidationResult
}
