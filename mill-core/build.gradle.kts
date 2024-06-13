plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.androidLibrary)
}

version = libs.versions.mill.get()
description = "A tool for managing UI states in compose multiplatform "

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_17.toString()
            }
        }
        publishLibraryVariants("release")
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "mill_core"
        }
    }

    macosArm64()

    macosX64()

    jvm("desktop")

    js(IR) {
        moduleName = "mill-core"
        browser()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.activity.compose)
            }
        }

        val nonAndroidMain by creating {
            dependsOn(commonMain)
            kotlin {
                srcDir("src/nonAndroidMain/kotlin")
            }
        }

        names.forEach {
            if (!it.startsWith("common")
                && !it.startsWith("android")
                && !it.startsWith("nonAndroid")
                && !it.contains("test", ignoreCase = true)) {
                getByName(it).dependsOn(nonAndroidMain)
            }
        }
    }
}

android {
    namespace = "coder.stanley.mill.core"
    compileSdk = 34
    defaultConfig {
        minSdk = 23
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}
