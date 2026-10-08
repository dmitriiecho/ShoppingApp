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
    // The module's Compose compiler plugin compiles the fixtures too and needs the runtime on their classpath.
    testFixturesCompileOnly(platform(libs.androidx.compose.bom))
    testFixturesCompileOnly(libs.androidx.compose.runtime)
}
