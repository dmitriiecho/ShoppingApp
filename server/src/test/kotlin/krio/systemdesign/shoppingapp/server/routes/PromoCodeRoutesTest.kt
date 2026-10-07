package krio.systemdesign.shoppingapp.server.routes

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import krio.systemdesign.shoppingapp.server.TEST_DATA
import krio.systemdesign.shoppingapp.server.dto.CartValidationResponseDTO
import krio.systemdesign.shoppingapp.server.dto.PromoCodeDTO
import krio.systemdesign.shoppingapp.server.serverTest
import krio.systemdesign.shoppingapp.server.validateCart

class PromoCodeRoutesTest {

    @Test
    fun `promo code ignores case`() = serverTest { client ->
        assertEquals(PromoCodeDTO("SALE10", 10), client.get("/promo-codes/SALE10").body<PromoCodeDTO>())
        assertEquals(PromoCodeDTO("SALE10", 10), client.get("/promo-codes/sale10").body<PromoCodeDTO>())
        assertEquals(HttpStatusCode.NotFound, client.get("/promo-codes/SALE11").status)
    }

    @Test
    fun `promo code ignores surrounding spaces`() = serverTest { client ->
        assertEquals(PromoCodeDTO("SALE10", 10), client.get("/promo-codes/%20sale10%20").body<PromoCodeDTO>())
        assertTrue(client.validateCart(promoCode = " SALE10 ").body<CartValidationResponseDTO>().promoCodeValid)
    }

    @Test
    fun `all promo codes as a list`() = serverTest { client ->
        assertEquals(TEST_DATA.promoCodes, client.get("/promo-codes").body<List<PromoCodeDTO>>())
    }
}
