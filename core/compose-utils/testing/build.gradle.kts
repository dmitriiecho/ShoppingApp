// viewModelTest, typeText and the screenshot tests' tester, which the tests of other modules use too.
// A separate module: Kotlin Multiplatform has no test fixtures.
plugins {
    alias(libs.plugins.shoppingapp.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // viewModelTest: runTest with Dispatchers.Main, where viewModelScope runs, replaced by the test dispatcher.
            api(libs.kotlinx.coroutines.test)
            // typeText: types into a TextFieldState and applies the snapshot.
            implementation(libs.jetbrains.compose.runtime)
            api(libs.jetbrains.compose.foundation)
        }
        // PausedClockPreviewTester for the screenshot tests, which run only on Android (Robolectric).
        androidMain.dependencies {
            implementation(libs.roborazzi.compose.preview.scanner.support)
            implementation(project.dependencies.platform(libs.androidx.compose.bom))
            implementation(libs.androidx.compose.ui.test.junit4)
        }
    }
}
