import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform") version "1.8.20"
    id("com.jakewharton.mosaic") version "0.6.0"
}

group = "dev.schlaubi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default {
        common {
            group("posix") {
                withApple()
                withLinux()
            }
        }
    }
    mingwX64()

    macosArm64()
    macosX64()

    targets {
        withType<KotlinNativeTarget> {
            binaries {
                executable {
                    entryPoint = "dev.schlaubi.tonbrett.cli.main"
                }
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
            }
        }
    }
}
