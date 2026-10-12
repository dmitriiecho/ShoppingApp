package krio.systemdesign.shoppingapp.navigation

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class DeepLinksTest {

    @Test
    fun `link without a trailing slash stays as it is`() {
        assertThat(normalizeDeepLink("https://example.com/shop/cart")).isEqualTo("https://example.com/shop/cart")
    }

    @Test
    fun `trailing slash goes`() {
        assertThat(
            normalizeDeepLink("https://example.com/shop/product/1/"),
        ).isEqualTo("https://example.com/shop/product/1")
    }

    @Test
    fun `slash before a query goes and the query stays`() {
        assertThat(normalizeDeepLink("https://example.com/shop/cart/?from=mail"))
            .isEqualTo("https://example.com/shop/cart?from=mail")
    }

    @Test
    fun `slash before a fragment goes and the fragment stays`() {
        assertThat(normalizeDeepLink("https://example.com/shop/catalog/#top"))
            .isEqualTo("https://example.com/shop/catalog#top")
    }

    @Test
    fun `slash inside a query stays`() {
        assertThat(normalizeDeepLink("https://example.com/shop/cart?back=/catalog/"))
            .isEqualTo("https://example.com/shop/cart?back=/catalog/")
    }
}
