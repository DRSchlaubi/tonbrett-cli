package dev.schlaubi.tonbrett.cli.commands

import androidx.compose.runtime.Composable
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import dev.schlaubi.tonbrett.cli.command.MosaicCommand
import dev.schlaubi.tonbrett.cli.ui.TonbrettCliApp
import dev.schlaubi.tonbrett.cli.util.url
import io.ktor.http.*

private val defaultUrl = Url("https://musikus.gutikus.schlau.bi")

class MainCommand : MosaicCommand(name = "tonbrett-cli", invokeWithoutSubcommand = true) {
    val apiUrl by option(help = "The API url to communicate with")
        .url()
        .default(defaultUrl)

    @Composable
    override fun Command() {
        TonbrettCliApp(this)
    }
}
