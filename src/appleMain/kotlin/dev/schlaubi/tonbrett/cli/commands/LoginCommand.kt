package dev.schlaubi.tonbrett.cli.commands

import platform.AppKit.NSWorkspace
import platform.Foundation.NSURL

actual fun openUrl(url: String) {
    val nsUrl = NSURL(string = url)
    val workspace = NSWorkspace.sharedWorkspace()
    workspace.openURL(nsUrl)
}
