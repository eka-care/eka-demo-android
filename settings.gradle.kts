pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        maven { url = uri("https://jitpack.io") }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("file:///${System.getProperty("user.home")}/.m2/repository") }
        gradlePluginPortal()
    }
}

rootProject.name = "Eka Care Intl"
include(":app")
include(":core")
include(":features")
include(":core:network")
include(":core:theme")
include(":features:patients")
include(":features:assistant")
include(":features:records")
include(":core:common")
include(":core:theme:ui")
include(":core:theme:ekatheme")
include(":core:theme:typography")
include(":core:theme:icons")
