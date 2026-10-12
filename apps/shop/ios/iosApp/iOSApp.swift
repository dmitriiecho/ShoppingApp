import SwiftUI
import ShopApp

@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
                // A shoppingapp:// link (CFBundleURLTypes in Info.plist), at a cold start too.
                .onOpenURL { url in
                    MainViewControllerKt.openDeepLink(link: url.absoluteString)
                }
        }
    }
}
