package krio.systemdesign.shoppingapp.server.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import krio.systemdesign.shoppingapp.server.dto.ProductDTO
import krio.systemdesign.shoppingapp.server.dto.ProductsPageDTO

// Страницы отдаются по номеру, как договорились с приложением (см. ProductApi в feature/catalog):
// товары не удаляются, а новые получают id больше существующих и дописываются в конец products.json.
// Порядок выдачи — порядок товаров в файле.
fun Route.productRoutes(products: List<ProductDTO>) {
    get("/products") {
        // Пробелы по краям не часть запроса: " mug" ищет то же, что "mug".
        val query = call.queryParameters["query"].orEmpty().trim()
        val page = call.queryParameters["page"]?.toIntOrNull()
        val pageSize = call.queryParameters["pageSize"]?.toIntOrNull()
        if (page == null || page < FIRST_PAGE || pageSize == null || pageSize !in 1..MAX_PAGE_SIZE) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }

        val found = if (query.isBlank()) {
            products
        } else {
            products.filter { it.name.contains(query, ignoreCase = true) }
        }
        val pages = found.chunked(pageSize)
        val index = page - FIRST_PAGE
        call.respond(
            ProductsPageDTO(
                products = pages.getOrNull(index).orEmpty(),
                endReached = index >= pages.lastIndex,
            ),
        )
    }

    get("/products/{id}") {
        val product = products.find { it.id == call.pathParameters["id"] }
        if (product == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            call.respond(product)
        }
    }
}

private const val FIRST_PAGE = 1
private const val MAX_PAGE_SIZE = 100
