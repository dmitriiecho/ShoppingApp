package krio.systemdesign.shoppingapp.server.data

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import krio.systemdesign.shoppingapp.server.dto.ProductDTO
import krio.systemdesign.shoppingapp.server.dto.PromoCodeDTO
import java.nio.file.Path
import kotlin.io.path.readText

// Товары и промокоды из JSON-файлов в папке data/. Файлы читаются один раз при запуске,
// поэтому после их правки сервер нужно перезапустить.
class ShopData(
    val products: List<ProductDTO>,
    val promoCodes: List<PromoCodeDTO>,
) {
    init {
        products.groupBy { it.id }.forEach { (id, sameId) ->
            require(sameId.size == 1) { "Несколько товаров с id $id" }
        }
        // Коды проверяются без учёта регистра, поэтому SALE10 и sale10 — один и тот же код.
        promoCodes.groupBy { it.code.uppercase() }.forEach { (code, sameCode) ->
            require(sameCode.size == 1) { "Промокод $code указан несколько раз" }
        }
        promoCodes.forEach {
            // Другой процент приложение не примет: PromoCode в :domain проверяет тот же диапазон.
            require(it.discountPercent in 1..100) { "У промокода ${it.code} скидка не в диапазоне 1..100" }
        }
    }

    companion object {
        fun load(dir: Path): ShopData = ShopData(
            products = dir.resolve("products.json").readJson(),
            promoCodes = dir.resolve("promo-codes.json").readJson(),
        )
    }
}

// Файлы читаем строгим Json, без ignoreUnknownKeys: опечатка в названии поля остановит запуск,
// а не потеряет значение молча.
private inline fun <reified T> Path.readJson(): T =
    try {
        Json.decodeFromString(readText())
    } catch (e: SerializationException) {
        throw IllegalStateException("Не удалось разобрать $this: ${e.message}", e)
    }
