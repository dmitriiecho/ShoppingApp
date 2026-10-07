package krio.systemdesign.shoppingapp.feature.cart.impl.domain.usecase

import javax.inject.Inject
import krio.systemdesign.shoppingapp.shared.domain.model.ItemIssue
import krio.systemdesign.shoppingapp.shared.domain.repository.CartRepository

class AcceptCartChangesUseCase @Inject constructor(private val cartRepository: CartRepository) {
    // Removes unavailable items and takes the new prices. Not-enough-stock issues are skipped:
    // the user lowers each item's quantity themselves.
    suspend operator fun invoke(issues: List<ItemIssue>): Result<Unit> = cartRepository.acceptChanges(issues)
}
