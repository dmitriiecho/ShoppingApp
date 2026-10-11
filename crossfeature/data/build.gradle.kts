plugins {
    alias(libs.plugins.shoppingapp.android.library)
    alias(libs.plugins.shoppingapp.metro)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

android {
    namespace = "krio.systemdesign.shoppingapp.crossfeature.data"
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(project(":crossfeature:domain"))
    implementation(project(":core:network"))
    implementation(libs.shoppingapp.contract)

    // api: the app's graph is built in :apps:shop and sees the database and DataStore types of DataModule.
    api(libs.androidx.room.runtime)
    api(libs.androidx.datastore.preferences)
    implementation(libs.kermit)

    ksp(libs.androidx.room.compiler)

    testImplementation(project(":crossfeature:domain:testing"))
    testImplementation(project(":core:network:testing"))
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.assertk)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
}
