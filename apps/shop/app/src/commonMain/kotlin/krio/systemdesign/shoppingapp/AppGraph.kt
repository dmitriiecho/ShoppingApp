package krio.systemdesign.shoppingapp

import io.ktor.client.HttpClient
import krio.systemdesign.shoppingapp.core.composeutils.viewmodel.InjectedViewModelFactory

// What App() takes from the app's dependency graph. Each platform declares its own graph on top of it
// (AndroidAppGraph in :apps:shop:android): Metro builds a graph only in platform code, where it sees the platform's
// contributions too.
interface AppGraph {
    val viewModelFactory: InjectedViewModelFactory

    // For Coil's image loader (App).
    val httpClient: HttpClient

    val incomingDeepLinks: IncomingDeepLinks
}
