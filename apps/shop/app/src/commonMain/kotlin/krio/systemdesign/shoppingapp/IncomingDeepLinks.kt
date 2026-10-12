package krio.systemdesign.shoppingapp

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

// Links the app is asked to open, from the platform (an Android Intent, onOpenURL on iOS). A link that comes before the navigation
// is ready, as at a cold start, waits in the channel until AppNavHost takes it.
@SingleIn(AppScope::class)
@Inject
class IncomingDeepLinks {
    private val channel = Channel<String>(Channel.BUFFERED)

    val links: Flow<String> = channel.receiveAsFlow()

    fun open(link: String) {
        channel.trySend(link)
    }
}
