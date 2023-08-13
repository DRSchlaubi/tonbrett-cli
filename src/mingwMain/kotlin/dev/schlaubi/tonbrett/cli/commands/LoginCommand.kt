package dev.schlaubi.tonbrett.cli.commands

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toLong
import platform.windows.SW_SHOWNORMAL
import platform.windows.ShellExecuteW

@OptIn(ExperimentalForeignApi::class)
actual fun openUrl(url: String) = memScoped {
    val result = ShellExecuteW(null, "open", url, null, null, SW_SHOWNORMAL)
    if (result.toLong() <= 32) {
        error("Failed to open URL in the default browser.")
    }
}
