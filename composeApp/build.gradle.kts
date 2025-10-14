
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.googleServices) // Added Google Services plugin
    alias(libs.plugins.firebase.crashlytics.gradle) // Added Crashlytics plugin
    alias(libs.plugins.buildkonfig)
}

kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.koin.android)
            implementation(libs.androidx.datastore.preferences)
            implementation(project(":CommonLib"))
            implementation(libs.google.playservices.ads)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.datastore.preferences.core)
            //third party webview
            implementation(libs.compose.webview.multiplatform)

            // Firebase SDKs
//            implementation(project.dependencies.platform(libs.firebase.bom)) // BOM for Firebase Kotlin SDK
            implementation(libs.firebase.analytics)
            implementation(libs.firebase.crashlytics)
            implementation(libs.firebase.config)
            implementation(libs.firebase.messaging)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.sqldelight.sqlite.driver)
        }

        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
            // For coroutine extensions (optional but common)
            implementation(libs.sqldelight.coroutines.extensions.native)
        }
    }
}

android {
    namespace = "com.ruicomp.cmptemplate"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.ruicomp.cmptemplate"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        manifestPlaceholders["ADMOB_APP_ID"] = "ca-app-pub-3940256099942544~3347511713"

        val formattedDate = SimpleDateFormat("MMM.dd.yyyy.hh.mm.ss", Locale.getDefault()).format(Date())
        base.archivesName = "${namespace}_v${versionCode}_V${versionName}_${formattedDate}"
    }

    signingConfigs {
        create("release") {
            storeFile = file("<folder>/filename.jks") //ex: signkey/release.jks
            storePassword = "strongpassword"
            keyAlias = "release"
            keyPassword = "strongpassword"
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21

        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs) // Added this line
    debugImplementation(compose.uiTooling)
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.ruicomp.cmptemplate.database")
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.ruicomp.cmptemplate.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.ruicomp.cmptemplate"
            packageVersion = "1.0.0"
        }
    }
}

buildkonfig {
    packageName = "com.ruicomp.cmptemplate"
    defaultConfigs {

        val addConstant: (constantName: String, constantValue: String) -> Unit = { constantName, constantValue ->
            buildConfigField(Type.STRING, constantName, constantValue)
        }

        addConstant("banner_home_high", "ca-app-pub-3940256099942544/6300978111")
        addConstant("banner_home", "ca-app-pub-3940256099942544/6300978111")
        addConstant("native_home_high", "ca-app-pub-3940256099942544/2247696110")
        addConstant("native_home", "ca-app-pub-3940256099942544/2247696110")
        addConstant("native_history_high", "ca-app-pub-3940256099942544/2247696110")
        addConstant("native_history", "ca-app-pub-3940256099942544/2247696110")
        addConstant("native_setting_high", "ca-app-pub-3940256099942544/2247696110")
        addConstant("native_setting", "ca-app-pub-3940256099942544/2247696110")
        addConstant("banner_schedule_high", "ca-app-pub-3940256099942544/6300978111")
        addConstant("banner_schedule", "ca-app-pub-3940256099942544/6300978111")
        addConstant("banner_saved_caller_high", "ca-app-pub-3940256099942544/6300978111")
        addConstant("banner_saved_caller", "ca-app-pub-3940256099942544/6300978111")
        addConstant("native_language_high", "ca-app-pub-3940256099942544/2247696110")
        addConstant("native_language", "ca-app-pub-3940256099942544/2247696110")
        addConstant("interstitial_home_high", "ca-app-pub-3940256099942544/1033173712")
        addConstant("interstitial_home", "ca-app-pub-3940256099942544/1033173712")
        addConstant("open_ad_id", "ca-app-pub-3940256099942544/9257395921")
    }

    targetConfigs {
        create("android") {}
        create("ios") {
            val addConstant: (constantName: String, constantValue: String) -> Unit = { constantName, constantValue ->
                buildConfigField(Type.STRING, constantName, "\"$constantValue\"")
            }

            addConstant("banner_home_high", "ca-app-pub-3940256099942544/2934735716")
            addConstant("banner_home", "ca-app-pub-3940256099942544/2934735716")
            addConstant("native_home_high", "ca-app-pub-3940256099942544/3986624511")
            addConstant("native_home", "ca-app-pub-3940256099942544/3986624511")
            addConstant("native_history_high", "ca-app-pub-3940256099942544/3986624511")
            addConstant("native_history", "ca-app-pub-3940256099942544/3986624511")
            addConstant("native_setting_high", "ca-app-pub-3940256099942544/3986624511")
            addConstant("native_setting", "ca-app-pub-3940256099942544/3986624511")
            addConstant("banner_schedule_high", "ca-app-pub-3940256099942544/2934735716")
            addConstant("banner_schedule", "ca-app-pub-3940256099942544/2934735716")
            addConstant("banner_saved_caller_high", "ca-app-pub-3940256099942544/2934735716")
            addConstant("banner_saved_caller", "ca-app-pub-3940256099942544/2934735716")
            addConstant("native_language_high", "ca-app-pub-3940256099942544/3986624511")
            addConstant("native_language", "ca-app-pub-3940256099942544/3986624511")
            addConstant("interstitial_home_high", "ca-app-pub-3940256099942544/4411468910")
            addConstant("interstitial_home", "ca-app-pub-3940256099942544/4411468910")
            addConstant("open_ad_id", "ca-app-pub-3940256099942544/5575463023")
        }
    }
}
