package krio.systemdesign.shoppingapp

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

// The iOS app's entry point: the Swift side (apps/shop/ios) shows this controller full screen.
@Suppress("FunctionName", "unused")
fun MainViewController(): UIViewController = ComposeUIViewController { App() }
