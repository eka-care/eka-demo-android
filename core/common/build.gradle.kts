import java.util.Properties

plugins {
    id("kotlin-kapt")
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

val config =
    Properties().apply { load(project.rootProject.file("config.properties").inputStream()) }

android {
    namespace = "eka.dr.intl.common"
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
            buildConfigField(
                "String",
                "EKA_CARE_AUTH_GO_URL",
                "\"${config["EKA_CARE_AUTH_GO_URL"]}\""
            )
        }
        create("staging") {
            buildConfigField(
                "String",
                "EKA_CARE_AUTH_GO_URL",
                "\"${config["EKA_CARE_AUTH_GO_URL_DEV"]}\""
            )
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
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    api(project(":core:theme:icons"))
    api(project(":core:theme:typography"))
    api(project(":core:theme:ekatheme"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // navigation dependencies
    api(libs.androidx.navigation.ui.ktx)
    api(libs.androidx.navigation.compose)
    api(libs.google.gson)

    // room dependencies
    api(libs.room.runtime)
    api(libs.room.ktx)
    kapt(libs.room.compiler)

    // koin dependencies
    api(platform(libs.koin.bom))
    api(libs.koin.android)
    api(libs.koin.androidx.compose)
    api(libs.koin.core.coroutines)

    implementation(libs.kotlinx.serialization.json)
    api(libs.coil.compose)

    api(libs.voice2rx.sdk){
        exclude(group = "com.google.protobuf", module = "protobuf-java")
        exclude(group = "androidx.constraintlayout")
        exclude(group = "androidx.appcompat", module = "appcompat")
    }
    api(libs.accompanist.systemuicontroller)
    api(libs.compose.shimmer)
    api(libs.androidx.compose.material)
    api(libs.lottie.compose)
}