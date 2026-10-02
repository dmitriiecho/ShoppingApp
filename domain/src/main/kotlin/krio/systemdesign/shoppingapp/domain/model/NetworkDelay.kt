package krio.systemdesign.shoppingapp.domain.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

// Задержка перед каждым запросом к серверу. Инструмент для тестирования: с ней видно, как экраны ведут себя,
// пока ждут ответа.
enum class NetworkDelay(val duration: Duration) {
    None(Duration.ZERO),
    TwoSeconds(2.seconds),
    FourSeconds(4.seconds),
}
