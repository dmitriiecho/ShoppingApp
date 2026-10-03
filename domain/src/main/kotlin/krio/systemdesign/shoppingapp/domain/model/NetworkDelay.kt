package krio.systemdesign.shoppingapp.domain.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

enum class NetworkDelay(val duration: Duration) {
    None(Duration.ZERO),
    TwoSeconds(2.seconds),
    FourSeconds(4.seconds),
}
