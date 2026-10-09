package krio.systemdesign.shoppingapp.server.data

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isFalse
import assertk.assertions.isNotEmpty
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.reflect.KClass
import kotlin.test.Test
import krio.systemdesign.shoppingapp.server.dto.CartItemDTO
import krio.systemdesign.shoppingapp.server.dto.CartValidationResponseDTO
import krio.systemdesign.shoppingapp.server.dto.ItemIssueDTO
import krio.systemdesign.shoppingapp.server.serverTest
import krio.systemdesign.shoppingapp.server.validateCart

// The real files in data/, the ones the deployed server reads.
class DataFilesTest {

    private val dataDir = Path("data")
    private val data = ShopData.load(dataDir)

    // Products whose imageUrl points to this server's /images/, not to an outside host.
    private val hostedImageProducts = data.products.filter { "/images/" in it.imageUrl }

    @Test
    fun `data files hold products and promo codes`() {
        assertThat(data.products).isNotEmpty()
        assertThat(data.promoCodes).isNotEmpty()
    }

    @Test
    fun `hosted image is named after its product`() {
        val misnamed = hostedImageProducts.filter {
            it.imageUrl.substringAfterLast('/').substringBefore('?') != "${it.id}.png"
        }

        assertThat(misnamed).isEmpty()
    }

    @Test
    fun `every hosted image has a file except the hub's`() {
        val missing = hostedImageProducts
            .filter { it.id != HUB_ID }
            .filterNot { dataDir.resolve("images/${it.id}.png").exists() }

        assertThat(missing).isEmpty()
    }

    // The app requests the missing file and shows its placeholder: the hub is how that case is tried out.
    @Test
    fun `hub has no image file`() {
        assertThat(dataDir.resolve("images/$HUB_ID.png").exists()).isFalse()
    }

    // The app's settings put these carts together to show one kind of change each (the Add…ProductToCartUseCase
    // classes in :feature:settings:impl, with the same ids, prices and quantities). The real data must keep
    // giving exactly those changes.

    @Test
    fun `settings' unavailable item is reported unavailable`() = serverTest(data) { client ->
        val issueTypes = client.issueTypes(CartItemDTO("16", price = 4499, quantity = 1))

        assertThat(issueTypes).containsExactly(ItemIssueDTO.Unavailable::class)
    }

    @Test
    fun `settings' not-enough-stock item is reported short of stock only`() = serverTest(data) { client ->
        val issueTypes = client.issueTypes(CartItemDTO("48", price = 4699, quantity = 10))

        assertThat(issueTypes).containsExactly(ItemIssueDTO.NotEnoughStock::class)
    }

    @Test
    fun `settings' price-changed item is reported with a new price only`() = serverTest(data) { client ->
        val issueTypes = client.issueTypes(CartItemDTO("40", price = 4900, quantity = 1))

        assertThat(issueTypes).containsExactly(ItemIssueDTO.PriceChanged::class)
    }

    @Test
    fun `settings' price-changed not-enough-stock item gets both changes`() = serverTest(data) { client ->
        val issueTypes = client.issueTypes(CartItemDTO("32", price = 995, quantity = 10))

        assertThat(issueTypes).containsExactly(ItemIssueDTO.PriceChanged::class, ItemIssueDTO.NotEnoughStock::class)
    }

    private suspend fun HttpClient.issueTypes(item: CartItemDTO): List<KClass<out ItemIssueDTO>> =
        validateCart(item).body<CartValidationResponseDTO>().issues.map { it::class }

    private companion object {
        const val HUB_ID = "3"
    }
}
