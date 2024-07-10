package dev.schlaubi.tonbrett.cli.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.ui.TextStyle
import dev.schlaubi.tonbrett.cli.command.exitAfter
import dev.schlaubi.tonbrett.cli.commands.MainCommand
import dev.schlaubi.tonbrett.cli.components.Error
import dev.schlaubi.tonbrett.cli.components.Spacer
import dev.schlaubi.tonbrett.cli.util.getConfig
import dev.schlaubi.tonbrett.client.Tonbrett

@Composable
fun TonbrettCliApp(mainCommand: MainCommand) {
    val token = remember { getConfig().sessionToken }
    if (token == null) {
        Error {
            exitAfter(exitCode = 1)
            Text("You are currently not logged in, please log in using")
            Spacer()
            Text("tonbrett-cli login", textStyle = TextStyle.Bold)
        }
    } else {
        val api = remember { Tonbrett(token, mainCommand.apiUrl) }
        SoundList(api)
    }
}
