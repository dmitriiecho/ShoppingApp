package krio.systemdesign.shoppingapp.domain.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

// A delay before every server request, a testing tool: shows how screens behave while waiting.
enum class NetworkDelay(val duration: Duration) {
    None(Duration.ZERO),
    TwoSeconds(2.seconds),
    FourSeconds(4.seconds),
}
