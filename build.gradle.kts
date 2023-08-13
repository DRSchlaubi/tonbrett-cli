import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.Executable
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.mosaic)
}

group = "dev.schlaubi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://europe-west3-maven.pkg.dev/mik-music/mikbot")
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
                baseName = "tonbrett-cli-macos-x64"
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json.okio)
                implementation(libs.kotlinx.datetime)
                implementation(libs.tonbrett.client)
                implementation(libs.okio)
                implementation(libs.clikt)
                implementation(libs.kotter)
            }
        }
    }
}
