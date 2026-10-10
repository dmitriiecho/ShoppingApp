package krio.systemdesign.shoppingapp.feature.promo.impl.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import kotlin.test.Test
import krio.systemdesign.shoppingapp.core.network.apiRequest
import krio.systemdesign.shoppingapp.core.network.apiSample
import krio.systemdesign.shoppingapp.core.network.networkTest
import krio.systemdesign.shoppingapp.feature.promo.impl.data.api.PromoApi
import krio.systemdesign.shoppingapp.feature.promo.impl.domain.model.PromoCodeCheckResult
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode

class PromoCodeRepositoryImplTest {

    @Test
    fun `code the server doesn't have is not found`() = networkTest(::PromoApi) { server, api ->
        server.enqueue(code = 404)

        val result = PromoCodeRepositoryImpl(api).checkPromoCode("SALE99")

        assertThat(result).isEqualTo(PromoCodeCheckResult.NotFound)
    }

    @Test
    fun `server error checking a code is an error`() = networkTest(::PromoApi) { server, api ->
        server.enqueue(code = 500)

        val result = PromoCodeRepositoryImpl(api).checkPromoCode("SALE10")

        assertThat(result).isInstanceOf<PromoCodeCheckResult.Error>()
    }

    // PromoCode rejects a percent outside 1..100; such an answer must not crash the app.
    @Test
    fun `code with a percent outside 1 to 100 is an error`() = networkTest(::PromoApi) { server, api ->
        server.enqueue(body = """{"code":"FREE","discountPercent":0}""")

        val result = PromoCodeRepositoryImpl(api).checkPromoCode("FREE")

        assertThat(result).isInstanceOf<PromoCodeCheckResult.Error>()
    }

    @Test
    fun `one code with a wrong percent fails the whole list`() = networkTest(::PromoApi) { server, api ->
        server.enqueue(body = """[{"code":"SALE10","discountPercent":10},{"code":"FREE","discountPercent":0}]""")

        val result = PromoCodeRepositoryImpl(api).getPromoCodes()

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `promo code is requested as in the API sample`() = networkTest(::PromoApi) { server, api ->
        server.enqueue(body = apiSample("promo-code.json").toString())

        PromoCodeRepositoryImpl(api).checkPromoCode("SALE10")

        val request = server.takeRequest()
        assertThat("${request.method} ${request.target}").isEqualTo(apiRequest("promo-code"))
    }

    @Test
    fun `promo code from the API sample is read`() = networkTest(::PromoApi) { server, api ->
        server.enqueue(body = apiSample("promo-code.json").toString())

        val result = PromoCodeRepositoryImpl(api).checkPromoCode("sale10")

        assertThat(result).isEqualTo(PromoCodeCheckResult.Valid(PromoCode("SALE10", 10)))
    }

    @Test
    fun `promo codes are requested as in the API sample`() = networkTest(::PromoApi) { server, api ->
        server.enqueue(body = apiSample("promo-codes.json").toString())

        PromoCodeRepositoryImpl(api).getPromoCodes()

        val request = server.takeRequest()
        assertThat("${request.method} ${request.target}").isEqualTo(apiRequest("promo-codes"))
    }

    @Test
    fun `promo codes from the API sample are read`() = networkTest(::PromoApi) { server, api ->
        server.enqueue(body = apiSample("promo-codes.json").toString())

        val result = PromoCodeRepositoryImpl(api).getPromoCodes()

        assertThat(result).isEqualTo(Result.success(listOf(PromoCode("SALE10", 10), PromoCode("SALE25", 25))))
    }
}
