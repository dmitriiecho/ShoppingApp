package krio.systemdesign.shoppingapp.server

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticFiles
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import java.io.File
import java.nio.file.Path as NioPath
import kotlin.io.path.Path
import kotlinx.serialization.json.Json
import krio.systemdesign.shoppingapp.server.data.ShopData
import krio.systemdesign.shoppingapp.server.routes.cartRoutes
import krio.systemdesign.shoppingapp.server.routes.productRoutes
import krio.systemdesign.shoppingapp.server.routes.promoCodeRoutes

// Порт и папку с данными задаёт systemd-сервис (см. deploy.sh). Значения по умолчанию подходят для запуска из папки server/.
fun main() {
    val port = System.getenv("PORT")?.toInt() ?: DEFAULT_PORT
    val dataDir = Path(System.getenv("DATA_DIR") ?: DEFAULT_DATA_DIR)
    val data = ShopData.load(dataDir)
    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        module(data, dataDir.resolve("images"))
    }.start(wait = true)
}

fun Application.module(
    data: ShopData,
    imagesDir: NioPath,
) {
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
        // Картинки из data/images/. В imageUrl у товара полный адрес этого же сервера.
        staticFiles("/images", File(imagesDir.toString()), index = null)
        productRoutes(data.products)
        promoCodeRoutes(data.promoCodes)
        cartRoutes(data.products, data.promoCodes)
    }
}

private const val DEFAULT_PORT = 8080
private const val DEFAULT_DATA_DIR = "data"
