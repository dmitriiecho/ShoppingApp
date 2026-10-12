package krio.systemdesign.shoppingapp.uikit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat

internal class UiKitActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Not enableEdgeToEdge() from androidx.activity: on a configuration change it reapplies the styles
        // of its first call, not the last one.
        WindowCompat.enableEdgeToEdge(window)
        setContent { App() }
    }
}
