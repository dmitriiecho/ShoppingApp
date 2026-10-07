package krio.systemdesign.shoppingapp.server.routes

import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import krio.systemdesign.shoppingapp.server.data.findPromoCode
import krio.systemdesign.shoppingapp.server.dto.CartItemDTO
import krio.systemdesign.shoppingapp.server.dto.CartValidationRequestDTO
import krio.systemdesign.shoppingapp.server.dto.CartValidationResponseDTO
import krio.systemdesign.shoppingapp.server.dto.ItemIssueDTO
import krio.systemdesign.shoppingapp.server.dto.ProductDTO
import krio.systemdesign.shoppingapp.server.dto.PromoCodeDTO

fun Route.cartRoutes(
    products: List<ProductDTO>,
    promoCodes: List<PromoCodeDTO>,
) {
    val productsById = products.associateBy { it.id }
    post("/cart/validate") {
        val request = call.receive<CartValidationRequestDTO>()
        val issues = request.items.flatMap { it.issues(productsById[it.productId]) }
        // A code's percent never changes, so it is enough to check the code still exists.
        val promoCodeValid = request.promoCode == null || promoCodes.findPromoCode(request.promoCode) != null
        call.respond(CartValidationResponseDTO(issues = issues, promoCodeValid = promoCodeValid))
    }
}

// An out-of-stock item can't be ordered at all, so its price and stock aren't reported.
// Otherwise price and stock are checked separately and one item can get both issues.
private fun CartItemDTO.issues(product: ProductDTO?): List<ItemIssueDTO> {
    // Products are never removed, so the app shouldn't send an unknown id.
    if (product == null || product.availableQuantity == 0) {
        return listOf(ItemIssueDTO.Unavailable(productId))
    }
    return buildList {
        if (price != product.price) {
            add(ItemIssueDTO.PriceChanged(productId, newPrice = product.price))
        }
        if (quantity > product.availableQuantity) {
            add(ItemIssueDTO.NotEnoughStock(productId, availableQuantity = product.availableQuantity))
        }
    }
}
