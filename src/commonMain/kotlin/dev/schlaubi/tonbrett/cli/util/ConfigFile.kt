package dev.schlaubi.tonbrett.cli.util

import kotlinx.cinterop.toKString
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.use
import platform.posix.getenv

private val fs = FileSystem.SYSTEM

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Config(@EncodeDefault(EncodeDefault.Mode.NEVER) val sessionToken: String? = null)

fun getAppDataFolder(): Path {
    val basePath = when(Platform.osFamily) {
        OsFamily.WINDOWS -> getenv("APPDATA")!!.toKString().toPath()
        OsFamily.MACOSX -> getenv("HOME")!!.toKString().toPath() / "Library" / "Application Support"
        else -> getenv("HOME")!!.toKString().toPath()
    }
    return basePath / "tonbrett-cli"
}

fun getConfigFile() = getAppDataFolder() / "config.json"

fun getConfig(): Config {
    val file = getConfigFile()
    return if (fs.exists(file)) {
        val string = fs.source(file).buffer().use {source ->
            source.readUtf8()
        }
        Json.decodeFromString<Config>(string)
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

    fs.sink(file, mustCreate = false).buffer().use { sink ->
        sink.writeUtf8(Json.encodeToString(config))
    }
}
