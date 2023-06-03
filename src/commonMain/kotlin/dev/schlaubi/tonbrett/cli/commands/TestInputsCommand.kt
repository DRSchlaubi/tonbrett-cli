package dev.schlaubi.tonbrett.cli.commands

import androidx.compose.runtime.*
import com.jakewharton.mosaic.ui.Text
import dev.schlaubi.tonbrett.cli.command.MosaicCommand
import dev.schlaubi.tonbrett.cli.io.Key
import dev.schlaubi.tonbrett.cli.io.LocalKeyEvents

class TestInputsCommand : MosaicCommand(help = "Test command to test internal input api") {
    @Composable
    override fun Command() {
        var lastKey by remember { mutableStateOf<Key?>(null) }
        val keyEvents = LocalKeyEvents.current
        LaunchedEffect(Unit) {
            keyEvents.collect {
                lastKey = it
            }
        }

        Text("Last selected key: $lastKey")
    }
}
