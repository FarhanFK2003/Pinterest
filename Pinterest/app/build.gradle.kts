plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.pinterest"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pinterest"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.material3.android)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("androidx.compose.material:material-icons-extended:1.7.4")

    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-analytics")

    implementation("androidx.room:room-runtime:2.5.0")
    //noinspection KaptUsageInsteadOfKsp
    kapt("androidx.room:room-compiler:2.5.0") // Use kapt for annotation processing
    implementation("androidx.room:room-ktx:2.5.0") // Optional: for coroutine support

    implementation("com.google.firebase:firebase-firestore-ktx:24.5.0")
    implementation("com.google.firebase:firebase-storage-ktx:20.5.0")
    implementation(libs.google.firebase.database)
    implementation(libs.coil.compose)
    implementation("com.google.code.gson:gson:2.8.9")

}