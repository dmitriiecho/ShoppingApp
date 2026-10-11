import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.testing.Test

// The JVM tests of Android and KMP modules (src/test, androidHostTest).
internal fun Test.configureHostTest() {
    // Robolectric reaches into JDK internals; on Java 17+ they have to be opened (robolectric.org).
    jvmArgs(ROBOLECTRIC_JVM_ARGS)
    // The JSON samples the app and the server agree on (apiSample and apiRequest in :core:network:testing).
    // Declared as an input, so an edited sample runs the tests again.
    inputs.dir(project.rootDir.resolve("server/api-samples")).withPathSensitivity(PathSensitivity.RELATIVE)
}

private val ROBOLECTRIC_JVM_ARGS = listOf(
    "--add-opens=java.base/java.lang=ALL-UNNAMED",
    "--add-opens=java.base/java.util=ALL-UNNAMED",
    "--add-opens=java.base/java.io=ALL-UNNAMED",
    "--add-opens=java.base/java.net=ALL-UNNAMED",
    "--add-opens=java.base/java.security=ALL-UNNAMED",
    "--add-opens=java.base/java.text=ALL-UNNAMED",
    "--add-opens=java.base/jdk.internal.access=ALL-UNNAMED",
    "--add-opens=java.desktop/java.awt.font=ALL-UNNAMED",
    "--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
)
