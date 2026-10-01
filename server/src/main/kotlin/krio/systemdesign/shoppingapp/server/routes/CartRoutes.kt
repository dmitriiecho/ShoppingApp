package krio.systemdesign.shoppingapp.server.routes

import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import krio.systemdesign.shoppingapp.server.data.findPromoCode
import krio.systemdesign.shoppingapp.server.dto.CartValidationRequestDTO
import krio.systemdesign.shoppingapp.server.dto.CartValidationResponseDTO
import krio.systemdesign.shoppingapp.server.dto.PromoCodeDTO

fun Route.cartRoutes(promoCodes: List<PromoCodeDTO>) {
    post("/cart/validate") {
        val request = call.receive<CartValidationRequestDTO>()
        // Процент у созданного промокода не меняется, поэтому достаточно проверить, что код ещё есть.
        val promoCodeValid = request.promoCode == null || promoCodes.findPromoCode(request.promoCode) != null
        // Товары пока не проверяются: любая позиция считается правильной.
        call.respond(CartValidationResponseDTO(issues = emptyList(), promoCodeValid = promoCodeValid))
    }
}
