import java.util.Properties
import java.io.File

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val localProperties = Properties().apply {
    val localPropertiesFile = File(rootDir, "local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

// In Developer Mode these can be left blank/"0" — attestation isn't enforced
// until you distribute via a release channel (see Wearables Developer Center).
// Never hardcode real values here; set them via local.properties (gitignored)
// or CI secrets (MWDAT_APPLICATION_ID / MWDAT_CLIENT_TOKEN env vars).
val mwdatApplicationId: String =
    System.getenv("MWDAT_APPLICATION_ID") ?: (localProperties.getProperty("mwdat_application_id") ?: "0")
val mwdatClientToken: String =
    System.getenv("MWDAT_CLIENT_TOKEN") ?: (localProperties.getProperty("mwdat_client_token") ?: "0")

android {
    namespace = "com.example.myapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myapp"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        manifestPlaceholders["mwdat_application_id"] = mwdatApplicationId
        manifestPlaceholders["mwdat_client_token"] = mwdatClientToken
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    packaging {
        jniLibs {
            useLegacyPackaging = true
            keepDebugSymbols.add("**/*.so")
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    implementation("com.meta.wearable:mwdat-core:0.8.0")
    implementation("com.meta.wearable:mwdat-camera:0.8.0")
    implementation("com.meta.wearable:mwdat-display:0.8.0")
}
