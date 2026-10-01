package krio.systemdesign.shoppingapp.server.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import krio.systemdesign.shoppingapp.server.data.findPromoCode
import krio.systemdesign.shoppingapp.server.dto.PromoCodeDTO

fun Route.promoCodeRoutes(promoCodes: List<PromoCodeDTO>) {
    // В ответе код записан так, как в promo-codes.json: на "sale10" вернётся "SALE10".
    get("/promo-codes/{code}") {
        val promoCode = promoCodes.findPromoCode(call.pathParameters["code"])
        if (promoCode == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            call.respond(promoCode)
        }
    }
}
