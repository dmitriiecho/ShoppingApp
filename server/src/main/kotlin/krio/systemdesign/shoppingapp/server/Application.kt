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
import java.nio.file.Path
import kotlin.io.path.Path
import krio.systemdesign.shoppingapp.server.data.ShopData
import krio.systemdesign.shoppingapp.server.routes.cartRoutes
import krio.systemdesign.shoppingapp.server.routes.productRoutes
import krio.systemdesign.shoppingapp.server.routes.promoCodeRoutes

// The systemd service sets PORT, DATA_DIR and PUBLIC_URL (see deploy.sh); the defaults fit a run from server/.
fun main() {
    val port = System.getenv("PORT")?.toInt() ?: DEFAULT_PORT
    val dataDir = Path(System.getenv("DATA_DIR") ?: DEFAULT_DATA_DIR)
    // The address clients reach the server at: product image addresses start with it.
    val publicUrl = System.getenv("PUBLIC_URL") ?: "http://localhost:$port"
    val data = ShopData.load(dataDir)
    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        module(data, dataDir.resolve("images"), publicUrl)
    }.start(wait = true)
}

fun Application.module(
    data: ShopData,
    imagesDir: Path,
    publicUrl: String,
) {
    install(ContentNegotiation) {
        json(serverJson)
    }
    install(CallLogging)
    routing {
        // A product's imageUrl is the full address of a file here.
        staticFiles(IMAGES_PATH, imagesDir.toFile(), index = null)
        productRoutes(data.products, imagesUrl = "$publicUrl$IMAGES_PATH")
        promoCodeRoutes(data.promoCodes)
        cartRoutes(data.products, data.promoCodes)
    }
}

private const val DEFAULT_PORT = 8080
private const val DEFAULT_DATA_DIR = "data"
private const val IMAGES_PATH = "/images"
