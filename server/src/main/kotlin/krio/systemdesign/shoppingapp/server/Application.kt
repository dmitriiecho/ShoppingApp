package krio.systemdesign.shoppingapp.server

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import krio.systemdesign.shoppingapp.server.data.ShopData
import krio.systemdesign.shoppingapp.server.routes.cartRoutes
import krio.systemdesign.shoppingapp.server.routes.productRoutes
import krio.systemdesign.shoppingapp.server.routes.promoCodeRoutes
import kotlin.io.path.Path

// Порт и папку с данными задаёт systemd-сервис (см. deploy.sh). Значения по умолчанию подходят для запуска из папки server/.
fun main() {
    val port = System.getenv("PORT")?.toInt() ?: DEFAULT_PORT
    val data = ShopData.load(Path(System.getenv("DATA_DIR") ?: DEFAULT_DATA_DIR))
    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        module(data)
    }.start(wait = true)
}

fun Application.module(data: ShopData) {
    install(ContentNegotiation) {
        // Те же настройки, что у Json в приложении (NetworkModule в core/network).
        json(
            Json {
                ignoreUnknownKeys = true
                explicitNulls = false
                encodeDefaults = true
            },
        )
    }
    install(CallLogging)
    routing {
        productRoutes(data.products)
        promoCodeRoutes(data.promoCodes)
        cartRoutes()
    }
}

private const val DEFAULT_PORT = 8080
private const val DEFAULT_DATA_DIR = "data"
