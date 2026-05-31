import java.util.Properties

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.hilt)
}

android {
  namespace = "com.example.data"
  compileSdk = 35

  defaultConfig {
    minSdk = 26

    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
      localProperties.load(localPropertiesFile.inputStream())
    }
    val yandexClientId = localProperties.getProperty("YANDEX_CLIENT_ID") ?: ""
    buildConfigField("String", "YANDEX_CLIENT_ID", "\"$yandexClientId\"")
  }

  buildFeatures {
    buildConfig = true
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}

dependencies {
  implementation(project(":domain"))
  implementation(project(":core"))

  implementation(libs.androidx.core.ktx)
  implementation(libs.kotlinx.coroutines.core)
  
  // AppMetrica
  implementation(libs.appmetrica.sdk)
  
  // Firebase
  implementation(platform(libs.firebase.bom))
  implementation(libs.firebase.messaging)
  implementation(libs.firebase.firestore)
  implementation(libs.firebase.crashlytics)
  implementation(libs.firebase.config)
  implementation(libs.firebase.analytics)
  
  // TensorFlow Lite
  implementation(libs.tensorflow.lite)
  implementation(libs.tensorflow.lite.support)
  implementation(libs.tensorflow.lite.metadata)

  // Auth SDKs
  implementation(libs.yandex.auth.sdk)
  implementation(libs.vk.sdk.core)
  implementation(libs.vk.sdk.api)
  
  // Security
  implementation(libs.androidx.security.crypto)

  // Room
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.room.ktx)
  "ksp"(libs.androidx.room.compiler)

  // WorkManager
  implementation(libs.androidx.work.runtime.ktx)
  implementation(libs.androidx.hilt.work)

  // Hilt
  implementation(libs.hilt.android)
  "ksp"(libs.hilt.compiler)
  
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
}
