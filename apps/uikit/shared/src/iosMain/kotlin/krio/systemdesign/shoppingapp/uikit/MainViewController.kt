package krio.systemdesign.shoppingapp.uikit

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

// The iOS UI kit's entry point: the Swift side (apps/uikit/ios) shows this controller full screen.
@Suppress("FunctionName", "unused")
fun MainViewController(): UIViewController = ComposeUIViewController { App() }
