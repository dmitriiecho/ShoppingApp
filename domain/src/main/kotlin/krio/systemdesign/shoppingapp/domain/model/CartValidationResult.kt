package krio.systemdesign.shoppingapp.domain.model

sealed interface CartValidationResult {
    data object Success : CartValidationResult
    data class Invalid(val issues: List<ItemIssue>) : CartValidationResult
    data class Error(val error: Throwable) : CartValidationResult
}
