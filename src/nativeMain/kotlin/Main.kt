package dev.schlaubi.tonbrett.cli

import com.github.ajalt.clikt.core.subcommands
import dev.schlaubi.tonbrett.cli.commands.LoginCommand
import dev.schlaubi.tonbrett.cli.commands.LogoutCommand
import dev.schlaubi.tonbrett.cli.commands.MainCommand
import dev.schlaubi.tonbrett.cli.commands.TestInputsCommand

fun main(args: Array<String>) = MainCommand()
    .subcommands(LoginCommand(), LogoutCommand(), TestInputsCommand())
    .main(args)
