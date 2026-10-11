package krio.systemdesign.shoppingapp.feature.promo.impl.data.api

import dev.zacsweers.metro.Inject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments
import krio.systemdesign.shoppingapp.feature.promo.impl.data.dto.PromoCodeDTO

@Inject
internal class PromoApi(private val client: HttpClient) {
    // The code is typed by the user, so it goes in as a path segment: a "/" or a space in it is escaped.
    suspend fun checkPromoCode(code: String): PromoCodeDTO =
        client.get { url { appendPathSegments("promo-codes", code) } }.body()

    // Every code on the server, for the hint on the promo code screen.
    suspend fun getPromoCodes(): List<PromoCodeDTO> = client.get("promo-codes").body()
}
