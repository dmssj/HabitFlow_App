plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
}

android {
  namespace = "com.example.domain"
  compileSdk = 35

  defaultConfig {
    minSdk = 26
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}

dependencies {
  implementation("javax.inject:javax.inject:1")
  implementation(libs.kotlinx.coroutines.core)
  
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
}
