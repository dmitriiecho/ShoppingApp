package krio.systemdesign.shoppingapp.server.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import krio.systemdesign.shoppingapp.server.dto.PromoCodeDTO

fun Route.promoCodeRoutes(promoCodes: List<PromoCodeDTO>) {
    // Код сравнивается без учёта регистра. В ответе код записан так, как в promo-codes.json: на "sale10" вернётся "SALE10".
    get("/promo-codes/{code}") {
        val promoCode = promoCodes.find { it.code.equals(call.pathParameters["code"], ignoreCase = true) }
        if (promoCode == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            call.respond(promoCode)
        }
    }
}
