plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "com.common.control"

    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    viewBinding {
        enable = true
    }
    dataBinding {
        enable = true
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation(fileTree("libs") { include("*.jar") })

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.material)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.cardview)
    implementation(libs.google.playservices.ads)

    implementation(libs.facebook.shimmer)
    implementation(libs.androidx.lifecycle.process)
//    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.github.androidspinkit)

    implementation(libs.androidx.multidex) // Still include multidex here

    implementation(libs.github.ratingbar)

    implementation(libs.google.gson)

    implementation(libs.android.billingclient)

//    implementation(libs.akexorcist.localization)

    implementation(libs.adjust.androidsdk)

    // Reference the BOM first
    // For platforms (BOMs), you typically use the platform() function
    implementation(platform(libs.google.firebase.bom))
    // Then reference individual Firebase libraries (no version needed)
    implementation(libs.firebase.crashlytics.google)
    implementation(libs.firebase.analytics.google)
    implementation(libs.firebase.config.google)
    implementation(libs.firebase.messaging.google)

    implementation(libs.facebook.audiencenetworksdk)
    implementation(libs.google.mediationtestsuite)

    //SDK solar engine
    implementation(libs.reyun.solarenginecore)

}