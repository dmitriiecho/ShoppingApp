plugins {
    alias(libs.plugins.shoppingapp.android.library)
    alias(libs.plugins.shoppingapp.metro)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "krio.systemdesign.shoppingapp.shared.data"
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(project(":shared:domain"))
    implementation(project(":core:network"))

    // api: the app's graph is built in :apps:shop and sees the database and DataStore types of DataModule.
    api(libs.androidx.room.runtime)
    api(libs.androidx.datastore.preferences)
    implementation(libs.kermit)

    ksp(libs.androidx.room.compiler)

    testImplementation(testFixtures(project(":shared:domain")))
    testImplementation(testFixtures(project(":core:network")))
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.assertk)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
}
