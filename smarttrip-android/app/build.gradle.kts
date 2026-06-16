plugins {
  alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.smarttripvoyager"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.example.smarttripvoyager"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
        compose = false
    }
}

dependencies {
  // Core Android dependencies
  implementation(libs.androidx.core.ktx)
  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("com.google.android.material:material:1.11.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
  implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

  // Retrofit & Gson
  implementation("com.squareup.retrofit2:retrofit:2.11.0")
  implementation("com.squareup.retrofit2:converter-gson:2.11.0")
  implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

  // Glide for image loading
  implementation("com.github.bumptech.glide:glide:4.16.0")

  // Room Database (Local)
  implementation("androidx.room:room-runtime:2.6.1")
  annotationProcessor("androidx.room:room-compiler:2.6.1")

  // Local tests
  testImplementation(libs.junit)

  // Instrumented tests
  androidTestImplementation(libs.androidx.test.core)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.test.espresso.core)
}
