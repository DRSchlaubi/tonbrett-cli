package dev.schlaubi.tonbrett.cli.util

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import dev.schlaubi.tonbrett.cli.commands.MainCommand

val CliktCommand.mainCommand: MainCommand
    get() = currentContext.rootContext.command as MainCommand

private val Context.rootContext: Context
    get() = parent?.rootContext ?: this
