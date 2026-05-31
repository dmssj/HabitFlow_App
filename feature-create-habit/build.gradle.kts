plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.hilt)
}

android {
  namespace = "com.example.feature.create"
  compileSdk = 35

  defaultConfig {
    minSdk = 26
  }

  flavorDimensions += "tier"
  productFlavors {
    create("free") {
      dimension = "tier"
      buildConfigField("Boolean", "IS_PRO", "false")
    }
    create("pro") {
      dimension = "tier"
      buildConfigField("Boolean", "IS_PRO", "true")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  buildFeatures {
    buildConfig = true
  }
}

dependencies {
  implementation(project(":domain"))
  implementation(project(":core"))
  implementation(project(":core:navigation"))

  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinx.coroutines.android)

  // Hilt
  implementation(libs.hilt.android)
  "ksp"(libs.hilt.compiler)
}
