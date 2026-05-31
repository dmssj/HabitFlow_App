pluginManagement {
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" }

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "HabitFlow"

include(":app")
include(":core")
include(":core:navigation")
include(":domain")
include(":data")
include(":feature-main")
include(":feature-statistics")
include(":feature-create-habit")
include(":feature-auth")
include(":feature-about")
include(":feature-profile")
include(":feature-debug")
include(":feature-detection")
