package krio.systemdesign.shoppingapp.shared.domain.model

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

// Temporary: fails on purpose to check that CI attaches the test report. Reverted right after.
class CiReportCheckTest {

    @Test
    fun `failing test makes CI attach the report`() {
        assertThat(1 + 1).isEqualTo(3)
    }
}
