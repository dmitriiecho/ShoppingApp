package krio.systemdesign.shoppingapp.uikit.samples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.delay

// A fake load for interactive samples: set it to true, and it turns back to false after SampleData.LOADING_MILLIS.
@Composable
fun rememberSampleLoading(): MutableState<Boolean> {
    val isLoading = rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(isLoading.value) {
        if (isLoading.value) {
            delay(SampleData.LOADING_MILLIS)
            isLoading.value = false
        }
    }
    return isLoading
}
