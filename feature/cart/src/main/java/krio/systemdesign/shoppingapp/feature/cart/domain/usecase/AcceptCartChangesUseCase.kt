package krio.systemdesign.shoppingapp.feature.cart.domain.usecase

import krio.systemdesign.shoppingapp.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.domain.repository.CartRepository
import javax.inject.Inject

class AcceptCartChangesUseCase @Inject constructor(
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(issues: List<ItemIssue>): Result<Unit> =
        cartRepository.acceptChanges(issues)
}
