plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.androidLibrary)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
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

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
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
        minSdk = 30
    }
}
