plugins {
    alias(libs.plugins.shoppingapp.kmp.library)
    alias(libs.plugins.shoppingapp.metro)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

room {
    schemaDirectory("$projectDir/schemas")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":crossfeature:domain"))
            implementation(project(":core:network"))
            implementation(libs.shoppingapp.contract)

            // api: the app's graph is built in :apps:shop and sees the database and DataStore types of DataModule.
            api(libs.androidx.room.runtime)
            api(libs.androidx.datastore.preferences.core)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.kermit)
        }
        commonTest.dependencies {
            implementation(project(":crossfeature:domain:testing"))
            implementation(project(":core:network:testing"))
            implementation(libs.kotlin.test)
            implementation(libs.assertk)
        }
        // The database and DataStore tests: Room needs an Android Context, so they run on Robolectric.
        getByName("androidHostTest").dependencies {
            implementation(libs.kotlin.test.junit)
            implementation(libs.robolectric)
            implementation(libs.androidx.test.core)
        }
    }
}

// Room's code generator, for each platform.
dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}
