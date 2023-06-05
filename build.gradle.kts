import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.Executable
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
    id("com.jakewharton.mosaic") version "0.6.0"
}

group = "dev.schlaubi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://schlaubi.jfrog.io/artifactory/mikbot/")
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

    targets {
        withType<KotlinNativeTarget> {
            binaries {
                withType<Executable> {
                    entryPoint = "dev.schlaubi.tonbrett.cli.main"
                }
            }
        }
    }

    mingwX64 {
        binaries {
            executable {
                baseName = "tonbrett-cli-windows-x64"
            }
        }
    }
    macosArm64 {
        binaries {
            executable {
                baseName = "tonbrett-cli-maocs-arm64"
            }
        }
    }
    macosX64 {
        binaries {
            executable {
                baseName = "tonbrett-cli-maocs-x64"
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
                implementation("dev.schlaubi.tonbrett:client:1.8.8")
                implementation("com.squareup.okio:okio:3.3.0")
                implementation("com.github.ajalt.clikt:clikt:3.5.2")
            }
        }
    }
}
