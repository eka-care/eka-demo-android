import java.util.Properties

plugins {
    id("kotlin-kapt")
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

val config =
    Properties().apply { load(project.rootProject.file("config.properties").inputStream()) }

android {
    namespace = "eka.dr.intl.patients"
    compileSdk = 35

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "PARCHI_URL", "\"${config["PARCHI_URL"]}\"")
            buildConfigField("String", "MDR_URL", "\"${config["MDR_URL"]}\"")
            buildConfigField("String", "EKA_CARE_URL", "\"${config["EKA_CARE_URL"]}\"")
            buildConfigField("String", "ELIXIR_URL", "\"${config["ELIXIR_URL"]}\"")
            buildConfigField("String", "AUTH", "\"${config["EKA_CARE_AUTH_GO_URL"]}\"")
        }
        getByName("debug") {
            buildConfigField("String", "MDR_URL", "\"${config["MDR_URL"]}\"")
            buildConfigField("String", "PARCHI_URL", "\"${config["PARCHI_URL"]}\"")
            buildConfigField("String", "EKA_CARE_URL", "\"${config["EKA_CARE_URL"]}\"")
            buildConfigField("String", "ELIXIR_URL", "\"${config["ELIXIR_URL"]}\"")
            buildConfigField("String", "AUTH", "\"${config["EKA_CARE_AUTH_GO_URL"]}\"")
        }
    }
    kapt {
        correctErrorTypes = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {

    implementation(project(":core:network"))
    implementation(project(":core:common"))
    implementation(project(":core:theme:ui"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.room.paging)
    implementation(libs.androidx.work.runtime.ktx)
    api(libs.paging.compose)
    kapt(libs.room.compiler)

    implementation(libs.eka.health.records)
    implementation(libs.libphonenumber)
}