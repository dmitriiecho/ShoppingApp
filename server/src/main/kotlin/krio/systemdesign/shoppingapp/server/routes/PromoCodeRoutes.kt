package krio.systemdesign.shoppingapp.server.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import krio.systemdesign.shoppingapp.server.data.findPromoCode
import krio.systemdesign.shoppingapp.server.dto.PromoCodeDTO

fun Route.promoCodeRoutes(promoCodes: List<PromoCodeDTO>) {
    // In promo-codes.json order. The app is a test stand and shows them as a hint on the promo code screen.
    get("/promo-codes") {
        call.respond(promoCodes)
    }

    // The code comes back as written in promo-codes.json: "sale10" returns "SALE10".
    get("/promo-codes/{code}") {
        val promoCode = promoCodes.findPromoCode(call.pathParameters["code"])
        if (promoCode == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            call.respond(promoCode)
        }
    }
}
