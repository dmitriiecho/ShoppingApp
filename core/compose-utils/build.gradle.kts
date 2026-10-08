plugins {
    alias(libs.plugins.shoppingapp.android.library)
    alias(libs.plugins.shoppingapp.android.compose)
}

android {
    namespace = "krio.systemdesign.shoppingapp.core.composeutils"
    testFixtures {
        enable = true
    }
}

dependencies {
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // viewModelTest: runTest with Dispatchers.Main, where viewModelScope runs, replaced by the test dispatcher.
    testFixturesApi(libs.kotlinx.coroutines.test)
    // typeText: types into a TextFieldState and applies the snapshot.
    testFixturesImplementation(platform(libs.androidx.compose.bom))
    testFixturesImplementation(libs.androidx.compose.runtime)
    testFixturesApi(libs.androidx.compose.foundation)
}
