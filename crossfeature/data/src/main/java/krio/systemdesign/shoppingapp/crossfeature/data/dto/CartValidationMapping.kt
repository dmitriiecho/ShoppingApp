package krio.systemdesign.shoppingapp.crossfeature.data.dto

import krio.systemdesign.shoppingapp.contract.CartItemDTO
import krio.systemdesign.shoppingapp.contract.CartValidationRequestDTO
import krio.systemdesign.shoppingapp.contract.ItemIssueDTO
import krio.systemdesign.shoppingapp.crossfeature.domain.model.Cart
import krio.systemdesign.shoppingapp.crossfeature.domain.model.ItemIssue

internal fun Cart.toValidationRequest(): CartValidationRequestDTO = CartValidationRequestDTO(
    items = items.map { CartItemDTO(productId = it.productId, price = it.price, quantity = it.quantity) },
    promoCode = promoCode?.code,
)

internal fun ItemIssueDTO.toDomain(): ItemIssue = when (this) {
    is ItemIssueDTO.Unavailable -> ItemIssue.Unavailable(productId)
    is ItemIssueDTO.PriceChanged -> ItemIssue.PriceChanged(productId, newPrice)
    is ItemIssueDTO.NotEnoughStock -> ItemIssue.NotEnoughStock(productId, availableQuantity)
}
