package krio.systemdesign.shoppingapp.server.routes

import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import krio.systemdesign.shoppingapp.server.dto.CartValidationRequestDTO
import krio.systemdesign.shoppingapp.server.dto.CartValidationResponseDTO

fun Route.cartRoutes() {
    // Пока любая корзина считается правильной. Тело всё равно разбираем,
    // чтобы на запрос не того формата ответить 400, а не молча согласиться.
    post("/cart/validate") {
        call.receive<CartValidationRequestDTO>()
        call.respond(CartValidationResponseDTO(issues = emptyList()))
    }
}
