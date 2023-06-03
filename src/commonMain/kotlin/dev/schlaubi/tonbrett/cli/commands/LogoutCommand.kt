package dev.schlaubi.tonbrett.cli.commands

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.jakewharton.mosaic.ui.Text
import dev.schlaubi.tonbrett.cli.command.MosaicCommand
import dev.schlaubi.tonbrett.cli.components.Success
import dev.schlaubi.tonbrett.cli.util.Config
import dev.schlaubi.tonbrett.cli.util.saveConfig

class LogoutCommand : MosaicCommand(help = "Deletes authentication information from this machine") {

    @Composable
    override fun Command() {
        LaunchedEffect(Unit) {
            saveConfig(Config(null))
            exitProcess()
        }

        Success { Text("Successfully logged out") }
    }
}
