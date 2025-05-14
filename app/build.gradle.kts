import java.util.Properties

plugins {
    id("kotlin-kapt")
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-parcelize")
}

val config =
    Properties().apply { load(project.rootProject.file("config.properties").inputStream()) }

android {
    namespace = "eka.dr.intl"
    compileSdk = 35

    signingConfigs {
        create("release") {
            storeFile = file("../ekadocintl.keystore")
            storePassword = "ekacare@svghacs"
            keyAlias = "ekacareintl"
            keyPassword = "ekacare@svghacs"
        }
    }

    defaultConfig {
        applicationId = "health.orbi.trykit"
        minSdk = 23
        targetSdk = 35
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
            buildConfigField(
                "String",
                "EKA_CARE_AUTH_GO_URL",
                "\"${config["EKA_CARE_AUTH_GO_URL"]}\""
            )
            buildConfigField(
                "String",
                "HUB_URL",
                "\"${config["HUB_URL"]}\""
            )
            buildConfigField("String", "NEEV_URL", "\"${config["NEEV_URL"]}\"")
            buildConfigField("String", "MDR_URL", "\"${config["MDR_URL"]}\"")
            buildConfigField("String", "EKA_CARE_URL", "\"${config["EKA_CARE_URL"]}\"")
            buildConfigField("String", "EKA_CARE_AUTH_GO_URL", "\"${config["EKA_CARE_AUTH_GO_URL"]}\"")
            buildConfigField("String", "DOC_WEB_URL", "\"${config["DOC_WEBVIEW_URL"]}\"")
            buildConfigField("String", "RECORDS_URL", "\"${config["RECORDS_URL"]}\"")
        }
        create("staging") {
            buildConfigField(
                "String",
                "EKA_CARE_AUTH_GO_URL",
                "\"${config["EKA_CARE_AUTH_GO_URL_DEV"]}\""
            )
            buildConfigField("String", "MDR_URL", "\"${config["MDR_URL_DEV"]}\"")
            buildConfigField("String", "EKA_CARE_URL", "\"${config["EKA_CARE_URL_DEV"]}\"")
            buildConfigField("String", "EKA_CARE_AUTH_GO_URL", "\"${config["EKA_CARE_AUTH_GO_URL_DEV"]}\"")
            buildConfigField(
                "String",
                "HUB_URL",
                "\"${config["HUB_URL"]}\""
            )
            buildConfigField("String", "NEEV_URL", "\"${config["NEEV_URL"]}\"")
            buildConfigField("String", "DOC_WEB_URL", "\"${config["DOC_WEBVIEW_URL_DEV"]}\"")
            buildConfigField("String", "RECORDS_URL", "\"${config["RECORDS_URL_DEV"]}\"")
        }
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            versionNameSuffix = ".debug"
            applicationIdSuffix = ".debug"
            manifestPlaceholders += listOf("branchioDebugMode" to "true")
            buildConfigField("String", "MDR_URL", "\"${config["MDR_URL"]}\"")
            buildConfigField("String", "EKA_CARE_URL", "\"${config["EKA_CARE_URL"]}\"")
            buildConfigField("String", "EKA_CARE_AUTH_GO_URL", "\"${config["EKA_CARE_AUTH_GO_URL"]}\"")
            buildConfigField(
                "String",
                "HUB_URL",
                "\"${config["HUB_URL"]}\""
            )
            buildConfigField("String", "NEEV_URL", "\"${config["NEEV_URL"]}\"")
            buildConfigField("String", "DOC_WEB_URL", "\"${config["DOC_WEBVIEW_URL"]}\"")
            buildConfigField("String", "RECORDS_URL", "\"${config["RECORDS_URL"]}\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kapt {
        correctErrorTypes = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {

    implementation(project(":features:patients"))
    implementation(project(":features:assistant"))
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":features:assistant"))
    implementation(project(":core:theme:ui"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.core.splashscreen)
    kapt(libs.room.compiler)
    implementation(libs.eka.health.records)
}