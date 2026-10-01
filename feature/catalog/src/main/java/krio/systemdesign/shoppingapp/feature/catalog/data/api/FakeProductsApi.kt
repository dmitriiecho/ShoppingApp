package krio.systemdesign.shoppingapp.feature.catalog.data.api

import kotlinx.coroutines.delay
import krio.systemdesign.shoppingapp.feature.catalog.data.dto.ProductDTO
import krio.systemdesign.shoppingapp.feature.catalog.data.dto.ProductsPageDTO
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.net.HttpURLConnection.HTTP_NOT_FOUND

// Сейчас не используется: приложение ходит на наш сервер (server/), товары оттуда — в server/data/products.json.
internal class FakeProductsApi : ProductsApi {

    override suspend fun getProducts(query: String, page: Int, pageSize: Int): Response<ProductsPageDTO> {
        delay(NETWORK_DELAY_MS)

        val filtered = if (query.isBlank()) {
            PRODUCTS
        } else {
            PRODUCTS.filter { it.name.contains(query, ignoreCase = true) }
        }

        if (page < FIRST_PAGE) {
            return Response.success(ProductsPageDTO(products = emptyList(), endReached = true))
        }

        val fromIndex = (page - FIRST_PAGE) * pageSize
        if (fromIndex >= filtered.size) {
            return Response.success(ProductsPageDTO(products = emptyList(), endReached = true))
        }

        val toIndex = minOf(fromIndex + pageSize, filtered.size)
        return Response.success(
            ProductsPageDTO(
                products = filtered.subList(fromIndex, toIndex),
                endReached = toIndex >= filtered.size,
            ),
        )
    }

    override suspend fun getProduct(id: String): Response<ProductDTO> {
        delay(NETWORK_DELAY_MS)
        // Как настоящий сервер: на неизвестный товар отвечаем 404.
        val product = PRODUCTS.find { it.id == id }
            ?: return Response.error(HTTP_NOT_FOUND, "".toResponseBody())
        return Response.success(product)
    }

    private companion object {
        const val NETWORK_DELAY_MS = 2000L
        const val FIRST_PAGE = 1

        val PRODUCTS = listOf(
            product(
                id = "1",
                name = "Wireless Headphones",
                price = 7999,
                description = "Беспроводные наушники с мягкими амбушюрами и стабильным Bluetooth-соединением до 30 часов.",
            ),
            product(
                id = "2",
                name = "Mechanical Keyboard",
                price = 12999,
                description = "Механическая клавиатура с тактильным откликом, подсветкой и раскладкой для работы и игр.",
            ),
            product(
                id = "3",
                name = "USB-C Hub",
                price = 3499,
                description = "Компактный хаб USB-C с HDMI, USB-A и слотом для карт памяти — один кабель вместо нескольких.",
                available = false,
            ),
            product(
                id = "4",
                name = "4K Monitor",
                price = 24999,
                description = "27-дюймовый монитор 4K с точной цветопередачей и тонкой рамкой для работы с графикой.",
            ),
            product(
                id = "5",
                name = "Laptop Stand",
                price = 4599,
                description = "Алюминиевая подставка поднимает экран ноутбука до уровня глаз и улучшает вентиляцию.",
            ),
            product(
                id = "6",
                name = "Bluetooth Speaker",
                price = 5999,
                description = "Портативная колонка с объёмным звуком, защитой от брызг и зарядкой на весь день.",
            ),
            product(
                id = "7",
                name = "Webcam HD",
                price = 6999,
                description = "Веб-камера Full HD с автофокусом и встроенным микрофоном для созвонов и стримов.",
            ),
            product(
                id = "8",
                name = "Noise Cancelling Earbuds",
                price = 8999,
                description = "Вкладыши с активным шумоподавлением, удобной посадкой и чехлом с быстрой зарядкой.",
            ),
            product(
                id = "9",
                name = "Cotton T-Shirt",
                price = 1999,
                description = "Мягкая хлопковая футболка повседневного кроя, держит форму после стирки.",
            ),
            product(
                id = "10",
                name = "Denim Jacket",
                price = 8999,
                description = "Классическая джинсовая куртка средней плотности — поверх футболки или худи.",
            ),
            product(
                id = "11",
                name = "Running Shoes",
                price = 7499,
                description = "Лёгкие кроссовки с амортизацией для ежедневных пробежек и ходьбы по городу.",
            ),
            product(
                id = "12",
                name = "Wool Sweater",
                price = 6499,
                description = "Тёплый шерстяной свитер свободного силуэта, не колется и хорошо держит тепло.",
            ),
            product(
                id = "13",
                name = "Leather Belt",
                price = 2999,
                description = "Ремень из натуральной кожи с металлической пряжкой, подходит к джинсам и брюкам.",
            ),
            product(
                id = "14",
                name = "Winter Scarf",
                price = 2499,
                description = "Плотный шарф из мягкой ткани, закрывает шею и не скользит под пальто.",
            ),
            product(
                id = "15",
                name = "Casual Sneakers",
                price = 5499,
                description = "Универсальные кеды с гибкой подошвой — на работу, учёбу и прогулки.",
            ),
            product(
                id = "16",
                name = "Hoodie",
                price = 3999,
                description = "Худи из плотного футера с капюшоном и карманом-кенгуру, не садится после стирки.",
                available = false,
            ),
            product(
                id = "17",
                name = "Ceramic Mug",
                price = 1299,
                description = "Керамическая кружка 350 мл с удобной ручкой, можно мыть в посудомойке.",
            ),
            product(
                id = "18",
                name = "Desk Lamp",
                price = 3799,
                description = "Настольная лампа с регулируемой яркостью и гибкой ножкой, не бликует на экране.",
            ),
            product(
                id = "19",
                name = "Throw Pillow",
                price = 1899,
                description = "Декоративная подушка с съёмным чехлом — на диван или кресло.",
            ),
            product(
                id = "20",
                name = "Wall Clock",
                price = 2599,
                description = "Тихие настенные часы с крупным циферблатом, ход без навязчивого тиканья.",
            ),
            product(
                id = "21",
                name = "Scented Candle",
                price = 999,
                description = "Ароматическая свеча с мягким устойчивым запахом примерно на 25 часов горения.",
            ),
            product(
                id = "22",
                name = "Kitchen Scale",
                price = 2299,
                description = "Кухонные весы с точностью до 1 г, стеклянной платформой и функцией тары.",
            ),
            product(
                id = "23",
                name = "Plant Pot",
                price = 1599,
                description = "Керамический горшок с дренажным отверстием и поддоном для комнатных растений.",
            ),
            product(
                id = "24",
                name = "Cutlery Set",
                price = 4299,
                description = "Набор столовых приборов из нержавеющей стали на 4 персоны, не темнеет со временем.",
            ),
        )

        fun product(
            id: String,
            name: String,
            price: Long,
            description: String,
            available: Boolean = true,
        ) = ProductDTO(
            id = id,
            name = name,
            price = price,
            imageUrl = "https://picsum.photos/seed/$id/400/400",
            description = description,
            available = available,
        )
    }
}
