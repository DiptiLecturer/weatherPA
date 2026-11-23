plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.weatherapp_pa"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }
    buildFeatures{
        viewBinding=true
        buildConfig = true
    }

    defaultConfig {
        buildConfigField(
            "String",
            "WEATHER_API_KEY",
            "\"bb3cfd7f4614ccccdbb22a0f4f73b35d\""
        )
        applicationId = "com.example.weatherapp_pa"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")

// Gson Converter
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

// Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // Lifecycle - ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.11.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.11.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.11.1")

    implementation("com.google.android.gms:play-services-location:21.4.0")
}