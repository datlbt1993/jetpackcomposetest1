plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.jecpackcomposeno1"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.jecpackcomposeno1"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

// AGP 9 dùng built-in Kotlin, không chạy được plugin `kotlin-parcelize` qua
// KotlinCompilerPluginSupportPlugin. Nạp compiler plugin trực tiếp bằng -Xplugin.
val parcelizeCompiler: Configuration by configurations.creating {
    isTransitive = false // -Xplugin nhận đúng 1 jar; deps đã có sẵn trên compiler classpath
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add(provider { "-Xplugin=${parcelizeCompiler.singleFile.absolutePath}" })
    }
}

dependencies {
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.games.activity)
    parcelizeCompiler("org.jetbrains.kotlin:kotlin-parcelize-compiler:${libs.versions.kotlin.get()}")
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.kotlin.parcelize.runtime)
    implementation(libs.accompanist.permissions)
    implementation(libs.coil.compose)
    implementation(libs.coil.video)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui.compose)
    implementation(libs.media3.ui.compose.material3)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}