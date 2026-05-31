import java.util.Properties

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.hilt)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
  alias(libs.plugins.google.services)
  alias(libs.plugins.firebase.crashlytics)
}

android {
  namespace = "com.example"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.aistudio.habitflow.zxpwt"
    minSdk = 26
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
      localProperties.load(localPropertiesFile.inputStream())
    }

    val appMetricaKey = localProperties.getProperty("APPMETRICA_API_KEY") ?: ""
    val yandexClientId = localProperties.getProperty("YANDEX_CLIENT_ID") ?: ""
    val mapkitApiKey = localProperties.getProperty("MAPKIT_API_KEY") ?: ""

    buildConfigField("String", "APPMETRICA_API_KEY", "\"$appMetricaKey\"")
    buildConfigField("String", "YANDEX_CLIENT_ID", "\"$yandexClientId\"")
    buildConfigField("String", "MAPKIT_API_KEY", "\"$mapkitApiKey\"")

    manifestPlaceholders["YANDEX_CLIENT_ID"] = yandexClientId
  }

  flavorDimensions += "tier"
  productFlavors {
    create("free") {
      dimension = "tier"
      // Remove suffix to match Yandex console ID
      buildConfigField("Boolean", "IS_PRO", "true") 
    }
    create("pro") {
      dimension = "tier"
      // Remove suffix to match Yandex console ID
      buildConfigField("Boolean", "IS_PRO", "true")
    }
  }

  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }

  packaging {
    jniLibs {
      useLegacyPackaging = true
    }
  }

  androidResources {
    noCompress += listOf("tflite", "txt")
  }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
}

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  // Modules
  implementation(project(":core"))
  implementation(project(":core:navigation"))
  implementation(project(":domain"))
  implementation(project(":data"))
  implementation(project(":feature-main"))
  implementation(project(":feature-statistics"))
  implementation(project(":feature-create-habit"))
  implementation(project(":feature-auth"))
  implementation(project(":feature-about"))
  implementation(project(":feature-profile"))
  implementation(project(":feature-debug"))
  implementation(project(":feature-detection"))

  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  implementation(libs.firebase.messaging)
  implementation(libs.firebase.analytics)
  implementation(libs.firebase.crashlytics)
  implementation(libs.firebase.config)
  implementation(libs.firebase.firestore)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  
  // AppMetrica
  implementation(libs.appmetrica.sdk)
  
  // Yandex Maps
  implementation(libs.yandex.maps.mobile)

  // Hilt
  implementation(libs.hilt.android)
  "ksp"(libs.hilt.compiler)

  // Room
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.room.ktx)
  "ksp"(libs.androidx.room.compiler)

  // WorkManager
  implementation(libs.androidx.work.runtime.ktx)
  implementation(libs.androidx.hilt.work)

  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
}
