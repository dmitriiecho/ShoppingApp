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
    post("/cart/validate") {
        val request = call.receive<CartValidationRequestDTO>()
        val productsById = products.associateBy { it.id }
        val issues = request.items.flatMap { it.issues(productsById[it.productId]) }
        // Процент у созданного промокода не меняется, поэтому достаточно проверить, что код ещё есть.
        val promoCodeValid = request.promoCode == null || promoCodes.findPromoCode(request.promoCode) != null
        call.respond(CartValidationResponseDTO(issues = issues, promoCodeValid = promoCodeValid))
    }
}

// Чем позиция корзины расходится с каталогом. Закончившийся товар заказать нельзя совсем,
// поэтому про его цену и остаток не сообщаем. Иначе цена и остаток проверяются отдельно
// и для одного товара могут прийти обе проблемы.
private fun CartItemDTO.issues(product: ProductDTO?): List<ItemIssueDTO> {
    // Товары из каталога не удаляются, так что неизвестный id приложение прислать не должно.
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
