package krio.systemdesign.shoppingapp.server.data

import java.nio.file.Path
import kotlin.io.path.readText
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import krio.systemdesign.shoppingapp.server.dto.PromoCodeDTO

// Read once at startup: an edited data file takes effect after a redeploy.
class ShopData(
    val products: List<Product>,
    val promoCodes: List<PromoCodeDTO>,
) {
    init {
        products.groupBy { it.id }.forEach { (id, sameId) ->
            require(sameId.size == 1) { "Duplicate product id $id" }
        }
        products.forEach {
            require(it.availableQuantity >= 0) { "Product ${it.id} has a negative availableQuantity" }
        }
        // Codes are matched ignoring case, so SALE10 and sale10 are the same code.
        promoCodes.groupBy { it.code.uppercase() }.forEach { (code, sameCode) ->
            require(sameCode.size == 1) { "Duplicate promo code $code" }
        }
        promoCodes.forEach {
            // The app rejects any other percent: PromoCode in :shared:domain checks the same range.
            require(it.discountPercent in 1..100) { "Promo code ${it.code} has discountPercent outside 1..100" }
        }
    }

    companion object {
        fun load(dir: Path): ShopData = ShopData(
            products = dir.resolve("products.json").readJson(),
            promoCodes = dir.resolve("promo-codes.json").readJson(),
        )
    }
}

// Ignores case and surrounding spaces: a code pasted from the clipboard often comes with spaces.
fun List<PromoCodeDTO>.findPromoCode(code: String?): PromoCodeDTO? =
    find { it.code.equals(code?.trim(), ignoreCase = true) }

// Strict Json, without ignoreUnknownKeys: a misspelled field stops the startup instead of being lost silently.
private inline fun <reified T> Path.readJson(): T = try {
    Json.decodeFromString(readText())
} catch (e: SerializationException) {
    throw IllegalStateException("Cannot parse $this: ${e.message}", e)
}
