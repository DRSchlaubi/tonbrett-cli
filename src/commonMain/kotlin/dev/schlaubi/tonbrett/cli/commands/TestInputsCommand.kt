package dev.schlaubi.tonbrett.cli.commands

import androidx.compose.runtime.*
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.jakewharton.mosaic.ui.Text
import dev.schlaubi.tonbrett.cli.command.MosaicCommand
import dev.schlaubi.tonbrett.cli.io.Key
import dev.schlaubi.tonbrett.cli.io.LocalKeyEvents

class TestInputsCommand : MosaicCommand(help = "Test command to test internal input api") {

    private val removeChar by option(help = "Hides the character pressed")
        .flag(defaultForHelp = "Disabled")

    @Composable
    override fun Command() {
        var lastKey by remember { mutableStateOf<Key?>(null) }
        val keyEvents = LocalKeyEvents.current
        LaunchedEffect(Unit) {
            keyEvents.collect {
                lastKey = it
            }
        }

        Text("Last selected key: ${lastKey?.sanitize(removeChar)}")
    }
}

private fun Key.sanitize(removeChar: Boolean) = copy(asciiChar = if (removeChar) null else asciiChar)
