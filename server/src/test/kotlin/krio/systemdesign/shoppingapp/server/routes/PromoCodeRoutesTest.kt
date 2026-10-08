package krio.systemdesign.shoppingapp.server.routes

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlinx.serialization.json.decodeFromJsonElement
import krio.systemdesign.shoppingapp.server.apiSample
import krio.systemdesign.shoppingapp.server.dto.PromoCodeDTO
import krio.systemdesign.shoppingapp.server.serverJson
import krio.systemdesign.shoppingapp.server.serverTest
import krio.systemdesign.shoppingapp.server.testShopData

class PromoCodeRoutesTest {

    @Test
    fun `promo code in another case is found and comes back as stored`() = serverTest(
        testShopData(promoCodes = listOf(PromoCodeDTO("SALE10", 10))),
    ) { client ->
        val promoCode = client.get("/promo-codes/sale10").body<PromoCodeDTO>()

        assertThat(promoCode).isEqualTo(PromoCodeDTO("SALE10", 10))
    }

    @Test
    fun `promo code with spaces around it is found`() = serverTest(
        testShopData(promoCodes = listOf(PromoCodeDTO("SALE10", 10))),
    ) { client ->
        val promoCode = client.get("/promo-codes/%20SALE10%20").body<PromoCodeDTO>()

        assertThat(promoCode).isEqualTo(PromoCodeDTO("SALE10", 10))
    }

    @Test
    fun `unknown promo code is rejected with 404`() = serverTest(
        testShopData(promoCodes = listOf(PromoCodeDTO("SALE10", 10))),
    ) { client ->
        val response = client.get("/promo-codes/SALE99")

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `all promo codes come in file order`() = serverTest(
        testShopData(promoCodes = listOf(PromoCodeDTO("SALE25", 25), PromoCodeDTO("SALE10", 10))),
    ) { client ->
        val promoCodes = client.get("/promo-codes").body<List<PromoCodeDTO>>()

        assertThat(promoCodes).containsExactly(PromoCodeDTO("SALE25", 25), PromoCodeDTO("SALE10", 10))
    }

    @Test
    fun `promo code is written as in the API sample`() = serverTest(samplePromoCodes()) { client ->
        val response = client.get("/promo-codes/SALE10")

        assertThat(serverJson.parseToJsonElement(response.bodyAsText())).isEqualTo(apiSample("promo-code.json"))
    }

    @Test
    fun `promo codes are written as in the API sample`() = serverTest(samplePromoCodes()) { client ->
        val response = client.get("/promo-codes")

        assertThat(serverJson.parseToJsonElement(response.bodyAsText())).isEqualTo(apiSample("promo-codes.json"))
    }

    private fun samplePromoCodes() = testShopData(
        promoCodes = serverJson.decodeFromJsonElement<List<PromoCodeDTO>>(apiSample("promo-codes.json")),
    )
}
