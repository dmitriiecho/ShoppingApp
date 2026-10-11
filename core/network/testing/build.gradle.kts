// networkTest, TestServer and the API samples, which the tests of other modules use too.
// A separate module: Kotlin Multiplatform has no test fixtures.
plugins {
    alias(libs.plugins.shoppingapp.kmp.library)
}

// Tests on iOS have no system properties, so the folder of the samples the app and the server agree on
// (server/api-samples) is compiled in as a constant. The tests read it on the JVM and on the iOS simulator alike.
val generateApiSamplesDir = tasks.register("generateApiSamplesDir") {
    val dir = rootDir.resolve("server/api-samples").absolutePath
    val output = layout.buildDirectory.dir("generated/apiSamplesDir")
    inputs.property("dir", dir)
    outputs.dir(output)
    doLast {
        val file = output.get().file("krio/systemdesign/shoppingapp/core/network/ApiSamplesDir.kt").asFile
        file.parentFile.mkdirs()
        file.writeText(
            "package krio.systemdesign.shoppingapp.core.network\n\ninternal const val API_SAMPLES_DIR = \"$dir\"\n",
        )
    }
}

kotlin {
    sourceSets {
        commonMain {
            kotlin.srcDir(generateApiSamplesDir)
            dependencies {
                api(project(":core:network"))
                api(libs.ktor.client.mock)
                api(libs.kotlinx.coroutines.test)
            }
        }
    }
}
