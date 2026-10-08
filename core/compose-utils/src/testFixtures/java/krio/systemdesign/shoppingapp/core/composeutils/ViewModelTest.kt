package krio.systemdesign.shoppingapp.core.composeutils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

// Runs a ViewModel test. viewModelScope runs on Dispatchers.Main, which a JVM test doesn't have: it is replaced by
// a test dispatcher that shares runTest's virtual clock and runs launched coroutines at once.
@OptIn(ExperimentalCoroutinesApi::class)
fun viewModelTest(block: suspend TestScope.() -> Unit): TestResult {
    Dispatchers.setMain(UnconfinedTestDispatcher())
    return try {
        runTest { block() }
    } finally {
        Dispatchers.resetMain()
    }
}
