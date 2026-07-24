plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            freeCompilerArgs.addAll(
                "-opt-in=androidx.compose.foundation.style.ExperimentalFoundationStyleApi",
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
                "-opt-in=androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi",
                "-opt-in=androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi"
            )
        }
    }

    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "SharedApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
    implementation("androidx.palette:palette-ktx:1.0.0")

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            
            implementation(libs.kotlinx.serialization.json)
        }
        
        commonTest.dependencies {
    implementation("androidx.palette:palette-ktx:1.0.0")

            implementation(kotlin("test"))
        }

        androidMain.dependencies {
    implementation("androidx.palette:palette-ktx:1.0.0")

            implementation(libs.mwdat.core)
            implementation(libs.mwdat.camera)
            implementation(libs.mwdat.display)
            implementation(libs.mwdat.mockdevice)
            implementation(libs.androidx.xr.glimmer)
            implementation(libs.androidx.xr.projected)
            implementation(kotlin("reflect"))

            implementation(libs.androidx.foundation)
            implementation(libs.androidx.compose.material.icons.extended)

            implementation(libs.google.material)
            implementation(libs.google.play.services.auth)
            implementation(libs.androidx.identity)
            implementation(libs.androidx.identity.play)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.lifecycle.runtime.ktx)
            implementation(libs.androidx.activity.compose)
            
            implementation(libs.androidx.compose.ui.tooling.preview)
            
            implementation(libs.androidx.navigation3.runtime)
            implementation(libs.androidx.navigation3.ui)
            implementation(libs.androidx.lifecycle.viewmodel.navigation3)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.adaptive.navigation3)
            implementation(libs.androidx.compose.material3.adaptive)
            implementation(libs.androidx.compose.material3.adaptive.layout)
            implementation(libs.androidx.compose.material3.adaptive.navigation)
            implementation(libs.androidx.compose.material3.adaptive.navigation.suite)

            implementation(libs.spotify.auth)
            implementation(libs.retrofit.core)
            implementation(libs.retrofit.gson)
            implementation(libs.glide.core)
            implementation(libs.coil.compose)

            implementation(libs.androidx.camera.core)
            implementation(libs.androidx.camera.camera2)
            implementation(libs.androidx.camera.lifecycle)
            implementation(libs.androidx.camera.view)
            implementation(libs.androidx.camera.compose)
            implementation(libs.androidx.camera.extensions)

            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.firestore)
            implementation(libs.firebase.database)
            implementation(libs.firebase.auth)
            implementation(libs.firebase.crashlytics)

            implementation(libs.timber)
            implementation(libs.hilt.android)
            implementation(libs.androidx.appfunctions)
            implementation(libs.androidx.appfunctions.service)
            implementation("com.google.firebase:firebase-dataconnect:16.0.0-beta01")
            
            // Engage SDK & WorkManager
            implementation("com.google.android.engage:engage-core:1.6.0")
            implementation("androidx.work:work-runtime-ktx:2.9.0")
            
            // Media3
            implementation("androidx.media3:media3-exoplayer:1.2.0")
            implementation("androidx.media3:media3-session:1.2.0")
        }
    }
}

android {
    namespace = "com.musically.studio.shared"
    compileSdk = 37

    defaultConfig {
        minSdk = 34
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.palette:palette-ktx:1.0.0")

    add("kspAndroid", libs.hilt.compiler)
    add("kspAndroid", libs.androidx.appfunctions.compiler)
}
ksp {
    arg("appfunctions:aggregateAppFunctions", "true")
}
