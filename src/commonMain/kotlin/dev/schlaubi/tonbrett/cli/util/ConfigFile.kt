@file:OptIn(ExperimentalSerializationApi::class)

package dev.schlaubi.tonbrett.cli.util

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.io.decodeFromSource
import kotlinx.serialization.json.io.encodeToSink
import platform.posix.getenv
import kotlin.experimental.ExperimentalNativeApi

private val fs = SystemFileSystem

@Serializable
data class Config(@EncodeDefault(EncodeDefault.Mode.NEVER) val sessionToken: String? = null)

@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
fun getAppDataFolder(): Path {
    val basePath = when(Platform.osFamily) {
        OsFamily.WINDOWS -> Path(getenv("APPDATA")!!.toKString())
        OsFamily.MACOSX -> Path(getenv("HOME")!!.toKString(),  "Library", "Application Support")
        else -> Path(getenv("HOME")!!.toKString())
    }
    return Path(basePath, "tonbrett-cli")
}

fun getConfigFile() = Path(getAppDataFolder(), "config.json")

fun getConfig(): Config {
    val file = getConfigFile()
    return if (fs.exists(file)) {
        fs.source(file).buffered().use { source ->
            Json.decodeFromSource<Config>(source)
        }
    } else {
        Config()
    }
}

fun saveConfig(config: Config) {
    val file = getConfigFile()
    val parent = file.parent ?: return
    if (!fs.exists(parent)) {
        fs.createDirectories(parent, mustCreate = false)
    }

    fs.sink(file).buffered().use { sink ->
        Json.encodeToSink(config, sink)
    }
}
