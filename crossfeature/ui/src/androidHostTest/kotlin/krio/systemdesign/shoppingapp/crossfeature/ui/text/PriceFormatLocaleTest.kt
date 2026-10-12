package krio.systemdesign.shoppingapp.crossfeature.ui.text

import assertk.assertThat
import assertk.assertions.isEqualTo
import java.util.Locale
import kotlin.test.AfterTest
import kotlin.test.Test

// Every price is in US dollars, whatever language the phone is set to. Only on the JVM: common code can't change
// the default locale.
class PriceFormatLocaleTest {

    private val deviceLocale = Locale.getDefault()

    @AfterTest
    fun restoreLocale() {
        Locale.setDefault(deviceLocale)
    }

    @Test
    fun `price is shown in US format on a Russian phone`() {
        Locale.setDefault(Locale.forLanguageTag("ru-RU"))

        assertThat(formatPrice(123456)).isEqualTo("$1,234.56")
    }
}
